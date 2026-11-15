package org.app.vestiobackend.dto;

import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;

import java.util.List;

public record OfferSummaryDto(OfferDto offer, String listingTitle, ListingStatus listingStatus,
                              List<ContactType> listingDeliveryTypes) {
}
