package org.app.vestiobackend.service;

import org.app.vestiobackend.dto.CreateOfferRequest;
import org.app.vestiobackend.entity.Listing;
import com.vestio.common.entity.enums.ContactType;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfferServiceTest extends AbstractServiceTest {

    @Autowired
    private OfferService offerService;

    @Autowired
    private OfferRepository offerRepository;

    @Test
    void rejectsAnOfferFromTheListingOwnerWithoutSavingIt() {
        Listing listing = createTestListing("Listing", testUser, testCity, testCategory);
        setupSecurityContext(testUser);
        CreateOfferRequest request = new CreateOfferRequest(BigDecimal.TEN, List.of(ContactType.MEETUP), "Offer");

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> offerService.createOffer(listing.getId(), request));

        assertEquals(ErrorCode.ERR_CANNOT_OFFER_ON_OWN_LISTING, error.getErrorCode());
        assertEquals(0, offerRepository.count());
    }

    @ParameterizedTest
    @MethodSource("invalidDeliveryTypes")
    void rejectsInvalidDeliveryOptionsOutsideTheController(List<ContactType> deliveryTypes) {
        Listing listing = createTestListing("Listing", testUser, testCity, testCategory);
        setupSecurityContext(createTestUser("buyer@example.com"));
        CreateOfferRequest request = new CreateOfferRequest(BigDecimal.TEN, deliveryTypes, "Offer");

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> offerService.createOffer(listing.getId(), request));

        assertEquals(ErrorCode.ERR_INVALID_OFFER_DELIVERY_TYPES, error.getErrorCode());
        assertEquals(0, offerRepository.count());
    }

    private static Stream<List<ContactType>> invalidDeliveryTypes() {
        return Stream.of(null, List.of(), Collections.singletonList(null));
    }
}
