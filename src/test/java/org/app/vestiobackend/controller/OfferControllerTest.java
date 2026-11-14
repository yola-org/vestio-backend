package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferControllerTest extends AbstractControllerTest {

    private static final String VALID_OFFER = """
            {"price":25.50,"deliveryTypes":["MEETUP"],"message":"Can we meet?"}
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Listing listing;
    private User buyer;

    @BeforeEach
    void createOfferFixtures() {
        listing = createTestListing("Listing", testUser, testCity, testCategory);
        buyer = createBuyer("buyer@example.com");
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/listings/{id}/offers", listing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_OFFER))
                .andExpect(status().isUnauthorized());
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
        try (var executor = Executors.newFixedThreadPool(2)) {
            CyclicBarrier start = new CyclicBarrier(2);
            Callable<Integer> submit = () -> {
                start.await(5, TimeUnit.SECONDS);
                return submitOffer(buyer, listing.getId(), VALID_OFFER)
                        .andReturn().getResponse().getStatus();
            };

            var first = executor.submit(submit);
            var second = executor.submit(submit);
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
                .andExpect(jsonPath("$.createdAt").exists());

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

    private UUID createPendingOffer() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
        return jdbcTemplate.queryForObject("SELECT id FROM offers WHERE user_id = ? AND listing_id = ?",
                UUID.class, buyer.getId(), listing.getId());
    }

    private User createBuyer(String email) {
        return userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .cognitoSub(UUID.randomUUID().toString())
                .email(email)
                .firstName("Offer")
                .lastName("Buyer")
                .build());
    }

    private ResultActions submitOffer(User author, UUID listingId, String body) throws Exception {
        return mockMvc.perform(post("/api/listings/{id}/offers", listingId)
                .with(authenticatedUser(author))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions updateDeliveryOptions(List<ContactType> options) throws Exception {
        ListingDto update = ListingDto.builder()
                .title(listing.getTitle())
                .price(listing.getPrice())
                .quantity(listing.getQuantity())
                .condition(Condition.GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .contactTypes(options)
                .build();
        return mockMvc.perform(put("/api/listings/{id}", listing.getId())
                .with(authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)));
    }

    private int offerCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM offers", Integer.class);
    }
}
