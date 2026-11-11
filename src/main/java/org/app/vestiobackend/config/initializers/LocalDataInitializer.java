package org.app.vestiobackend.config.initializers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.repository.UserRepository;
import org.app.vestiobackend.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserStatusType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import javax.sql.DataSource;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
@Order(2)
public class LocalDataInitializer implements CommandLineRunner {

    private static final List<String> SEED_EMAILS = List.of(
            "local@example.com", "seller1@example.com", "seller2@example.com",
            "expert@example.com", "casual@example.com");
    private static final String DEFAULT_PASSWORD = "Password123!";

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final DataSource dataSource;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${cognito.user-pool-id}")
    private String userPoolId;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0 && listingRepository.count() == 0) {
            log.info("Loading local test data from local_test_data.sql.");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("sql/local_test_data.sql"));
            populator.execute(dataSource);
        }

        for (User user : userRepository.findAllByEmailIn(SEED_EMAILS)) {
            try {
                syncUserWithCognito(user);
            } catch (RuntimeException e) {
                throw new IllegalStateException("Failed to sync local Cognito user " + user.getEmail(), e);
            }
        }
        log.info("Local data initialized. Users: {}, Listings: {}", userRepository.count(), listingRepository.count());
    }

    private void syncUserWithCognito(User user) {
        String username;
        List<AttributeType> attributes;
        boolean needsPassword;
        try {
            UserType createdUser = cognitoClient.adminCreateUser(AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(user.getEmail())
                    .userAttributes(
                            AttributeType.builder().name("email").value(user.getEmail()).build(),
                            AttributeType.builder().name("email_verified").value("true").build(),
                            AttributeType.builder().name("given_name").value(user.getFirstName()).build(),
                            AttributeType.builder().name("family_name").value(user.getLastName()).build())
                    .messageAction("SUPPRESS")
                    .build()).user();
            username = createdUser.username();
            attributes = createdUser.attributes();
            needsPassword = true;
        } catch (UsernameExistsException e) {
            AdminGetUserResponse existingUser = cognitoClient.adminGetUser(AdminGetUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(user.getEmail())
                    .build());
            username = existingUser.username();
            attributes = existingUser.userAttributes();
            needsPassword = existingUser.userStatus() == UserStatusType.FORCE_CHANGE_PASSWORD;
        }

        String sub = attributes.stream()
                .filter(attribute -> "sub".equals(attribute.name()))
                .map(AttributeType::value)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cognito did not return a subject."));

        if (needsPassword) {
            cognitoClient.adminSetUserPassword(AdminSetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .password(DEFAULT_PASSWORD)
                    .permanent(true)
                    .build());
        }
        if (!sub.equals(user.getCognitoSub())) {
            user.setCognitoSub(sub);
            userRepository.save(user);
        }
    }
}
