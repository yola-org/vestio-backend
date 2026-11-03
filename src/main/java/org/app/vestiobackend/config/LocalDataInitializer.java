package org.app.vestiobackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.repository.UserRepository;
import org.app.vestiobackend.repository.ListingRepository;
import org.app.vestiobackend.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import javax.sql.DataSource;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
@Order(2)
public class LocalDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final DataSource dataSource;
    private final CognitoIdentityProviderClient cognitoClient;
    private final AuthService authService;

    @Value("${cognito.user-pool-id}")
    private String userPoolId;

    @Value("${cognito.client-id}")
    private String clientId;

    @Value("${cognito.client-secret}")
    private String clientSecret;

    @Override
    public void run(String... args) throws Exception {
        log.info("[INITIALIZATION] Starting LocalDataInitializer (Order 2) - loading local_test_data.sql");
        if (userRepository.count() > 0 || listingRepository.count() > 0) {
            log.info("Local data already present, skipping local test data initialization.");
            return;
        }

        log.info("Initializing local test data from local_test_data.sql...");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("sql/local_test_data.sql"));
        populator.execute(dataSource);
        
        log.info("Local test data DB initialization completed. Syncing users with Cognito...");
        syncUsersWithCognito();
        
        log.info("[INITIALIZATION] LocalDataInitializer completed. Users: {}, Listings: {}", userRepository.count(), listingRepository.count());
    }

    private void syncUsersWithCognito() {
        List<User> users = userRepository.findAll();
        String defaultPassword = "Password123!";

        for (User user : users) {
            try {
                log.debug("Creating user {} in Cognito...", user.getEmail());
                
                AdminCreateUserRequest createRequest = AdminCreateUserRequest.builder()
                        .userPoolId(userPoolId)
                        .username(user.getEmail())
                        .userAttributes(
                                AttributeType.builder().name("email").value(user.getEmail()).build(),
                                AttributeType.builder().name("email_verified").value("true").build(),
                                AttributeType.builder().name("given_name").value(user.getFirstName()).build(),
                                AttributeType.builder().name("family_name").value(user.getLastName()).build()
                        )
                        .messageAction("SUPPRESS")
                        .build();

                cognitoClient.adminCreateUser(createRequest);

                AdminSetUserPasswordRequest setPasswordRequest = AdminSetUserPasswordRequest.builder()
                        .userPoolId(userPoolId)
                        .username(user.getEmail())
                        .password(defaultPassword)
                        .permanent(true)
                        .build();

                cognitoClient.adminSetUserPassword(setPasswordRequest);
                log.info("User {} created and password set in Cognito.", user.getEmail());

            } catch (UsernameExistsException e) {
                log.info("User {} already exists in Cognito. Resetting password...", user.getEmail());
                AdminSetUserPasswordRequest setPasswordRequest = AdminSetUserPasswordRequest.builder()
                        .userPoolId(userPoolId)
                        .username(user.getEmail())
                        .password(defaultPassword)
                        .permanent(true)
                        .build();

                cognitoClient.adminSetUserPassword(setPasswordRequest);
                log.info("Password reset for existing user {} in Cognito.", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to sync user {} with Cognito: {}", user.getEmail(), e.getMessage());
            }
        }
    }
}
