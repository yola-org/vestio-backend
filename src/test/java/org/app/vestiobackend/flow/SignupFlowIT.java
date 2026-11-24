package org.app.vestiobackend.flow;

import org.app.vestiobackend.service.AbstractServiceIT;
import org.app.vestiobackend.service.AuthService;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Uses real HTTP so container ERROR dispatches run through the security chain. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SignupFlowIT.FailingEndpoint.class)
class SignupFlowIT extends AbstractServiceIT {
    @Autowired private AuthService authService;
    @LocalServerPort int port;
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> signup(String email) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/auth/signup"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"firstName":"Test","lastName":"User","email":"%s","password":"Password123!"}
                        """.formatted(email))).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void cognitoOnlyDuplicateReturnsBadRequestWithoutCreatingLocalUser() throws Exception {
        when(cognitoClient.signUp(any(SignUpRequest.class)))
                .thenThrow(UsernameExistsException.builder().message("User already exists").build());
        var response = signup("cognito-only@example.com");
        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("ERR_EMAIL_ALREADY_ASSOCIATED_WITH_ANOTHER_ACCOUNT"));
        assertTrue(response.body().contains("This email is already associated with an account."));
        assertTrue(userRepository.findByEmail("cognito-only@example.com").isEmpty());
        verify(cognitoClient).signUp(any(SignUpRequest.class));
        verifyNoMoreInteractions(cognitoClient);
    }

    @Test
    void databaseDuplicateIsCaseInsensitiveAndNeverCallsCognito() throws Exception {
        for (String email : new String[]{TEST_EMAIL, TEST_EMAIL.toUpperCase()}) {
            var response = signup(email);
            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("ERR_EMAIL_ALREADY_ASSOCIATED_WITH_ANOTHER_ACCOUNT"));
        }
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void unexpectedErrorsKeepTheirServerStatusAndProtectedPathsStayProtected() throws Exception {
        var failure = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/test/unexpected-error"))
                .GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(500, failure.statusCode());
        for (String path : new String[]{"/error", "/api/profile"}) {
            var response = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                    .GET().build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @RestController
    static class FailingEndpoint {
        @GetMapping("/api/test/unexpected-error")
        public void fail() { throw new IllegalStateException("Test failure"); }
    }

    @Test
    @Transactional
    void signupUsesUserIdAsPrimaryKeyForAuthenticatedRequests() {
        String userId = UUID.randomUUID().toString();
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("Cognito");
        request.setLastName("User");
        request.setEmail("userId@example.com");
        request.setPassword("Password123!");
        when(cognitoClient.signUp(any(SignUpRequest.class))).thenReturn(SignUpResponse.builder().userSub(userId).build());

        authService.signup(request);
        setupSecurityContext(userId);
        UserContext context = new UserContext(userRepository);
        assertEquals(UUID.fromString(userId), context.getCurrentUser().getId());
        assertEquals("userId@example.com", context.getCurrentUserEmail());
    }
}
