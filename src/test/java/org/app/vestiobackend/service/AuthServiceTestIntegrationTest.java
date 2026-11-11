package org.app.vestiobackend.service;

import org.app.vestiobackend.dto.JwtResponseDto;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.http.errors.exception.ResourceAlreadyExistsException;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import jakarta.persistence.EntityManager;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class AuthServiceTestIntegrationTest extends AbstractServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testSignup() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane.doe@example.com");
        request.setPassword("Password123!");
        String sub = UUID.randomUUID().toString();
        when(cognitoClient.signUp(any(SignUpRequest.class)))
                .thenReturn(SignUpResponse.builder().userSub(sub).build());

        authService.signup(request);

        // Verify Cognito call
        verify(cognitoClient).signUp(any(SignUpRequest.class));

        // Verify DB record
        User user = userRepository.findByCognitoSub(sub).orElseThrow();
        assertEquals(request.getEmail(), user.getEmail());
        assertNotEquals(sub, user.getId().toString());
    }

    @Test
    public void signupWithExistingEmailShouldThrowException() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(TEST_EMAIL); // Already exists from AbstractServiceTest
        request.setPassword("Password123!");

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.signup(request));
    }

    @Test
    public void testLogin() {
        String email = "jane.doe@example.com";
        User user = createTestUser(email);
        user.setCognitoSub(null);
        userRepository.saveAndFlush(user);
        String sub = UUID.randomUUID().toString();

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail(email);
        request.setPassword("Password123!");

        AdminInitiateAuthResponse response = AdminInitiateAuthResponse.builder()
                .authenticationResult(AuthenticationResultType.builder()
                        .idToken("test-id-token")
                        .accessToken("test-access-token")
                        .expiresIn(3600)
                        .tokenType("Bearer")
                        .build())
                .build();

        when(cognitoClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class))).thenReturn(response);
        when(cognitoClient.getUser(GetUserRequest.builder().accessToken("test-access-token").build()))
                .thenReturn(GetUserResponse.builder()
                        .userAttributes(AttributeType.builder().name("sub").value(sub).build())
                        .build());

        JwtResponseDto result = authService.login(request);

        assertEquals("test-id-token", result.getIdToken());
        assertEquals("test-access-token", result.getAccessToken());
        verify(cognitoClient).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
        entityManager.clear();
        assertEquals(user.getId(), userRepository.findByCognitoSub(sub).orElseThrow().getId());
    }

    @Test
    public void loginWithNonExistentEmailShouldThrowException() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("nonexistent@example.com");
        request.setPassword("Password123!");

        assertThrows(UnAuthorizedException.class, () -> authService.login(request));
    }
}
