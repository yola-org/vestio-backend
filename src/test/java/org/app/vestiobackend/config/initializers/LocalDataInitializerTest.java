package org.app.vestiobackend.config.initializers;

import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.repository.ListingRepository;
import org.app.vestiobackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserStatusType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocalDataInitializerTest {

    @Test
    void linksNewAndExistingSeedUsersToTheirCognitoSubjects() {
        UserRepository users = mock(UserRepository.class);
        CognitoIdentityProviderClient cognito = mock(CognitoIdentityProviderClient.class);
        DataSource dataSource = mock(DataSource.class);
        LocalDataInitializer initializer = new LocalDataInitializer(
                users, mock(ListingRepository.class), dataSource, cognito);
        ReflectionTestUtils.setField(initializer, "userPoolId", "test-pool");

        User newUser = User.builder().email("local@example.com").firstName("Local").lastName("User").build();
        User existingUser = User.builder().email("seller1@example.com").firstName("Amina").lastName("Sellers").build();
        String newSub = UUID.randomUUID().toString();
        String existingSub = UUID.randomUUID().toString();

        when(users.count()).thenReturn(2L);
        when(users.findAllByEmailIn(List.of("local@example.com", "seller1@example.com",
                "seller2@example.com", "expert@example.com", "casual@example.com")))
                .thenReturn(List.of(newUser, existingUser));
        when(cognito.adminCreateUser(any(AdminCreateUserRequest.class))).thenAnswer(invocation -> {
            AdminCreateUserRequest request = invocation.getArgument(0);
            if (existingUser.getEmail().equals(request.username())) {
                throw UsernameExistsException.builder().build();
            }
            assertThat(request.username()).isEqualTo(newUser.getEmail());
            return AdminCreateUserResponse.builder().user(UserType.builder()
                    .username(newSub)
                    .attributes(AttributeType.builder().name("sub").value(newSub).build())
                    .build()).build();
        });
        when(cognito.adminGetUser(AdminGetUserRequest.builder()
                .userPoolId("test-pool").username(existingUser.getEmail()).build()))
                .thenReturn(AdminGetUserResponse.builder()
                        .username(existingSub)
                        .userStatus(UserStatusType.CONFIRMED)
                        .userAttributes(AttributeType.builder().name("sub").value(existingSub).build())
                        .build());

        initializer.run();

        assertThat(newUser.getCognitoSub()).isEqualTo(newSub);
        assertThat(existingUser.getCognitoSub()).isEqualTo(existingSub);
        verify(users).save(newUser);
        verify(users).save(existingUser);
        verify(cognito).adminSetUserPassword(any(AdminSetUserPasswordRequest.class));
        verify(cognito).adminSetUserPassword(argThat((AdminSetUserPasswordRequest request) ->
                request.permanent() && newSub.equals(request.username())));
    }
}
