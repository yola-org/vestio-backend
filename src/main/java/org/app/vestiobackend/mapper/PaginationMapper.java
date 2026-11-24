package org.app.vestiobackend.mapper;

import org.app.vestiobackend.dto.PaginatedResult;
import org.springframework.data.domain.Page;

/** Converts database query results into the API's pagination DTO. */
public final class PaginationMapper {
    private PaginationMapper() { }

    public static <T> PaginatedResult<T> from(Page<T> page) {
        return new PaginatedResult<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
