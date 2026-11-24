package org.app.vestiobackend.security;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class ApiAuthenticationInterceptor implements HandlerInterceptor {

    private final UserRepository users;

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private final BearerTokenAuthenticationEntryPoint entryPoint = new BearerTokenAuthenticationEntryPoint();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        boolean authRequired = method.hasMethodAnnotation(AuthRequired.class)
                || AnnotatedElementUtils.hasAnnotation(method.getBeanType(), AuthRequired.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authRequired || (trustResolver.isAuthenticated(authentication) && isCurrentSession(authentication))) {
            return true;
        }

        entryPoint.commence(request, response, null);
        return false;
    }

    private boolean isCurrentSession(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) return true;
        UUID id;
        try {
            id = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException | NullPointerException invalidSubject) {
            return false;
        }
        return users.findById(id).map(user -> {
            if (user.getPasswordChangedAt() == null) return true;
            Instant authenticatedAt = jwt.hasClaim("auth_time") ? jwt.getClaimAsInstant("auth_time") : jwt.getIssuedAt();
            return authenticatedAt != null && authenticatedAt.isAfter(user.getPasswordChangedAt());
        }).orElse(true);
    }
}
