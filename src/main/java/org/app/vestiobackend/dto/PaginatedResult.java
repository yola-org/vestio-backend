package org.app.vestiobackend.dto;

import java.util.List;

/** Shared API page contract. Page numbers are zero based. */
public record PaginatedResult<T>(List<T> items, int page, int size, long totalElements, int totalPages) { }
