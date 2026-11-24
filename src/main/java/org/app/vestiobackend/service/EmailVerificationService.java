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
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository users;
    private final EmailVerificationPinRepository pins;
    private final ApplicationEventPublisher events;
    private final CognitoIdentityProviderClient cognito;
    @Value("${cognito.client-secret}") private String secret;
    @Value("${cognito.user-pool-id}") private String poolId;

    public void issue(User user) {
        Instant now = Instant.now();
        var latest = pins.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
        if (latest.isPresent() && latest.get().getCreatedAt().plusSeconds(60).isAfter(now)) {
            throw new BadRequestException(ErrorCode.ERR_VERIFICATION_RATE_LIMITED, "Please wait one minute before requesting another PIN.");
        }
        String code = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
        EmailVerificationPin pin = new EmailVerificationPin();
        pin.setId(UUID.randomUUID());
        pin.setUserId(user.getId());
        pin.setPinHash(hash(pin.getId(), code));
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
        EmailVerificationPin pin = pins.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).orElseThrow(this::invalid);
        if (!pin.getExpiresAt().isAfter(Instant.now()) || pin.getAttempts() >= 5) throw invalid();
        pin.setAttempts(pin.getAttempts() + 1);
        if (!MessageDigest.isEqual(pin.getPinHash().getBytes(StandardCharsets.UTF_8), hash(pin.getId(), code).getBytes(StandardCharsets.UTF_8))) throw invalid();
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
        pins.deleteByUserId(user.getId());
        events.publishEvent(new AccountCreated(user.getId(), user.getEmail(), user.getFirstName()));
    }

    private BadRequestException invalid() {
        return new BadRequestException(ErrorCode.ERR_INVALID_VERIFICATION_PIN, "Invalid or expired PIN. Use the latest email or request another PIN.");
    }

    private String hash(UUID id, String code) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal((id + ":" + code).getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException failure) {
            throw new IllegalStateException("Cannot hash verification PIN", failure);
        }
    }
}
