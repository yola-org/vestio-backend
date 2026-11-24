package org.app.vestiobackend.controller;

import org.app.vestiobackend.service.EmailVerificationService;
import org.app.vestiobackend.dto.ConfirmEmailRequestDto;
import org.app.vestiobackend.dto.ResendPinRequestDto;
import org.app.vestiobackend.dto.ForgotPasswordRequestDto;
import org.app.vestiobackend.dto.ResetPasswordRequestDto;
import org.app.vestiobackend.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.JwtResponseDto;
import org.app.vestiobackend.dto.LoginRequestDto;
import org.app.vestiobackend.dto.RefreshTokenRequestDto;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.service.AuthService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication operations.
 * Provides endpoints for user signup, login, and token refresh.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService verification;
    private final PasswordResetService passwordReset;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        passwordReset.request(request.email());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body("If an eligible account exists, a password reset PIN has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        passwordReset.reset(request.email(), request.pin(), request.newPassword());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body("Password reset. Please sign in with your new password.");
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@Valid @RequestBody ConfirmEmailRequestDto request) {
        verification.confirm(request.email(), request.pin());
        return ResponseEntity.ok("Email verified. You can now sign in.");
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resendConfirmation(@Valid @RequestBody ResendPinRequestDto request) {
        verification.resend(request.email());
        return ResponseEntity.ok("If the account requires verification, a PIN has been sent.");
    }

    /**
     * Endpoint for user registration.
     *
     * @param request The signup request DTO containing user details.
     * @return A response entity with a success message.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto request) {
        authService.signup(request);
        return ResponseEntity.ok("User registered successfully. Please confirm your email using the PIN sent to you.");
    }

    /**
     * Endpoint for user login.
     *
     * @param request The login request DTO containing email and password.
     * @return A response entity containing the JWT response with all tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        JwtResponseDto response = authService.login(request);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        JwtResponseDto response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
    }
}
