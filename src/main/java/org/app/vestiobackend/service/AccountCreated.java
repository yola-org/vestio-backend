package org.app.vestiobackend.service;

import java.util.UUID;

/** Snapshot of the activated account; contains no password, token, or managed entity. */
public record AccountCreated(UUID userId, String email, String firstName) { }
