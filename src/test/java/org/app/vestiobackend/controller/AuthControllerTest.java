package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.JwtResponseDto;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest {

    @MockBean
    private AuthService authService;

    @Test
    public void signupShouldReturnOk() throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123!");

        doNothing().when(authService).signup(any(SignupRequestDto.class));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void loginShouldReturnTokens() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123!");

        JwtResponseDto response = JwtResponseDto.builder()
                .accessToken("mock-access")
                .idToken("mock-id")
                .expiresIn(3600L)
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access"))
                .andExpect(jsonPath("$.idToken").value("mock-id"));
    }
}