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
        setupSecurityContext(testSub);
        String userId = userContext.getCurrentUserId();
        assertThat(userId).isEqualTo(testSub);
    }

    @Test
    public void getClaimShouldReturnClaimValueFromJwt() {
        String testSub = UUID.randomUUID().toString();
        setupSecurityContext(testSub);
        String username = userContext.getClaim("username");
        assertThat(username).isEqualTo(testSub);
    }

    @Test
    public void getCurrentUserShouldReturnUserEntity() {
        User user = userContext.getCurrentUser();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void getCurrentUserShouldThrowExceptionWhenUserNotFound() {
        setupSecurityContext(UUID.randomUUID().toString());
        assertThrows(ResourceNotFoundException.class, () -> userContext.getCurrentUser());
    }

    @Test
    void resolvesStringPrincipalByNormalizedEmailWithoutJwtClaims() {
        setupSecurityContextWithString("TEST@EXAMPLE.COM");

        assertThat(userContext.getCurrentUser().getId()).isEqualTo(testUser.getId());
        assertThat(userContext.getCurrentUserId()).isNull();
        assertThat(userContext.getClaim("username")).isNull();
    }

    @Test
    void fallsBackToAuthenticationNameForAnUnrecognizedPrincipal() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new Object());
        when(authentication.getName()).thenReturn("TEST@EXAMPLE.COM");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(userContext.getCurrentUserEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void cannotResolveEmailWithoutAuthentication() {
        SecurityContextHolder.clearContext();

        assertThat(userContext.getCurrentUserId()).isNull();
        assertThat(assertThrows(RuntimeException.class, userContext::getCurrentUserEmail))
                .hasMessage("Unknown principal type: null");
    }

    @Test
    void rejectsJwtWithBlankSubject() {
        setupSecurityContext(" ");

        assertThrows(ResourceNotFoundException.class, userContext::getCurrentUser);
    }

    private void setupSecurityContextWithString(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
