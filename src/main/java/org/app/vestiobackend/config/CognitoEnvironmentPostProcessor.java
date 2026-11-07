package org.app.vestiobackend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the Cognito configuration into the Spring environment from AWS
 * Secrets Manager — the ONLY source of Cognito data. No Cognito environment
 * variables exist or are used.
 *
 * <p>If every required property ({@code cognito.client-id}, {@code cognito.client-secret},
 * {@code cognito.user-pool-id}, {@code cognito.issuer-uri}, {@code cognito.jwk-set-uri},
 * {@code cognito.region}) is already provided — e.g. from a profile-specific YAML with
 * literal values (tests) — the fetch is skipped entirely.</p>
 *
 * <p>Otherwise the JSON secret is fetched using the default AWS credentials
 * provider chain:</p>
 * <ul>
 *   <li>prod: the instance profile role ({@code spring.profiles.active=prod,jdbc})</li>
 *   <li>local: your AWS profile ({@code AWS_PROFILE=vestio-dev} or ~/.aws/credentials)</li>
 * </ul>
 *
 * <p>Secret JSON format (a single secret holds everything):</p>
 * <pre>{ "clientId": "...", "clientSecret": "...", "userPoolId": "...",
 *       "issuerUri": "...", "jwkSetUri": "...", "region": "ca-central-1" }</pre>
 *
 * <p>{@code issuerUri}/{@code jwkSetUri}/{@code region} may be omitted; they are derived
 * (from {@code region} + {@code userPoolId} / from {@code AWS_REGION} / ca-central-1).</p>
 *
 * <p>Secret name resolution: {@code cognito.secret-name} property if set (e.g. in a YAML),
 * otherwise {@code vestio/cognito-client-secret} when the active profiles contain
 * "prod", otherwise {@code vestio-dev/cognito-client-secret}.</p>
 */
public class CognitoEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(CognitoEnvironmentPostProcessor.class);

    private static final String CLIENT_ID = "cognito.client-id";
    private static final String CLIENT_SECRET = "cognito.client-secret";
    private static final String USER_POOL_ID = "cognito.user-pool-id";
    private static final String ISSUER_URI = "cognito.issuer-uri";
    private static final String JWK_SET_URI = "cognito.jwk-set-uri";
    private static final String REGION = "cognito.region";

    private static final List<String> REQUIRED_PROPERTIES = List.of(
            CLIENT_ID, CLIENT_SECRET, USER_POOL_ID, ISSUER_URI, JWK_SET_URI, REGION);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Nullable; injected only by tests. */
    private final SecretsManagerClient secretsClient;

    public CognitoEnvironmentPostProcessor() {
        this.secretsClient = null;
    }

    /** Test constructor — skips building a real client (no AWS calls). */
    CognitoEnvironmentPostProcessor(SecretsManagerClient secretsClient) {
        this.secretsClient = secretsClient;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        List<String> missing = REQUIRED_PROPERTIES.stream()
                .filter(key -> !StringUtils.hasText(environment.getProperty(key)))
                .toList();
        if (missing.isEmpty()) {
            log.debug("Cognito configuration fully provided; skipping Secrets Manager fetch.");
            return;
        }

        String secretName = environment.getProperty("cognito.secret-name", defaultSecretName());
        String region = environment.getProperty(REGION, awsRegion());
        try {
            String json = fetchSecret(secretName, region);
            Map<String, Object> mapped = mapSecret(json, region);

            Map<String, Object> resolved = new HashMap<>();
            for (String key : REQUIRED_PROPERTIES) {
                if (missing.contains(key) && mapped.get(key) != null) {
                    resolved.put(key, mapped.get(key));
                }
            }

            List<String> unresolved = missing.stream().filter(k -> !resolved.containsKey(k)).toList();
            if (!unresolved.isEmpty()) {
                throw new IllegalStateException(
                        "Secret '" + secretName + "' did not provide values for: " + unresolved
                                + ". Expected JSON keys: clientId, clientSecret, userPoolId, issuerUri, jwkSetUri, region.");
            }

            environment.getPropertySources().addLast(new MapPropertySource("cognitoSecrets", resolved));
            log.info("Loaded Cognito configuration from Secrets Manager secret '{}'.", secretName);
        } catch (Exception e) {
            log.warn("Could not load the Cognito configuration from Secrets Manager secret '{}': {}. "
                            + "Ensure AWS credentials are available (instance profile in prod, AWS profile locally), "
                            + "or provide all cognito.* properties directly.",
                    secretName, e.getMessage());
        }
    }

    private String defaultSecretName() {
        String profiles = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profiles == null) {
            profiles = System.getProperty("spring.profiles.active", "");
        }
        return profiles.toLowerCase().contains("prod")
                ? "vestio/cognito-client-secret"
                : "vestio-dev/cognito-client-secret";
    }

    private String awsRegion() {
        String region = System.getenv("AWS_REGION");
        if (StringUtils.hasText(region)) {
            return region;
        }
        region = System.getenv("AWS_DEFAULT_REGION");
        return StringUtils.hasText(region) ? region : "ca-central-1";
    }

    private String fetchSecret(String secretName, String region) {
        if (secretsClient != null) {
            return secretsClient.getSecretValue(
                    GetSecretValueRequest.builder().secretId(secretName).build()).secretString();
        }
        try (SecretsManagerClient client = SecretsManagerClient.builder().region(Region.of(region)).build()) {
            return client.getSecretValue(
                    GetSecretValueRequest.builder().secretId(secretName).build()).secretString();
        }
    }

    private Map<String, Object> mapSecret(String json, String defaultRegion) throws Exception {
        JsonNode node = MAPPER.readTree(json);
        Map<String, Object> out = new HashMap<>();

        putIfPresent(out, node, "clientId", CLIENT_ID);
        putIfPresent(out, node, "clientSecret", CLIENT_SECRET);
        putIfPresent(out, node, "userPoolId", USER_POOL_ID);
        putIfPresent(out, node, "issuerUri", ISSUER_URI);
        putIfPresent(out, node, "jwkSetUri", JWK_SET_URI);
        putIfPresent(out, node, "region", REGION);

        String region = (String) out.get(REGION);
        if (!StringUtils.hasText(region)) {
            region = defaultRegion;
            out.put(REGION, region);
        }

        // Derive issuer/JWKS from region + pool id when not present in the secret.
        if (!out.containsKey(ISSUER_URI) && out.containsKey(USER_POOL_ID)) {
            String issuer = "https://cognito-idp." + region + ".amazonaws.com/" + out.get(USER_POOL_ID);
            out.put(ISSUER_URI, issuer);
            if (!out.containsKey(JWK_SET_URI)) {
                out.put(JWK_SET_URI, issuer + "/.well-known/jwks.json");
            }
        }
        return out;
    }

    private void putIfPresent(Map<String, Object> out, JsonNode node, String field, String key) {
        JsonNode value = node.get(field);
        if (value != null && !value.isNull() && StringUtils.hasText(value.asText())) {
            out.put(key, value.asText());
        }
    }
}
