package org.app.vestiobackend.security;

import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.service.AbstractServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserContextTest extends AbstractServiceTest {

    @Autowired
    private UserContext userContext;

    @Test
    public void getCurrentUserEmailShouldReturnEmailFromUserResolvedBySub() {
        setupSecurityContext(testUser.getCognitoSub(), testUser.getCognitoSub());
        String email = userContext.getCurrentUserEmail();
        assertThat(email).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void getCurrentUserEmailShouldReturnEmailFromStringPrincipal() {
        setupSecurityContextWithString(TEST_EMAIL);
        String email = userContext.getCurrentUserEmail();
        assertThat(email).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void getCurrentUserIdShouldReturnSubFromJwt() {
        String testSub = UUID.randomUUID().toString();
        setupSecurityContext(TEST_EMAIL, testSub);
        String userId = userContext.getCurrentUserId();
        assertThat(userId).isEqualTo(testSub);
    }

    @Test
    public void getClaimShouldReturnClaimValueFromJwt() {
        String testSub = UUID.randomUUID().toString();
        setupSecurityContext(TEST_EMAIL, testSub);
        String username = userContext.getClaim("username");
        assertThat(username).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void getCurrentUserShouldReturnUserEntity() {
        User user = userContext.getCurrentUser();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void getCurrentUserShouldThrowExceptionWhenUserNotFound() {
        setupSecurityContext(TEST_EMAIL, UUID.randomUUID().toString());
        assertThrows(ResourceNotFoundException.class, () -> userContext.getCurrentUser());
    }

    private void setupSecurityContextWithString(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
