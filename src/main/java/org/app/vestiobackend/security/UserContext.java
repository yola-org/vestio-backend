package org.app.vestiobackend.security;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Component providing access to the current authenticated user's context.
 * It abstracts the retrieval of user information from the security context,
 * handling different types of authentication principals (e.g., JWT from Cognito).
 */
@Component
@RequiredArgsConstructor
public class UserContext {

    private final UserRepository userRepository;

    /**
     * Retrieves the email of the currently authenticated user.
     * Supports both {@link Jwt} and {@link String} principal types.
     *
     * @return The email address of the current user.
     * @throws RuntimeException if the principal type is unknown.
     */
    public String getCurrentUserEmail() {
        Object principal = getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            String email = jwt.getClaimAsString("email");
            if (email != null) {
                return email.toLowerCase();
            }
            // Fallback to 'username' claim (Cognito Access Tokens)
            String username = jwt.getClaimAsString("username");
            if (username != null) {
                return username.toLowerCase();
            }
            // Fallback to subject
            String sub = jwt.getSubject();
            return sub != null ? sub.toLowerCase() : null;
        } else if (principal instanceof String) {
            return ((String) principal).toLowerCase();
        }

        // Final fallback to authentication name
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return auth.getName().toLowerCase();
        }

        throw new RuntimeException("Unknown principal type: " + (principal != null ? principal.getClass() : "null"));
    }

    /**
     * Retrieves the unique identifier (sub) of the currently authenticated user from the JWT.
     *
     * @return The user ID (sub claim) or null if the principal is not a JWT.
     */
    public String getCurrentUserId() {
        Object principal = getPrincipal();
        if (principal instanceof Jwt) {
            // Cognito uses 'sub' for the user ID
            return ((Jwt) principal).getClaimAsString("sub");
        }
        return null;
    }

    /**
     * Retrieves a specific claim from the current user's JWT.
     *
     * @param claimName The name of the claim to retrieve.
     * @return The claim value as a String, or null if the principal is not a JWT.
     */
    public String getClaim(String claimName) {
        Object principal = getPrincipal();
        if (principal instanceof Jwt) {
            return ((Jwt) principal).getClaimAsString(claimName);
        }
        return null;
    }

    /**
     * Internal helper to safely retrieve the principal from the security context.
     *
     * @return The principal object or null if no authentication is present.
     */
    private Object getPrincipal() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Retrieves the {@link User} entity corresponding to the currently authenticated user.
     *
     * @return The User entity from the database.
     * @throws ResourceNotFoundException if the user is not found in the local database.
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "User not found"));
    }
}
