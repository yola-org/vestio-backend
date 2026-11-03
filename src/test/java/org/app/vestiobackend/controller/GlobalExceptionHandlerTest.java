package org.app.vestiobackend.controller;

import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.ResourceAlreadyExistsException;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.app.vestiobackend.service.AuthService;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest extends AbstractControllerTest {

    @MockBean
    private AuthService authService;

    @Test
    public void handleResourceAlreadyExistsException() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("already@exists.com");
        request.setPassword("Password123!");

        doThrow(new ResourceAlreadyExistsException(ErrorCode.ERR_EMAIL_ALREADY_ASSOCIATED_WITH_ANOTHER_ACCOUNT, "Email exists"))
                .when(authService).signup(any());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ErrorCode.ERR_EMAIL_ALREADY_ASSOCIATED_WITH_ANOTHER_ACCOUNT.name()))
                .andExpect(jsonPath("$.message").value("Email exists"));
    }

    @Test
    public void handleValidationException() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        // Missing required fields if validation is present (assuming @Valid is used in controller)

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    public void handleResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Not found"))
                .when(authService).login(any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"pass\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.ERR_RESOURCE_NOT_FOUND.name()));
    }

    @Test
    public void handleUnAuthorizedException() throws Exception {
        doThrow(new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Unauthorized"))
                .when(authService).login(any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"pass\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(ErrorCode.ERR_UNAUTHORIZED.name()));
    }
}
