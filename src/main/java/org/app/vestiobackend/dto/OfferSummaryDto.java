package org.app.vestiobackend.dto;

import org.app.vestiobackend.entity.enums.ListingStatus;

public record OfferSummaryDto(OfferDto offer, String listingTitle, ListingStatus listingStatus) {
}
