package org.app.vestiobackend.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.app.vestiobackend.entity.enums.ContactType;

import java.math.BigDecimal;
import java.util.List;

public record CreateOfferRequest(
        @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal price,
        @NotEmpty List<@NotNull ContactType> deliveryTypes,
        @NotNull String message
) {
}
