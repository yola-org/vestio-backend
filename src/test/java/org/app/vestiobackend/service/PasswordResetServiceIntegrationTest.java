package org.app.vestiobackend.service;

import org.app.vestiobackend.entity.Pin;
import org.app.vestiobackend.entity.PinPurpose;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.repository.PinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUserGlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RecordApplicationEvents
class PasswordResetServiceIntegrationTest extends AbstractServiceTest {
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
    void missingCognitoAccountReturnsEmailNotFoundWithoutChangingLocalAccount() {
        resets.request(TEST_EMAIL);
        var event = latestEvent();
        when(cognitoClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
                .thenThrow(UserNotFoundException.builder().build());
        var failure = assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, event.pin(), NEW_PASSWORD));
        assertEquals("ERR_EMAIL_NOT_FOUND", failure.getErrorCode().name());
        assertEquals(0, latestPin().getAttempts());
        assertNull(userRepository.findById(testUser.getId()).orElseThrow().getPasswordChangedAt());
        assertEquals(0, events.stream(PasswordChanged.class).count());
    }

    @Test
    void missingEmailReturnsSpecificBadRequestAndNeverCreatesPins() {
        var requestFailure = assertThrows(BadRequestException.class, () -> resets.request("missing@example.com"));
        assertEquals("ERR_EMAIL_NOT_FOUND", requestFailure.getErrorCode().name());
        var resetFailure = assertThrows(BadRequestException.class, () -> resets.reset("missing@example.com", "123456", NEW_PASSWORD));
        assertEquals("ERR_EMAIL_NOT_FOUND", resetFailure.getErrorCode().name());
        assertEquals(0, events.stream(PasswordResetPinCreated.class).count());
        assertTrue(pins.findAll().isEmpty());
        verifyNoInteractions(cognitoClient);
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

    @Test
    void activationCodeCannotResetPassword() {
        Pin activation = new Pin();
        activation.setId(UUID.randomUUID());
        activation.setUserId(testUser.getId());
        activation.setPurpose(PinPurpose.ACCOUNT_ACTIVATION);
        activation.setPinHash(codes.hash(activation.getId(), "123456"));
        activation.setCreatedAt(Instant.now());
        activation.setExpiresAt(Instant.now().plusSeconds(600));
        pins.saveAndFlush(activation);
        assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, "123456", NEW_PASSWORD));
        assertEquals(0, pins.findById(activation.getId()).orElseThrow().getAttempts());
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void expiredCodeAndFiveCommittedWrongAttemptsCannotResetPassword() {
        resets.request(TEST_EMAIL);
        var event = latestEvent();
        var pin = latestPin();
        pin.setExpiresAt(Instant.now().minusSeconds(1));
        pins.saveAndFlush(pin);
        assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, event.pin(), NEW_PASSWORD));
        pin.setExpiresAt(Instant.now().plusSeconds(600));
        pins.saveAndFlush(pin);
        String wrong = event.pin().equals("000000") ? "000001" : "000000";
        for (int i = 1; i <= 5; i++) {
            assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, wrong, NEW_PASSWORD));
            assertEquals(i, latestPin().getAttempts());
        }
        assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, event.pin(), NEW_PASSWORD));
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void cooldownAndUnverifiedAccountsDoNotIssueExtraCodes() {
        resets.request(TEST_EMAIL);
        resets.request(TEST_EMAIL);
        var unverified = createTestUser("unverified-reset@example.com");
        unverified.setEmailVerified(false);
        userRepository.saveAndFlush(unverified);
        resets.request(unverified.getEmail());
        assertEquals(1, events.stream(PasswordResetPinCreated.class).count());
        assertEquals(1, pins.findAll().stream().filter(pin -> pin.getPurpose() == PinPurpose.PASSWORD_RESET).count());
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void providerFailureLeavesCodeAvailableAndAccountUnchanged() {
        resets.request(TEST_EMAIL);
        var event = latestEvent();
        when(cognitoClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
                .thenThrow(CognitoIdentityProviderException.builder().message("Unavailable").build())
                .thenReturn(AdminSetUserPasswordResponse.builder().build());
        assertThrows(CognitoIdentityProviderException.class, () -> resets.reset(TEST_EMAIL, event.pin(), NEW_PASSWORD));
        assertNotNull(latestPin());
        assertNull(userRepository.findById(testUser.getId()).orElseThrow().getPasswordChangedAt());
        assertEquals(0, events.stream(PasswordChanged.class).count());
        resets.reset(TEST_EMAIL, event.pin(), NEW_PASSWORD);
        verify(cognitoClient, timeout(2000)).adminUserGlobalSignOut(any(AdminUserGlobalSignOutRequest.class));
    }

    @Test
    void passwordPolicyFailureDoesNotConsumeValidCode() {
        resets.request(TEST_EMAIL);
        var event = latestEvent();
        when(cognitoClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
                .thenThrow(InvalidPasswordException.builder().build());
        var failure = assertThrows(BadRequestException.class, () -> resets.reset(TEST_EMAIL, event.pin(), NEW_PASSWORD));
        assertEquals("ERR_PASSWORD_POLICY", failure.getErrorCode().name());
        assertEquals(0, latestPin().getAttempts());
        assertNull(userRepository.findById(testUser.getId()).orElseThrow().getPasswordChangedAt());
    }
}
