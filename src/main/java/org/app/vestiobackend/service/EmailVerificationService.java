package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.entity.*;
import org.app.vestiobackend.repository.*;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final UserRepository users;
    private final PinRepository pins;
    private final EmailPinCodec codes;
    private final ApplicationEventPublisher events;
    private final CognitoIdentityProviderClient cognito;
    @Value("${cognito.user-pool-id}") private String poolId;

    public void issue(User user) {
        Instant now = Instant.now();
        var latest = pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.ACCOUNT_ACTIVATION);
        if (latest.isPresent() && latest.get().getCreatedAt().plusSeconds(60).isAfter(now)) {
            throw new BadRequestException(ErrorCode.ERR_VERIFICATION_RATE_LIMITED, "Please wait one minute before requesting another PIN.");
        }
        String code = codes.generate();
        Pin pin = new Pin();
        pin.setId(UUID.randomUUID());
        pin.setUserId(user.getId());
        pin.setPurpose(PinPurpose.ACCOUNT_ACTIVATION);
        pin.setPinHash(codes.hash(pin.getId(), code));
        pin.setCreatedAt(now);
        pin.setExpiresAt(now.plusSeconds(600));
        pins.save(pin);
        events.publishEvent(new VerificationPinCreated(pin.getId(), user.getEmail(), code));
    }

    @Transactional
    public void resend(String email) {
        users.lockByEmail(email).filter(user -> !user.isEmailVerified()).ifPresent(this::issue);
    }

    // Commit failed attempts too, so retrying cannot reset the five-attempt limit.
    @Transactional(noRollbackFor = BadRequestException.class)
    public void confirm(String email, String code) {
        User user = users.lockByEmail(email).orElseThrow(this::invalid);
        if (user.isEmailVerified()) return;
        Pin pin = pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.ACCOUNT_ACTIVATION).orElseThrow(this::invalid);
        if (!pin.getExpiresAt().isAfter(Instant.now()) || pin.getAttempts() >= 5) throw invalid();
        pin.setAttempts(pin.getAttempts() + 1);
        if (!codes.matches(pin.getId(), pin.getPinHash(), code)) throw invalid();
        try {
            cognito.adminConfirmSignUp(AdminConfirmSignUpRequest.builder().userPoolId(poolId).username(user.getEmail()).build());
        } catch (NotAuthorizedException alreadyConfirmed) {
            // Recover if Cognito succeeded but the previous database commit failed.
            var remote = cognito.adminGetUser(AdminGetUserRequest.builder().userPoolId(poolId).username(user.getEmail()).build());
            if (remote.userStatus() != UserStatusType.CONFIRMED) throw alreadyConfirmed;
        }
        cognito.adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder().userPoolId(poolId).username(user.getEmail())
                .userAttributes(AttributeType.builder().name("email_verified").value("true").build()).build());
        user.setEmailVerified(true);
        pins.deleteByUserIdAndPurpose(user.getId(), PinPurpose.ACCOUNT_ACTIVATION);
        events.publishEvent(new AccountCreated(user.getId(), user.getEmail(), user.getFirstName()));
    }

    private BadRequestException invalid() {
        return new BadRequestException(ErrorCode.ERR_INVALID_VERIFICATION_PIN, "Invalid or expired PIN. Use the latest email or request another PIN.");
    }
}
