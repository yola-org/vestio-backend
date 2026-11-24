package org.app.vestiobackend.controller;

import org.hamcrest.Matchers;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.ContactType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserOfferControllerTest extends AbstractOfferControllerTest {

    @Test
    void listsOnlyOffersSentOrReceivedByTheAuthenticatedUser() throws Exception {
        User otherSeller = createBuyer("other-seller@example.com");
        User otherBuyer = createBuyer("other-buyer@example.com");
        Listing otherListing = createTestListing("Other listing", otherSeller, testCity, testCategory);
        String profileKey = buyer.getId() + "/profile/" + UUID.randomUUID() + ".webp";
        buyer.setProfileImageKey(profileKey);
        userRepository.saveAndFlush(buyer);
        UUID cover = UUID.randomUUID();
        listing.setAssetKeys(List.of(cover, UUID.randomUUID()));
        listingRepository.saveAndFlush(listing);
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        submitOffer(testUser, otherListing.getId(), VALID_OFFER).andExpect(status().isCreated());
        submitOffer(otherBuyer, otherListing.getId(), VALID_OFFER).andExpect(status().isCreated());

        mockMvc.perform(get("/api/offers/sent").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].offer.userId").value(testUser.getId().toString()))
                .andExpect(jsonPath("$[0].offer.listingId").value(otherListing.getId().toString()))
                .andExpect(jsonPath("$[0].listingTitle").value("Other listing"));

        mockMvc.perform(get("/api/offers/received").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].offer.userId").value(buyer.getId().toString()))
                .andExpect(jsonPath("$[0].offer.listingId").value(listing.getId().toString()))
                .andExpect(jsonPath("$[0].listingTitle").value("Listing"))
                .andExpect(jsonPath("$[0].senderName").value("Offer Buyer"))
                .andExpect(jsonPath("$[0].senderProfileImageKey").value(profileKey))
                .andExpect(jsonPath("$[0].listingCoverAssetKey").value(cover.toString()));
    }

    @Test
    void requiresAuthenticationToListSentAndReceivedOffers() throws Exception {
        mockMvc.perform(get("/api/offers/sent")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/offers/received")).andExpect(status().isUnauthorized());
    }

    @Test
    void mapsAllDeliveryOptionsAndJoinsBuyerAndListingOwner() throws Exception {
        updateDeliveryOptions(List.of(ContactType.MEETUP, ContactType.DELIVERY, ContactType.PARCEL))
                .andExpect(status().isOk());

        submitOffer(buyer, listing.getId(), """
                {"price":99999999999999999.99,"deliveryTypes":["MEETUP","DELIVERY","PARCEL"],"message":"Either works"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(buyer.getId().toString()))
                .andExpect(jsonPath("$.listingId").value(listing.getId().toString()))
                .andExpect(jsonPath("$.deliveryTypes").value(contains("MEETUP", "DELIVERY", "PARCEL")))
                .andExpect(jsonPath("$.message").value("Either works"))
                .andExpect(jsonPath("$.createdAt").value(Matchers.endsWith("Z")));

        UUID sentOffer = jdbcTemplate.queryForObject("""
                SELECT offer_id FROM user_offers
                WHERE user_id = ? AND listing_id = ? AND first_name = ? AND last_name = ?
                """, UUID.class, buyer.getId(), listing.getId(), buyer.getFirstName(), buyer.getLastName());
        UUID receivedOffer = jdbcTemplate.queryForObject("""
                SELECT offer_id FROM users_offers
                WHERE user_id = ? AND buyer_id = ? AND listing_id = ? AND message = ?
                """, UUID.class, testUser.getId(), buyer.getId(), listing.getId(), "Either works");
        assertEquals(sentOffer, receivedOffer);
        assertEquals(new BigDecimal("99999999999999999.99"),
                jdbcTemplate.queryForObject("SELECT price FROM users_offers WHERE offer_id = ?",
                        BigDecimal.class, receivedOffer));
    }

}
