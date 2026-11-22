package org.app.vestiobackend.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PaginatedResult<T>(List<T> items, int page, int size, long totalElements, int totalPages) {
    public static <T> PaginatedResult<T> from(Page<T> page) {
        return new PaginatedResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
