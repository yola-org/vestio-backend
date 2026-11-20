package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.CreateOfferRequest;
import org.app.vestiobackend.dto.OfferDto;
import org.app.vestiobackend.dto.OfferSummaryDto;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Offer;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.OfferStatus;
import com.vestio.common.entity.enums.NotificationType;
import com.vestio.common.event.payload.OfferSentPayload;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.http.errors.exception.ResourceAlreadyExistsException;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.app.vestiobackend.repository.ListingRepository;
import org.app.vestiobackend.repository.OfferRepository;
import org.app.vestiobackend.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final ListingRepository listingRepository;
    private final OfferRepository offerRepository;
    private final UserContext userContext;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<OfferSummaryDto> getSentOffers() {
        return offerRepository.findByUser_IdOrderByCreatedAtDescIdDesc(userContext.getCurrentUser().getId())
                .stream().map(this::toOfferSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<OfferSummaryDto> getReceivedOffers() {
        return offerRepository.findByListing_User_IdOrderByCreatedAtDescIdDesc(userContext.getCurrentUser().getId())
                .stream().map(this::toOfferSummary).toList();
    }

    @Transactional
    public OfferDto createOffer(UUID listingId, CreateOfferRequest request) {
        User buyer = userContext.getCurrentUser();
        Listing listing = listingRepository.findForUpdateById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));

        if (listing.getUser().getId().equals(buyer.getId())) {
            throw new BadRequestException(ErrorCode.ERR_CANNOT_OFFER_ON_OWN_LISTING,
                    "You cannot make an offer on your own listing");
        }

        if (offerRepository.existsByUserIdAndListingId(buyer.getId(), listingId)) {
            throw new ResourceAlreadyExistsException(ErrorCode.ERR_OFFER_ALREADY_EXISTS,
                    "You already made an offer on this listing");
        }

        Offer offer = new Offer();
        offer.setUser(buyer);
        offer.setListing(listing);
        applyOfferDetails(offer, request);
        Offer saved = offerRepository.saveAndFlush(offer);
        String senderName = Stream.of(buyer.getFirstName(), buyer.getLastName())
                .filter(name -> name != null && !name.isBlank())
                .map(String::strip).collect(Collectors.joining(" "));
        var payload = new OfferSentPayload(saved.getId(), saved.getPrice(), buyer.getId(),
                senderName, listing.getTitle());
        notificationService.notifyUser(listing.getUser().getId(), NotificationType.OFFER_SENT,
                payload, listing.getId());
        return toOfferDto(saved);
    }

    @Transactional
    public OfferDto updateOffer(UUID listingId, UUID offerId, CreateOfferRequest request) {
        Offer offer = findPendingOfferForBuyer(listingId, offerId);
        applyOfferDetails(offer, request);
        return toOfferDto(offer);
    }

    @Transactional
    public void deleteOffer(UUID listingId, UUID offerId) {
        offerRepository.delete(findPendingOfferForBuyer(listingId, offerId));
    }

    @Transactional
    public OfferDto acceptOffer(UUID listingId, UUID offerId) {
        User currentUser = userContext.getCurrentUser();
        Offer offer = findOfferForUpdate(listingId, offerId);
        if (!offer.getListing().getUser().getId().equals(currentUser.getId())) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Only the listing owner can accept this offer");
        }
        offer.setStatus(OfferStatus.ACCEPTED);
        return toOfferDto(offer);
    }

    private Offer findOfferForUpdate(UUID listingId, UUID offerId) {
        listingRepository.findForUpdateById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Listing not found"));
        return offerRepository.findByIdAndListingId(offerId, listingId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Offer not found"));
    }

    private Offer findPendingOfferForBuyer(UUID listingId, UUID offerId) {
        User currentUser = userContext.getCurrentUser();
        Offer offer = findOfferForUpdate(listingId, offerId);
        if (!offer.getUser().getId().equals(currentUser.getId())) {
            throw new UnAuthorizedException(ErrorCode.ERR_UNAUTHORIZED, "Only the buyer can change this offer");
        }
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadRequestException(ErrorCode.ERR_OFFER_NOT_PENDING, "Only pending offers can be changed");
        }
        return offer;
    }

    private void applyOfferDetails(Offer offer, CreateOfferRequest request) {
        List<ContactType> deliveryTypes = request.deliveryTypes();
        List<ContactType> listingTypes = offer.getListing().getContactTypes();
        if (deliveryTypes == null || deliveryTypes.isEmpty()
                || new HashSet<>(deliveryTypes).size() != deliveryTypes.size()
                || deliveryTypes.stream().anyMatch(type -> type == null || listingTypes == null || !listingTypes.contains(type))) {
            throw new BadRequestException(ErrorCode.ERR_INVALID_OFFER_DELIVERY_TYPES,
                    "Choose one or more distinct delivery options offered by this listing");
        }
        offer.setPrice(request.price());
        offer.setDeliveryTypes(deliveryTypes.stream().map(ContactType::name).toArray(String[]::new));
        offer.setMessage(request.message());
    }

    private OfferDto toOfferDto(Offer offer) {
        List<ContactType> deliveryTypes = Arrays.stream(offer.getDeliveryTypes()).map(ContactType::valueOf).toList();
        return new OfferDto(offer.getId(), offer.getUser().getId(), offer.getListing().getId(), offer.getPrice(),
                deliveryTypes, offer.getMessage(), offer.getStatus(), offer.getCreatedAt());
    }

    private OfferSummaryDto toOfferSummary(Offer offer) {
        Listing listing = offer.getListing();
        return new OfferSummaryDto(toOfferDto(offer), listing.getTitle(), listing.getStatus(), listing.getContactTypes());
    }

    void validateListingContactTypes(Listing listing, List<ContactType> contactTypes) {
        List<ContactType> proposedTypes = contactTypes == null ? List.of() : contactTypes;
        if (listing.getContactTypes() != null && !proposedTypes.containsAll(listing.getContactTypes())) {
            var proposedNames = proposedTypes.stream().map(ContactType::name).collect(Collectors.toSet());
            boolean hasAffectedOffers = offerRepository.findByListingId(listing.getId()).stream()
                    .flatMap(offer -> Arrays.stream(offer.getDeliveryTypes()))
                    .anyMatch(type -> !proposedNames.contains(type));
            if (hasAffectedOffers) {
                throw new BadRequestException(ErrorCode.ERR_LISTING_DELIVERY_TYPE_IN_USE,
                        "A delivery option used by an existing offer cannot be removed");
            }
        }
    }
}
