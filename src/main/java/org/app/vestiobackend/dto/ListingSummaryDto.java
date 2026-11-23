package org.app.vestiobackend.dto;

import com.vestio.common.entity.enums.Condition;
import com.vestio.common.entity.enums.ListingStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Fields displayed by listing cards; detail and mutation responses use ListingDto. */
@Getter
@RequiredArgsConstructor
public class ListingSummaryDto {
    private final UUID id;
    private final String title;
    private final BigDecimal price;
    private final Condition condition;
    private final ListingStatus status;
    private final UUID userId;
    private final String sellerFirstName;
    private final String sellerLastName;
    private final String sellerProfileImageKey;
    private final UUID coverAssetKey;
    private final Instant createdAt;
    private final String categoryName;
    private final String descriptionPreview;
    private final List<String> colors = new ArrayList<>();
    private final List<String> cityNames = new ArrayList<>();
    private final List<String> sizeNames = new ArrayList<>();
}
