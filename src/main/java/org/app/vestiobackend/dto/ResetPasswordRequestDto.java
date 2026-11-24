package org.app.vestiobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "[0-9]{6}") String pin,
        @NotBlank @Size(min = 12, max = 256)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9\\s])\\S+$",
                message = "Use uppercase, lowercase, a number and a symbol, with no spaces") String newPassword) { }
