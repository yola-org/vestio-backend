package org.app.vestiobackend.dto;

import jakarta.validation.constraints.*;

public record ResendPinRequestDto(@NotBlank @Email String email) { }
