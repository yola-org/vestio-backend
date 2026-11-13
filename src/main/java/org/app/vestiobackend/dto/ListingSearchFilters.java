package org.app.vestiobackend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;

import java.math.BigDecimal;
import java.util.UUID;

public record ListingSearchFilters(
        String query,
        UUID categoryId,
        UUID brandId,
        UUID cityId,
        UUID sizeId,
        String gender,
        String color,
        Condition condition,
        ContactType contactType,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice
) {
    @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }
}
