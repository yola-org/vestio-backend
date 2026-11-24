package org.app.vestiobackend.service;

import java.util.UUID;

/** Transient email payload; never persisted or logged. */
public record PasswordResetPinCreated(UUID id, String email, String pin) { }
