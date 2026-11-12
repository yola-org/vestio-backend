package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.JwtResponseDto;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.InternalException;
import org.app.vestiobackend.http.errors.exception.ResourceAlreadyExistsException;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.app.vestiobackend.mapper.UserMapper;
import org.app.vestiobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for user authentication and account management.
 * Integrates with AWS Cognito for identity management and maintains local user records.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CognitoIdentityProviderClient cognitoClient;
    private final UserMapper userMapper;

    @Value("${cognito.client-id}")
    private String clientId;

    @Value("${cognito.client-secret}")
    private String clientSecret;

    @Value("${cognito.token-expiration-seconds:86400}")
    private int tokenExpirationSeconds;

    @Value("${spring.profiles.active:default}")
    private String activeProfiles;

    /**
     * Checks if the 'local' profile is currently active.
     *
     * @return true if the local profile is active, false otherwise.
     */
    private boolean isLocalProfileActive() {
        if (activeProfiles == null) return false;
        return Arrays.asList(activeProfiles.split(",")).contains("local");
    }

    /**
     * Registers a new user.
     * Creates the user in AWS Cognito and saves a corresponding user record in the local database.
     *
     * @param request The signup request containing user details and password.
     * @throws ResourceAlreadyExistsException if the email is already registered in the local database.
     */
    @Transactional
    public void signup(SignupRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException(ErrorCode.ERR_EMAIL_ALREADY_ASSOCIATED_WITH_ANOTHER_ACCOUNT, "This email is already associated with an account.");
        }

        // 1. Create user in Cognito
        AttributeType emailAttr = AttributeType.builder().name("email").value(request.getEmail()).build();

        String secretHash = calculateSecretHash(clientId, clientSecret, request.getEmail());

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .clientId(clientId)
                .secretHash(secretHash)
                .username(request.getEmail())
                .password(request.getPassword())
                .userAttributes(emailAttr)
                .build();

        SignUpResponse response = cognitoClient.signUp(signUpRequest);

        // 2. Save user in local DB
        User user = userMapper.signupRequestToUser(request);
        user.setId(UUID.randomUUID());
        user.setCognitoSub(response.userSub());

        userRepository.save(user);
    }

    /**
     * Authenticates a user and returns a set of JWT tokens.
     * Uses Cognito's ADMIN_NO_SRP_AUTH flow.
     *
     * @param request The login request containing email and password.
     * @return The JWT response containing ID, access, and refresh tokens.
     * @throws UnAuthorizedException if the user is not found in the local database or credentials are invalid.
     */
    public JwtResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnAuthorizedException("User not found or invalid credentials."));

        String secretHash = calculateSecretHash(clientId, clientSecret, request.getEmail());

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .clientId(clientId)
                .userPoolId(getUserPoolIdFromConfig())
                .authParameters(Map.of(
                        "USERNAME", request.getEmail(),
                        "PASSWORD", request.getPassword(),
                        "SECRET_HASH", secretHash
                ))
                .build();

        try {
            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);
            AuthenticationResultType result = authResponse.authenticationResult();
            if (user.getCognitoSub() == null) {
                linkExistingUser(user, result.accessToken());
            }

            return toJwtResponse(result);
        } catch (UserNotFoundException | NotAuthorizedException e) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "User not found or invalid credentials.");
        }
    }

    public JwtResponseDto refresh(String refreshToken) {
        GetTokensFromRefreshTokenRequest request = GetTokensFromRefreshTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .refreshToken(refreshToken)
                .build();

        try {
            return toJwtResponse(cognitoClient.getTokensFromRefreshToken(request).authenticationResult());
        } catch (NotAuthorizedException | UserNotFoundException | RefreshTokenReuseException e) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Invalid or expired refresh token.");
        }
    }

    private JwtResponseDto toJwtResponse(AuthenticationResultType result) {
        return JwtResponseDto.builder()
                .accessToken(result.accessToken())
                .idToken(result.idToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn().longValue())
                .tokenType(result.tokenType())
                .build();
    }

    private void linkExistingUser(User user, String accessToken) {
        GetUserResponse cognitoUser = cognitoClient.getUser(GetUserRequest.builder()
                .accessToken(accessToken)
                .build());
        String cognitoSub = cognitoUser.userAttributes().stream()
                .filter(attribute -> "sub".equals(attribute.name()))
                .map(AttributeType::value)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElseThrow(() -> new InternalException(ErrorCode.ERR_INTERNAL_SERVER_ERROR,
                        "Cognito user is missing its subject."));

        if (userRepository.linkCognitoSub(user.getId(), cognitoSub) == 0) {
            throw new UnAuthorizedException("User not found or invalid credentials.");
        }
    }

    @Value("${cognito.user-pool-id}")
    private String userPoolId;

    /**
     * Retrieves the Cognito user pool ID from the configuration.
     *
     * @return The user pool ID.
     */
    private String getUserPoolIdFromConfig() {
        return userPoolId;
    }

    /**
     * Calculates the HMAC-SHA256 secret hash required by AWS Cognito for clients with a secret.
     *
     * @param userPoolClientId     The Cognito client ID.
     * @param userPoolClientSecret The Cognito client secret.
     * @param userName             The username (email).
     * @return The base64 encoded secret hash.
     * @throws InternalException if an error occurs during hash calculation.
     */
    public String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new InternalException(ErrorCode.ERR_INTERNAL_SERVER_ERROR, "Error calculating secret hash", e);
        }
    }
}
