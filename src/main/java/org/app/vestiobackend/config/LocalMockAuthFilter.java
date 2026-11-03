package org.app.vestiobackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * For local development, if an Authorization header starts with "Bearer mock-local-jwt-token-for-",
 * we bypass JWT validation and set the email in the SecurityContext.
 */
@Component
@Profile("local")
public class LocalMockAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer mock-local-jwt-token-for-")) {
            String email = header.replace("Bearer mock-local-jwt-token-for-", "").trim().toLowerCase();
            if (!email.isEmpty()) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                // Wrap request to hide Authorization header from downstream JWT filters
                HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return null;
                        }
                        return super.getHeader(name);
                    }

                    @Override
                    public java.util.Enumeration<String> getHeaders(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return Collections.emptyEnumeration();
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public java.util.Enumeration<String> getHeaderNames() {
                        List<String> names = Collections.list(super.getHeaderNames());
                        names.removeIf(n -> n.equalsIgnoreCase("Authorization"));
                        return Collections.enumeration(names);
                    }
                };
                filterChain.doFilter(wrappedRequest, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
