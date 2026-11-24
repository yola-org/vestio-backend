package org.app.vestiobackend.controller;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferControllerTest extends AbstractOfferControllerTest {

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/listings/{id}/offers", listing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isUnauthorized());
        assertEquals(0, offerCount());
    }

    @Test
    void rejectsOffersFromTheListingOwner() throws Exception {
        submitOffer(testUser, listing.getId(), VALID_OFFER)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_CANNOT_OFFER_ON_OWN_LISTING"));
        assertEquals(0, offerCount());
    }

    @Test
    void returnsNotFoundForUnknownListing() throws Exception {
        submitOffer(buyer, UUID.randomUUID(), VALID_OFFER)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ERR_RESOURCE_NOT_FOUND"));
        assertEquals(0, offerCount());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"deliveryTypes\":[\"MEETUP\"],\"message\":\"Offer\"}",
            "{\"price\":0,\"deliveryTypes\":[\"MEETUP\"],\"message\":\"Offer\"}",
            "{\"price\":-1,\"deliveryTypes\":[\"MEETUP\"],\"message\":\"Offer\"}",
            "{\"price\":1.001,\"deliveryTypes\":[\"MEETUP\"],\"message\":\"Offer\"}",
            "{\"price\":100000000000000000,\"deliveryTypes\":[\"MEETUP\"],\"message\":\"Offer\"}",
            "{\"price\":1,\"deliveryTypes\":[\"MEETUP\"]}",
            "{\"price\":1,\"deliveryTypes\":[\"MEETUP\"],\"message\":null}",
            "{\"price\":1,\"message\":\"Offer\"}",
            "{\"price\":1,\"deliveryTypes\":null,\"message\":\"Offer\"}",
            "{\"price\":1,\"deliveryTypes\":[],\"message\":\"Offer\"}",
            "{\"price\":1,\"deliveryTypes\":[\"MEETUP\",null],\"message\":\"Offer\"}"
    })
    void rejectsInvalidRequestFields(String body) throws Exception {
        submitOffer(buyer, listing.getId(), body)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
        assertEquals(0, offerCount());
    }

    @Test
    void rejectsUnknownDeliveryType() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER.replace("MEETUP", "UNKNOWN"))
                .andExpect(status().isBadRequest());
        assertEquals(0, offerCount());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"PARCEL\"", "\"MEETUP\",\"PARCEL\"", "\"MEETUP\",\"MEETUP\""})
    void rejectsSelectionsThatAreNotDistinctListingOptions(String deliveryTypes) throws Exception {
        submitOffer(buyer, listing.getId(), """
                {"price":25.50,"deliveryTypes":[%s],"message":"Offer"}
                """.formatted(deliveryTypes))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_INVALID_OFFER_DELIVERY_TYPES"));
        assertEquals(0, offerCount());
    }

    @Test
    void rejectsOfferWhenListingHasNoDeliveryOptions() throws Exception {
        updateDeliveryOptions(List.of()).andExpect(status().isOk());
        submitOffer(buyer, listing.getId(), VALID_OFFER)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_INVALID_OFFER_DELIVERY_TYPES"));
        assertEquals(0, offerCount());
    }

    @Test
    void keepsOneOfferPerBuyerAndListing() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        submitOffer(buyer, listing.getId(), VALID_OFFER.replace("25.50", "30.00"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("ERR_OFFER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("You already made an offer on this listing"))
                .andExpect(jsonPath("$.path").value("/api/listings/" + listing.getId() + "/offers"));
        assertEquals(1, offerCount());
        assertEquals(new BigDecimal("25.50"), jdbcTemplate.queryForObject("SELECT price FROM offers", BigDecimal.class));

        User secondBuyer = createBuyer("second-buyer@example.com");
        submitOffer(secondBuyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        Listing secondListing = createTestListing("Second listing", testUser, testCity, testCategory);
        submitOffer(buyer, secondListing.getId(), VALID_OFFER).andExpect(status().isCreated());
        assertEquals(3, offerCount());
    }

    @Test
    void handlesConcurrentSubmissionsFromTheSameBuyer() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CyclicBarrier start = new CyclicBarrier(2);
            Callable<Integer> submit = () -> {
                start.await(5, TimeUnit.SECONDS);
                return submitOffer(buyer, listing.getId(), VALID_OFFER)
                        .andReturn().getResponse().getStatus();
            };

            Future<Integer> first = executor.submit(submit);
            Future<Integer> second = executor.submit(submit);
            assertEquals(List.of(201, 409), List.of(first.get(10, TimeUnit.SECONDS),
                    second.get(10, TimeUnit.SECONDS)).stream().sorted().toList());
        }
        assertEquals(1, offerCount());
    }

    @Test
    void databaseRejectsDuplicateBuyerAndListing() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER).andExpect(status().isCreated());
        assertThrows(DuplicateKeyException.class, () -> jdbcTemplate.update("""
                INSERT INTO offers (id, user_id, listing_id, price, delivery_types, message)
                SELECT ?, user_id, listing_id, price, delivery_types, message FROM offers
                WHERE user_id = ? AND listing_id = ?
                """, UUID.randomUUID(), buyer.getId(), listing.getId()));
        assertEquals(1, offerCount());
    }

    @Test
    void onlyTheBuyerCanEditTheirPendingOffer() throws Exception {
        UUID offerId = createPendingOffer();
        String path = "/api/listings/{id}/offers/{offerId}";
        String updatedOffer = """
                {"price":30.25,"deliveryTypes":["MEETUP"],"message":"Updated offer"}
                """;

        mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON).content(updatedOffer))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON).content(updatedOffer.replace("MEETUP", "PARCEL")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_INVALID_OFFER_DELIVERY_TYPES"));

        mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON).content(updatedOffer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(offerId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.price").value(30.25));

        mockMvc.perform(get("/api/offers/sent").with(authenticatedUser(buyer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].offer.message").value("Updated offer"))
                .andExpect(jsonPath("$.items[0].listingDeliveryTypes[0]").value("MEETUP"));
        assertEquals(1, offerCount());
    }

    @Test
    void buyerCannotEditOrCancelAnAcceptedOffer() throws Exception {
        UUID offerId = createPendingOffer();
        String path = "/api/listings/{id}/offers/{offerId}";
        mockMvc.perform(put(path + "/accept", listing.getId(), offerId).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());

        mockMvc.perform(put(path, listing.getId(), offerId).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_OFFER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_OFFER_NOT_PENDING"));

        mockMvc.perform(delete(path, listing.getId(), offerId).with(authenticatedUser(buyer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_OFFER_NOT_PENDING"));
        assertEquals(1, offerCount());
    }

    @Test
    void acceptanceAndDeletionRequireTheOfferToBelongToTheRequestedListing() throws Exception {
        UUID offerId = createPendingOffer();
        Listing other = createTestListing("Other", testUser, testCity, testCategory);

        for (UUID invalidListingId : List.of(other.getId(), UUID.randomUUID())) {
            mockMvc.perform(put("/api/listings/{id}/offers/{offerId}/accept", invalidListingId, offerId)
                            .with(authenticatedUser(testUser)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("ERR_RESOURCE_NOT_FOUND"));
            mockMvc.perform(delete("/api/listings/{id}/offers/{offerId}", invalidListingId, offerId)
                            .with(authenticatedUser(buyer)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("ERR_RESOURCE_NOT_FOUND"));
        }
        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT status FROM offers WHERE id = ?", String.class, offerId));
    }

}
