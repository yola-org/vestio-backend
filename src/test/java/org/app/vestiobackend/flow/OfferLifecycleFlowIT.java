package org.app.vestiobackend.flow;

import org.app.vestiobackend.controller.AbstractOfferControllerTest;

import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.ContactType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferLifecycleFlowIT extends AbstractOfferControllerTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "DELIVERY")
    void blocksRemovingOptionsUsedByAnOffer(String remainingType) throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        List<ContactType> remaining = remainingType == null ? null : remainingType.isEmpty()
                ? List.of() : List.of(ContactType.valueOf(remainingType));
        updateDeliveryOptions(remaining)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_LISTING_DELIVERY_TYPE_IN_USE"));
        assertEquals(List.of("MEETUP"), jdbcTemplate.queryForList(
                "SELECT contact_type FROM listing_contact_types WHERE listing_id = ?",
                String.class, listing.getId()));
        assertEquals(1, offerCount());
    }

    @Test
    void allowsAddingOptionsAndRemovingUnusedOptions() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        updateDeliveryOptions(List.of(ContactType.MEETUP, ContactType.DELIVERY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactTypes").value(containsInAnyOrder("MEETUP", "DELIVERY")));

        updateDeliveryOptions(List.of(ContactType.MEETUP))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactTypes").value(contains("MEETUP")));
        assertEquals(1, offerCount());
    }

    @Test
    void removesOffersFromBothViewsWhenListingIsDeleted() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        mockMvc.perform(delete("/api/listings/{id}", listing.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isNoContent());

        assertEquals(0, offerCount());
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_offers", Integer.class));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users_offers", Integer.class));
    }

    @Test
    void removesOffersWhenBuyerIsDeleted() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        userRepository.deleteById(buyer.getId());

        assertEquals(0, offerCount());
        assertTrue(listingRepository.existsById(listing.getId()));
    }

    @Test
    void onlyListingOwnerCanAcceptAndStatusAppearsInBothViews() throws Exception {
        UUID offerId = createPendingOffer();
        String path = "/api/listings/{id}/offers/{offerId}/accept";

        mockMvc.perform(put(path, listing.getId(), offerId))
                .andExpect(status().isUnauthorized());
        for (User nonOwner : List.of(buyer, createBuyer("other@example.com"))) {
            mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(nonOwner)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("ERR_UNAUTHORIZED"));
        }
        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT status FROM users_offers WHERE offer_id = ?", String.class, offerId));

        for (int attempt = 0; attempt < 2; attempt++) {
            mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(testUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(offerId.toString()))
                    .andExpect(jsonPath("$.status").value("ACCEPTED"));
        }
        assertEquals("ACCEPTED", jdbcTemplate.queryForObject(
                "SELECT status FROM user_offers WHERE offer_id = ?", String.class, offerId));
        assertEquals("ACCEPTED", jdbcTemplate.queryForObject(
                "SELECT status FROM users_offers WHERE offer_id = ?", String.class, offerId));
        mockMvc.perform(put("/api/listings/{id}/offers/{offerId}/refuse", listing.getId(), offerId).with(authenticatedUser(testUser)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("ERR_OFFER_NOT_PENDING"));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chats", Integer.class));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages WHERE is_system = TRUE AND system_type = 'OFFER_ACCEPTED'", Integer.class));
        UUID chatId = jdbcTemplate.queryForObject("SELECT id FROM chats", UUID.class);
        mockMvc.perform(get("/api/notifications").with(authenticatedUser(buyer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].type").value("offer-accepted"))
                .andExpect(jsonPath("$.items[0].payload.offerId").value(offerId.toString()))
                .andExpect(jsonPath("$.items[0].payload.chatId").value(chatId.toString()))
                .andExpect(jsonPath("$.items[0].payload.ownerName").isString())
                .andExpect(jsonPath("$.items[0].payload.buyerName").value("Offer Buyer"))
                .andExpect(jsonPath("$.items[0].payload.listingTitle").value("Listing"));
    }

    @Test
    void onlyBuyerCanDeleteAndCanSubmitAnotherOfferAfterDeleting() throws Exception {
        UUID offerId = createPendingOffer();
        String path = "/api/listings/{id}/offers/{offerId}";

        mockMvc.perform(delete(path, listing.getId(), offerId))
                .andExpect(status().isUnauthorized());
        for (User nonBuyer : List.of(testUser, createBuyer("other@example.com"))) {
            mockMvc.perform(delete(path, listing.getId(), offerId).with(authenticatedUser(nonBuyer)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("ERR_UNAUTHORIZED"));
        }
        assertEquals(1, offerCount());

        mockMvc.perform(delete(path, listing.getId(), offerId).with(authenticatedUser(buyer)))
                .andExpect(status().isNoContent());
        assertEquals(0, offerCount());
        mockMvc.perform(delete(path, listing.getId(), offerId).with(authenticatedUser(buyer)))
                .andExpect(status().isNotFound());
        submitOffer(buyer, listing.getId(), VALID_OFFER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void onlyOwnerCanRefuseAndFinalDecisionsCannotBeReversed() throws Exception {
        UUID offerId = createPendingOffer();
        String path = "/api/listings/{id}/offers/{offerId}/refuse";
        mockMvc.perform(put(path, listing.getId(), offerId)).andExpect(status().isUnauthorized());
        for (User nonOwner : List.of(buyer, createBuyer("stranger@example.com"))) {
            mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(nonOwner)))
                    .andExpect(status().isUnauthorized());
        }
        Listing other = createTestListing("Other", testUser, testCity, testCategory);
        mockMvc.perform(put(path, other.getId(), offerId).with(authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(testUser)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("REFUSED"));
        }
        assertEquals("REFUSED", jdbcTemplate.queryForObject("SELECT status FROM user_offers WHERE offer_id = ?", String.class, offerId));
        assertEquals("REFUSED", jdbcTemplate.queryForObject("SELECT status FROM users_offers WHERE offer_id = ?", String.class, offerId));
        mockMvc.perform(put("/api/listings/{id}/offers/{offerId}/accept", listing.getId(), offerId).with(authenticatedUser(testUser)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("ERR_OFFER_NOT_PENDING"));
        mockMvc.perform(put("/api/listings/{id}/offers/{offerId}", listing.getId(), offerId).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_OFFER))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("ERR_OFFER_NOT_PENDING"));
        mockMvc.perform(delete("/api/listings/{id}/offers/{offerId}", listing.getId(), offerId).with(authenticatedUser(buyer)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("ERR_OFFER_NOT_PENDING"));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chats", Integer.class));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages", Integer.class));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM notifications", Integer.class));
    }

}
