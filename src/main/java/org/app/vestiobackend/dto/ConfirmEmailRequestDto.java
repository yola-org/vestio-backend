package org.app.vestiobackend.dto;

import jakarta.validation.constraints.*;

public record ConfirmEmailRequestDto(@NotBlank @Email String email, @NotBlank @Pattern(regexp = "[0-9]{6}") String pin) { }
