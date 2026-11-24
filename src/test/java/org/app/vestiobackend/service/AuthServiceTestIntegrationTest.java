package org.app.vestiobackend.service;

import org.app.vestiobackend.repository.PinRepository;
import org.app.vestiobackend.entity.PinPurpose;
import java.time.Instant;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import static org.mockito.Mockito.verifyNoInteractions;
import org.app.vestiobackend.security.UserContext;
import org.app.vestiobackend.dto.JwtResponseDto;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.Pin;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.event.ApplicationEvents;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import jakarta.persistence.EntityManager;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Transactional
@RecordApplicationEvents
public class AuthServiceTestIntegrationTest extends AbstractServiceTest {

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private AuthService authService;

    @Autowired
    private EntityManager entityManager;

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

    @Test
    public void testSignup() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane.doe@example.com");
        request.setPassword("Password123!");
        String userId = UUID.randomUUID().toString();
        when(cognitoClient.signUp(any(SignUpRequest.class)))
                .thenReturn(SignUpResponse.builder().userSub(userId).build());

        authService.signup(request);

        // Verify Cognito call
        verify(cognitoClient).signUp(any(SignUpRequest.class));

        // Verify DB record
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(userId, user.getId().toString());
        VerificationPinCreated created = applicationEvents.stream(VerificationPinCreated.class).findFirst().orElseThrow();
        assertFalse(user.isEmailVerified());
        assertEquals(request.getEmail(), created.email());
        assertTrue(created.pin().matches("[0-9]{6}"));
    }

    @Test
    public void signupWithExistingEmailShouldThrowException() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(TEST_EMAIL); // Already exists from AbstractServiceTest
        request.setPassword("Password123!");

        assertThrows(BadRequestException.class, () -> authService.signup(request));
        assertEquals(0, applicationEvents.stream(AccountCreated.class).count());
    }

    @Test
    public void testLogin() {
        String email = "jane.doe@example.com";
        User user = createTestUser(email);

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail(email);
        request.setPassword("Password123!");

        AdminInitiateAuthResponse response = AdminInitiateAuthResponse.builder()
                .authenticationResult(AuthenticationResultType.builder()
                        .idToken("test-id-token")
                        .accessToken("test-access-token")
                        .refreshToken("test-refresh-token")
                        .expiresIn(3600)
                        .tokenType("Bearer")
                        .build())
                .build();

        when(cognitoClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class))).thenReturn(response);

        JwtResponseDto result = authService.login(request);

        assertEquals("test-id-token", result.getIdToken());
        assertEquals("test-access-token", result.getAccessToken());
        assertEquals("test-refresh-token", result.getRefreshToken());
        assertEquals(new JwtResponseDto.DisplayName(user.getFirstName(), user.getLastName(), user.getProfileImageKey()), result.getDisplayName());
        verify(cognitoClient).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
        entityManager.clear();
        assertEquals(user.getId(), userRepository.findByEmail(email).orElseThrow().getId());
        verifyNoMoreInteractions(cognitoClient);
    }

    @Test
    public void refreshShouldExchangeTheTokenWithCognito() {
        GetTokensFromRefreshTokenRequest request = GetTokensFromRefreshTokenRequest.builder()
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .refreshToken("original-refresh")
                .build();
        when(cognitoClient.getTokensFromRefreshToken(request))
                .thenReturn(GetTokensFromRefreshTokenResponse.builder()
                        .authenticationResult(AuthenticationResultType.builder()
                                .accessToken("refreshed-access")
                                .idToken("refreshed-id")
                                .refreshToken("rotated-refresh")
                                .expiresIn(3600)
                                .tokenType("Bearer")
                                .build())
                        .build());

        JwtResponseDto result = authService.refresh("original-refresh");

        assertEquals("refreshed-access", result.getAccessToken());
        assertEquals("refreshed-id", result.getIdToken());
        assertEquals("rotated-refresh", result.getRefreshToken());
    }

    @Test
    public void refreshShouldRejectAnInvalidCognitoToken() {
        when(cognitoClient.getTokensFromRefreshToken(any(GetTokensFromRefreshTokenRequest.class)))
                .thenThrow(NotAuthorizedException.builder().build());

        assertThrows(UnAuthorizedException.class, () -> authService.refresh("expired-refresh"));
    }

    @Test
    public void loginWithNonExistentEmailShouldThrowException() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("nonexistent@example.com");
        request.setPassword("Password123!");

        assertThrows(UnAuthorizedException.class, () -> authService.login(request));
    }

    @Test
    void rejectsInvalidCredentialsForAnExistingUser() {
        when(cognitoClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class)))
                .thenThrow(NotAuthorizedException.builder().build());
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail(TEST_EMAIL);
        request.setPassword("invalid-password");

        assertThrows(UnAuthorizedException.class, () -> authService.login(request));

        verify(cognitoClient).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
        verifyNoMoreInteractions(cognitoClient);
    }

    @Test
    void signupUsesUserIdAsPrimaryKeyForAuthenticatedRequests() {
        String userId = UUID.randomUUID().toString();
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("Cognito");
        request.setLastName("User");
        request.setEmail("userId@example.com");
        request.setPassword("Password123!");
        when(cognitoClient.signUp(any(SignUpRequest.class))).thenReturn(SignUpResponse.builder().userSub(userId).build());

        authService.signup(request);
        setupSecurityContext(userId);
        UserContext context = new UserContext(userRepository);
        assertEquals(UUID.fromString(userId), context.getCurrentUser().getId());
        assertEquals("userId@example.com", context.getCurrentUserEmail());
    }
}
