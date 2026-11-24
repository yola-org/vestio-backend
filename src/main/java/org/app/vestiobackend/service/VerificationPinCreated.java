package org.app.vestiobackend.service;

import java.util.UUID;

/** Transient mail payload; never persisted or logged. */
public record VerificationPinCreated(UUID id, String email, String pin) { }
