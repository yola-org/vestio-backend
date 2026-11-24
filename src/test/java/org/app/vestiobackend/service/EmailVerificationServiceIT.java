package org.app.vestiobackend.service;

import org.app.vestiobackend.repository.PinRepository;
import org.app.vestiobackend.entity.PinPurpose;
import java.time.Instant;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import static org.mockito.Mockito.verifyNoInteractions;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.Pin;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.event.ApplicationEvents;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@Transactional
@RecordApplicationEvents
public class EmailVerificationServiceIT extends AbstractServiceIT {

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private AuthService authService;

    @Autowired private EmailVerificationService verification;
    @Autowired private PinRepository pins;
    @Autowired private EmailPinCodec codes;

    private User unverifiedUser() {
        User user = createTestUser("verify@example.com");
        user.setEmailVerified(false);
        userRepository.saveAndFlush(user);
        return user;
    }

    @Test
    void passwordResetPinCannotActivateAccount() {
        User user = unverifiedUser();
        Pin pin = new Pin();
        pin.setId(UUID.randomUUID());
        pin.setUserId(user.getId());
        pin.setPurpose(PinPurpose.PASSWORD_RESET);
        pin.setPinHash(codes.hash(pin.getId(), "123456"));
        pin.setCreatedAt(Instant.now());
        pin.setExpiresAt(Instant.now().plusSeconds(600));
        pins.saveAndFlush(pin);
        assertThrows(BadRequestException.class, () -> verification.confirm(user.getEmail(), "123456"));
        assertFalse(userRepository.findById(user.getId()).orElseThrow().isEmailVerified());
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void rejectsExpiredPinAndUnverifiedLogin() {
        User user = unverifiedUser();
        verification.issue(user);
        var event = applicationEvents.stream(VerificationPinCreated.class).findFirst().orElseThrow();
        var pin = pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.ACCOUNT_ACTIVATION).orElseThrow();
        pin.setExpiresAt(Instant.now().minusSeconds(1));
        pins.saveAndFlush(pin);
        assertThrows(BadRequestException.class, () -> verification.confirm(user.getEmail(), event.pin()));
        LoginRequestDto login = new LoginRequestDto();
        login.setEmail(user.getEmail());
        login.setPassword("Password123!");
        assertThrows(UnAuthorizedException.class, () -> authService.login(login));
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void limitsResendsAndLocksPinAfterFiveFailures() {
        User user = unverifiedUser();
        verification.issue(user);
        var event = applicationEvents.stream(VerificationPinCreated.class).findFirst().orElseThrow();
        assertThrows(BadRequestException.class, () -> verification.resend(user.getEmail()));
        String wrong = event.pin().equals("000000") ? "000001" : "000000";
        for (int i = 0; i < 5; i++) {
            assertThrows(BadRequestException.class, () -> verification.confirm(user.getEmail(), wrong));
        }
        assertEquals(5, pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.ACCOUNT_ACTIVATION).orElseThrow().getAttempts());
        assertThrows(BadRequestException.class, () -> verification.confirm(user.getEmail(), event.pin()));
        verifyNoInteractions(cognitoClient);
    }

}
