package org.app.vestiobackend.service;

import org.app.vestiobackend.dto.CreateOfferRequest;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import org.app.vestiobackend.repository.OfferRepository;
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

    @ParameterizedTest
    @MethodSource("invalidDeliveryTypes")
    void rejectsInvalidDeliveryOptionsOutsideTheController(List<ContactType> deliveryTypes) {
        Listing listing = createTestListing("Listing", testUser, testCity, testCategory);
        setupSecurityContext(createTestUser("buyer@example.com"));
        var request = new CreateOfferRequest(BigDecimal.TEN, deliveryTypes, "Offer");

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> offerService.createOffer(listing.getId(), request));

        assertEquals(ErrorCode.ERR_INVALID_OFFER_DELIVERY_TYPES, error.getErrorCode());
        assertEquals(0, offerRepository.count());
    }

    private static Stream<List<ContactType>> invalidDeliveryTypes() {
        return Stream.of(null, List.of(), Collections.singletonList(null));
    }
}
