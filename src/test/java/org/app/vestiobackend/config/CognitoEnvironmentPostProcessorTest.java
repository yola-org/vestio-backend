package org.app.vestiobackend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Verifies Cognito configuration loading from AWS Secrets Manager:
 * pre-provided properties win, missing ones are filled from the JSON secret,
 * and issuer/JWKS are derived from region + pool id when absent.
 */
class CognitoEnvironmentPostProcessorTest {

    private static final String FULL_JSON = """
            {
              "clientId": "client-123",
              "clientSecret": "secret-456",
              "userPoolId": "ca-central-1_TestPool",
              "issuerUri": "https://cognito-idp.ca-central-1.amazonaws.com/ca-central-1_TestPool",
              "jwkSetUri": "https://cognito-idp.ca-central-1.amazonaws.com/ca-central-1_TestPool/.well-known/jwks.json",
              "region": "ca-central-1"
            }
            """;

    private static final String MINIMAL_JSON = """
            {
              "clientId": "client-123",
              "clientSecret": "secret-456",
              "userPoolId": "ca-central-1_TestPool"
            }
            """;

    private SecretsManagerClient secretsClient;

    @BeforeEach
    void setUp() {
        secretsClient = mock(SecretsManagerClient.class);
    }

    private void stubSecret(String json) {
        when(secretsClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(GetSecretValueResponse.builder().secretString(json).build());
    }

    private MockEnvironment fullyProvidedEnvironment() {
        return new MockEnvironment()
                .withProperty("cognito.issuer-uri", "provided-issuer")
                .withProperty("cognito.jwk-set-uri", "provided-jwks")
                .withProperty("cognito.client-id", "provided-client")
                .withProperty("cognito.client-secret", "provided-secret")
                .withProperty("cognito.user-pool-id", "provided-pool")
                .withProperty("cognito.region", "us-east-1");
    }

    @Test
    @DisplayName("All cognito.* properties provided -> Secrets Manager is never called")
    void allProvidedSkipsSecretsManager() {
        MockEnvironment env = fullyProvidedEnvironment();
        when(secretsClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenThrow(new AssertionError("Secrets Manager must not be called"));

        new CognitoEnvironmentPostProcessor(secretsClient).postProcessEnvironment(env, null);

        assertFalse(env.getPropertySources().stream()
                .anyMatch(ps -> "cognitoSecrets".equals(ps.getName())));
        assertEquals("provided-client", env.getProperty("cognito.client-id"));
        verify(secretsClient, times(0)).getSecretValue(any(GetSecretValueRequest.class));
    }

    @Test
    @DisplayName("No cognito.* properties -> full config comes from the Secrets Manager JSON secret")
    void missingPropertiesFallBackToSecretsManager() {
        stubSecret(FULL_JSON);
        MockEnvironment env = new MockEnvironment();

        new CognitoEnvironmentPostProcessor(secretsClient).postProcessEnvironment(env, null);

        assertEquals("client-123", env.getProperty("cognito.client-id"));
        assertEquals("secret-456", env.getProperty("cognito.client-secret"));
        assertEquals("ca-central-1_TestPool", env.getProperty("cognito.user-pool-id"));
        assertEquals("https://cognito-idp.ca-central-1.amazonaws.com/ca-central-1_TestPool",
                env.getProperty("cognito.issuer-uri"));
        assertEquals("https://cognito-idp.ca-central-1.amazonaws.com/ca-central-1_TestPool/.well-known/jwks.json",
                env.getProperty("cognito.jwk-set-uri"));
        assertEquals("ca-central-1", env.getProperty("cognito.region"));
    }

    @Test
    @DisplayName("Issuer/JWKS are derived from region + pool id when the secret omits them")
    void derivesIssuerAndJwksFromPoolId() {
        stubSecret(MINIMAL_JSON);
        MockEnvironment env = new MockEnvironment();

        new CognitoEnvironmentPostProcessor(secretsClient).postProcessEnvironment(env, null);

        assertEquals("https://cognito-idp.ca-central-1.amazonaws.com/ca-central-1_TestPool",
                env.getProperty("cognito.issuer-uri"));
        assertEquals("https://cognito-idp.ca-central-1.amazonaws.com/ca-central-1_TestPool/.well-known/jwks.json",
                env.getProperty("cognito.jwk-set-uri"));
        assertEquals("ca-central-1", env.getProperty("cognito.region"));
    }

    @Test
    @DisplayName("Uses cognito.secret-name property when configured")
    void usesConfiguredSecretName() {
        stubSecret(FULL_JSON);
        MockEnvironment env = new MockEnvironment()
                .withProperty("cognito.secret-name", "/custom/secret");

        new CognitoEnvironmentPostProcessor(secretsClient).postProcessEnvironment(env, null);

        assertEquals("client-123", env.getProperty("cognito.client-id"));
        assertEquals("ca-central-1_TestPool", env.getProperty("cognito.user-pool-id"));
    }

    @Test
    @DisplayName("Partially provided -> provided values win, the secret fills the rest")
    void providedPropertiesWinOverSecret() {
        stubSecret(FULL_JSON);
        MockEnvironment env = new MockEnvironment()
                .withProperty("cognito.client-id", "provided-wins");

        new CognitoEnvironmentPostProcessor(secretsClient).postProcessEnvironment(env, null);

        assertEquals("provided-wins", env.getProperty("cognito.client-id"));
        assertEquals("secret-456", env.getProperty("cognito.client-secret"));
        assertEquals("ca-central-1_TestPool", env.getProperty("cognito.user-pool-id"));
    }

    @Test
    @DisplayName("Invalid/absent secret -> warns and leaves properties unresolved (no exception)")
    void invalidSecretWarnsButDoesNotThrow() {
        when(secretsClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(GetSecretValueResponse.builder().secretString("not-json").build());

        MockEnvironment env = new MockEnvironment();
        assertDoesNotThrow(() -> new CognitoEnvironmentPostProcessor(secretsClient)
                .postProcessEnvironment(env, null));
        assertNull(env.getProperty("cognito.client-id"));
    }
}
