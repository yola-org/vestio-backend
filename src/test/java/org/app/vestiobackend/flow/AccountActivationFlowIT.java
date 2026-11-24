package org.app.vestiobackend.flow;

import org.app.vestiobackend.service.AbstractServiceIT;
import org.app.vestiobackend.service.AccountCreated;
import org.app.vestiobackend.service.EmailVerificationService;
import org.app.vestiobackend.service.VerificationPinCreated;

import org.app.vestiobackend.repository.PinRepository;
import org.app.vestiobackend.entity.PinPurpose;
import java.time.Instant;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.event.ApplicationEvents;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Transactional
@RecordApplicationEvents
public class AccountActivationFlowIT extends AbstractServiceIT {

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private EntityManager entityManager;

    @Autowired private EmailVerificationService verification;
    @Autowired private PinRepository pins;

    private User unverifiedUser() {
        User user = createTestUser("verify@example.com");
        user.setEmailVerified(false);
        userRepository.saveAndFlush(user);
        return user;
    }

    @Test
    void confirmsLatestPinAndDeletesAllPins() {
        User user = unverifiedUser();
        verification.issue(user);
        var old = applicationEvents.stream(VerificationPinCreated.class).findFirst().orElseThrow();
        var stored = pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.ACCOUNT_ACTIVATION).orElseThrow();
        assertNotEquals(old.pin(), stored.getPinHash());
        stored.setCreatedAt(Instant.now().minusSeconds(61));
        pins.saveAndFlush(stored);
        verification.resend(user.getEmail());
        var latest = applicationEvents.stream(VerificationPinCreated.class).reduce((a, b) -> b).orElseThrow();
        // The old PIN hash cannot validate against the latest row, even in the unlikely case codes repeat.
        assertNotEquals(old.id(), latest.id());
        if (!old.pin().equals(latest.pin())) {
            assertThrows(BadRequestException.class,
                    () -> verification.confirm(user.getEmail(), old.pin()));
        }
        verification.confirm(user.getEmail(), latest.pin());
        entityManager.flush();
        entityManager.clear();
        assertTrue(userRepository.findById(user.getId()).orElseThrow().isEmailVerified());
        assertTrue(pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(user.getId(), PinPurpose.ACCOUNT_ACTIVATION).isEmpty());
        verify(cognitoClient).adminConfirmSignUp(any(AdminConfirmSignUpRequest.class));
        verify(cognitoClient).adminUpdateUserAttributes(any(AdminUpdateUserAttributesRequest.class));
        assertEquals(1, applicationEvents.stream(AccountCreated.class).count());
    }

}
