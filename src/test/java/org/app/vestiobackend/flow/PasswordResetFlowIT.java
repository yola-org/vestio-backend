package org.app.vestiobackend.flow;

import org.app.vestiobackend.service.AbstractServiceIT;
import org.app.vestiobackend.service.EmailPinCodec;
import org.app.vestiobackend.service.PasswordChanged;
import org.app.vestiobackend.service.PasswordResetPinCreated;
import org.app.vestiobackend.service.PasswordResetService;

import org.app.vestiobackend.entity.Pin;
import org.app.vestiobackend.entity.PinPurpose;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.repository.PinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUserGlobalSignOutRequest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RecordApplicationEvents
class PasswordResetFlowIT extends AbstractServiceIT {
    private static final String NEW_PASSWORD = "NewPassword123!";
    @Autowired PasswordResetService resets;
    @Autowired PinRepository pins;
    @Autowired ApplicationEvents events;
    @Autowired EmailPinCodec codes;

    private PasswordResetPinCreated latestEvent() {
        return events.stream(PasswordResetPinCreated.class).reduce((first, last) -> last).orElseThrow();
    }

    private Pin latestPin() {
        return pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(testUser.getId(), PinPurpose.PASSWORD_RESET).orElseThrow();
    }

    @Test
    void resetsWithLatestCodeAndDeletesOnlyResetPins() {
        // Different purposes must remain independent, even for the same user.
        Pin activation = new Pin();
        activation.setId(UUID.randomUUID());
        activation.setUserId(testUser.getId());
        activation.setPurpose(PinPurpose.ACCOUNT_ACTIVATION);
        activation.setPinHash(codes.hash(activation.getId(), "111111"));
        activation.setCreatedAt(Instant.now());
        activation.setExpiresAt(Instant.now().plusSeconds(600));
        pins.saveAndFlush(activation);

        resets.request(TEST_EMAIL.toUpperCase());
        var old = latestEvent();
        var stored = latestPin();
        assertNotEquals(old.pin(), stored.getPinHash());
        stored.setCreatedAt(Instant.now().minusSeconds(61));
        pins.saveAndFlush(stored);
        resets.request(TEST_EMAIL);
        var latest = latestEvent();
        assertNotEquals(old.pin(), latest.pin());
        assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, old.pin(), NEW_PASSWORD));
        resets.reset(TEST_EMAIL, latest.pin(), NEW_PASSWORD);

        verify(cognitoClient).adminSetUserPassword(AdminSetUserPasswordRequest.builder()
                .userPoolId("ca-central-1_test").username(TEST_EMAIL).password(NEW_PASSWORD).permanent(true).build());
        verify(cognitoClient, timeout(2000)).adminUserGlobalSignOut(any(AdminUserGlobalSignOutRequest.class));
        assertNotNull(userRepository.findById(testUser.getId()).orElseThrow().getPasswordChangedAt());
        assertTrue(pins.findFirstByUserIdAndPurposeOrderByCreatedAtDesc(testUser.getId(), PinPurpose.PASSWORD_RESET).isEmpty());
        assertTrue(pins.findById(activation.getId()).isPresent());
        assertEquals(latest.id(), events.stream(PasswordChanged.class).findFirst().orElseThrow().resetId());
        assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, latest.pin(), NEW_PASSWORD));
    }

}
