package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.JwtResponseDto;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.app.vestiobackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest {

    @MockitoBean
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
                .refreshToken("mock-refresh")
                .expiresIn(3600L)
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access"))
                .andExpect(jsonPath("$.idToken").value("mock-id"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh"));
    }

    @Test
    public void refreshShouldReturnTokensWithoutAnAccessToken() throws Exception {
        when(authService.refresh("mock-refresh")).thenReturn(JwtResponseDto.builder()
                .accessToken("new-access").idToken("new-id").expiresIn(3600L).tokenType("Bearer").build());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"mock-refresh"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }

    @Test
    public void refreshShouldRejectExpiredTokens() throws Exception {
        when(authService.refresh("expired-refresh")).thenThrow(new UnAuthorizedException("Expired refresh token."));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"expired-refresh"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
