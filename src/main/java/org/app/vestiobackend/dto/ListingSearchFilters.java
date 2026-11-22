package org.app.vestiobackend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import com.vestio.common.entity.enums.Condition;
import com.vestio.common.entity.enums.ContactType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ListingSearchFilters(
        String query,
        UUID categoryId,
        Boolean includeSubcategories,
        UUID brandId,
        String gender,
        List<@NotBlank String> colors,
        List<@NotNull UUID> cityIds,
        List<@NotNull UUID> sizeIds,
        List<@NotNull Condition> conditions,
        List<@NotNull ContactType> contactTypes,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice
) {
    public ListingSearchFilters {
        colors = colors == null ? List.of() : colors;
        cityIds = cityIds == null ? List.of() : cityIds;
        sizeIds = sizeIds == null ? List.of() : sizeIds;
        conditions = conditions == null ? List.of() : conditions;
        contactTypes = contactTypes == null ? List.of() : contactTypes;
    }

    @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }
}
