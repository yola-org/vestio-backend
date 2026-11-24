package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.entity.Pin;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.PinPurpose;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.repository.PinRepository;
import org.app.vestiobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUserGlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.PasswordHistoryPolicyViolationException;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository users;
    private final PinRepository pins;
    private final EmailPinCodec codes;
    private final CognitoIdentityProviderClient cognito;
    private final ApplicationEventPublisher events;
    @Value("${cognito.user-pool-id}") private String poolId;

    @Transactional
    public void request(String email) {
        User user = existingUser(email);
        if (!user.isEmailVerified()) return;
        Instant now = Instant.now();
        var latest = pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.PASSWORD_RESET);
        if (latest.isPresent() && latest.get().getCreatedAt().plusSeconds(60).isAfter(now)) return;
        String code;
        do {
            code = codes.generate();
        } while (latest.isPresent() && codes.matches(latest.get().getId(), latest.get().getPinHash(), code));
        Pin pin = new Pin();
        pin.setId(UUID.randomUUID());
        pin.setUserId(user.getId());
        pin.setPurpose(PinPurpose.PASSWORD_RESET);
        pin.setPinHash(codes.hash(pin.getId(), code));
        pin.setCreatedAt(now);
        pin.setExpiresAt(now.plusSeconds(600));
        pins.save(pin);
        events.publishEvent(new PasswordResetPinCreated(pin.getId(), user.getEmail(), code));
    }

    @Transactional(noRollbackFor = BadRequestException.class)
    public void reset(String email, String code, String newPassword) {
        var user = existingUser(email);
        if (!user.isEmailVerified()) throw invalid();
        var pin = pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.PASSWORD_RESET).orElseThrow(this::invalid);
        if (!pin.getExpiresAt().isAfter(Instant.now()) || pin.getAttempts() >= 5) throw invalid();
        if (!codes.matches(pin.getId(), pin.getPinHash(), code)) {
            pin.setAttempts(pin.getAttempts() + 1);
            throw invalid();
        }
        try {
            cognito.adminSetUserPassword(AdminSetUserPasswordRequest.builder().userPoolId(poolId)
                    .username(user.getEmail()).password(newPassword).permanent(true).build());
        } catch (UserNotFoundException failure) {
            throw emailNotFound();
        } catch (InvalidPasswordException | PasswordHistoryPolicyViolationException failure) {
            throw new BadRequestException(ErrorCode.ERR_PASSWORD_POLICY, "Choose a new password that meets the password policy and has not been used recently.");
        }
        user.setPasswordChangedAt(Instant.now());
        pins.deleteByUserIdAndPurpose(user.getId(), PinPurpose.PASSWORD_RESET);
        events.publishEvent(new PasswordChanged(user.getId(), user.getEmail(), pin.getId()));
    }

    // Finish revocation before returning the successful reset response.
    @TransactionalEventListener
    public void revokeSessions(PasswordChanged event) {
        try {
            cognito.adminUserGlobalSignOut(AdminUserGlobalSignOutRequest.builder()
                    .userPoolId(poolId).username(event.email()).build());
        } catch (Exception failure) {
            // The database session cutoff still rejects old JWTs, including refreshed tokens with old auth_time.
            log.error("Cognito session revocation failed for account {} ({})", event.userId(), failure.getClass().getSimpleName());
        }
    }

    private User existingUser(String email) {
        return users.lockByEmail(email.strip()).orElseThrow(this::emailNotFound);
    }

    private BadRequestException emailNotFound() {
        return new BadRequestException(ErrorCode.ERR_EMAIL_NOT_FOUND, "Your email wasn't found.");
    }

    private BadRequestException invalid() {
        return new BadRequestException(ErrorCode.ERR_INVALID_RESET_PIN, "Invalid or expired reset PIN. Use the latest email or request another PIN.");
    }
}
