package org.app.vestiobackend.service;

import java.util.UUID;

public record PasswordChanged(UUID userId, String email, UUID resetId) { }
