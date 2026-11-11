package org.app.vestiobackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class ApiAuthenticationInterceptor implements HandlerInterceptor {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private final BearerTokenAuthenticationEntryPoint entryPoint = new BearerTokenAuthenticationEntryPoint();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        boolean authRequired = method.hasMethodAnnotation(AuthRequired.class)
                || AnnotatedElementUtils.hasAnnotation(method.getBeanType(), AuthRequired.class);
        if (!authRequired || trustResolver.isAuthenticated(SecurityContextHolder.getContext().getAuthentication())) {
            return true;
        }

        entryPoint.commence(request, response, null);
        return false;
    }
}
