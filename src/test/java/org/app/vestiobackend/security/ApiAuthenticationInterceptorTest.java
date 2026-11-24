package org.app.vestiobackend.security;

import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.method.HandlerMethod;

import java.time.Instant;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiAuthenticationInterceptorTest {
    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @AuthRequired public void protectedEndpoint() { }

    private boolean allow(Instant issuedAt, Instant authenticatedAt, Instant changedAt) throws Exception {
        var id = UUID.randomUUID();
        var users = mock(UserRepository.class);
        var user = User.builder().id(id).passwordChangedAt(changedAt).build();
        when(users.findById(id)).thenReturn(Optional.of(user));
        var jwt = Jwt.withTokenValue("token").header("alg", "RS256").subject(id.toString()).issuedAt(issuedAt)
                .claim("auth_time", authenticatedAt.getEpochSecond()).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
        var response = new MockHttpServletResponse();
        boolean allowed = new ApiAuthenticationInterceptor(users).preHandle(new MockHttpServletRequest(), response,
                new HandlerMethod(this, getClass().getMethod("protectedEndpoint")));
        if (!allowed) assertEquals(401, response.getStatus());
        return allowed;
    }

    @Test void rejectsOldAndRefreshedSessionsAfterReset() throws Exception {
        var resetAt = Instant.parse("2026-11-24T12:00:00Z");
        assertFalse(allow(resetAt.minusSeconds(60), resetAt.minusSeconds(60), resetAt));
        assertFalse(allow(resetAt.plusSeconds(60), resetAt.minusSeconds(60), resetAt));
    }

    @Test void acceptsNewLoginAndAccountsWithoutPasswordChanges() throws Exception {
        var resetAt = Instant.parse("2026-11-24T12:00:00Z");
        assertTrue(allow(resetAt.plusSeconds(1), resetAt.plusSeconds(1), resetAt));
        assertTrue(allow(resetAt, resetAt, null));
    }
}
