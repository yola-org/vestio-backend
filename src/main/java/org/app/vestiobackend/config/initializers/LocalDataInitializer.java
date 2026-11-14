package org.app.vestiobackend.config.initializers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserStatusType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.List;
import java.util.UUID;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {

    private static final List<String> ACCOUNTS = List.of("admin", "admin2", "admin3", "admin4", "admin5");
    private static final String DEFAULT_PASSWORD = "!Password123";

    private final JdbcTemplate jdbcTemplate;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${cognito.user-pool-id}")
    private String userPoolId;

    @Override
    public void run(String... args) {
        for (String account : ACCOUNTS) {
            String email = account + "@example.com";
            String subject = ensureCognitoUser(email, account);
            int linked = jdbcTemplate.update("""
                    INSERT INTO users (id, email, first_name, last_name, cognito_sub, created_at, updated_at)
                    VALUES (?, ?, ?, 'Dev', ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    ON CONFLICT (email) DO UPDATE
                    SET cognito_sub = EXCLUDED.cognito_sub, updated_at = CURRENT_TIMESTAMP
                    WHERE users.cognito_sub IS NULL
                    """, UUID.randomUUID(), email, account, subject);
            if (linked == 0) {
                String existingSubject = jdbcTemplate.queryForObject(
                        "SELECT cognito_sub FROM users WHERE email = ?", String.class, email);
                if (!subject.equals(existingSubject)) {
                    throw new IllegalStateException("Dev user is linked to a different Cognito account: " + email);
                }
            }
        }
    }

    private String ensureCognitoUser(String email, String account) {
        AdminGetUserResponse user = getOrCreateCognitoUser(email, account);
        String subject = user.userAttributes().stream()
                .filter(attribute -> "sub".equals(attribute.name()))
                .map(AttributeType::value)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cognito subject missing for " + email));
        if (user.userStatus() == UserStatusType.FORCE_CHANGE_PASSWORD) {
            cognitoClient.adminSetUserPassword(request -> request.userPoolId(userPoolId)
                    .username(user.username())
                    .password(DEFAULT_PASSWORD)
                    .permanent(true));
        }
        return subject;
    }

    private AdminGetUserResponse getOrCreateCognitoUser(String email, String account) {
        try {
            return cognitoClient.adminGetUser(request -> request.userPoolId(userPoolId).username(email));
        } catch (UserNotFoundException ignored) {
            // Create the account below.
        }
        try {
            cognitoClient.adminCreateUser(request -> request.userPoolId(userPoolId)
                    .username(email)
                    .messageAction("SUPPRESS")
                    .userAttributes(
                            AttributeType.builder().name("email").value(email).build(),
                            AttributeType.builder().name("email_verified").value("true").build(),
                            AttributeType.builder().name("given_name").value(account).build(),
                            AttributeType.builder().name("family_name").value("Dev").build()));
        } catch (UsernameExistsException ignored) {
            // Another startup created the account after the lookup.
        }
        return cognitoClient.adminGetUser(request -> request.userPoolId(userPoolId).username(email));
    }
}
