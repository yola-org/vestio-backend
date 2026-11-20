package org.app.vestiobackend.dto;

import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.OfferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OfferDto(
        UUID id,
        UUID userId,
        UUID listingId,
        BigDecimal price,
        List<ContactType> deliveryTypes,
        String message,
        OfferStatus status,
        LocalDateTime createdAt
) {
}
