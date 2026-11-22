package org.app.vestiobackend.dto;

import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.ListingStatus;

import java.util.List;
import java.util.UUID;

public record OfferSummaryDto(OfferDto offer, String listingTitle, ListingStatus listingStatus,
                              List<ContactType> listingDeliveryTypes, UUID listingCoverAssetKey,
                              String senderName, String senderProfileImageKey) {
}
