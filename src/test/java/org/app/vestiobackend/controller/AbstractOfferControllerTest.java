package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.Condition;
import com.vestio.common.entity.enums.ContactType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractOfferControllerTest extends AbstractControllerTest {
    protected static final String VALID_OFFER = """
            {"price":25.50,"deliveryTypes":["MEETUP"],"message":"Can we meet?"}
            """;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected Listing listing;
    protected User buyer;

    @BeforeEach
    void createOfferFixtures() {
        listing = createTestListing("Listing", testUser, testCity, testCategory);
        buyer = createBuyer("buyer@example.com");
    }

    protected UUID createPendingOffer() throws Exception {
        submitOffer(buyer, listing.getId(), VALID_OFFER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
        return jdbcTemplate.queryForObject("SELECT id FROM offers WHERE user_id = ? AND listing_id = ?",
                UUID.class, buyer.getId(), listing.getId());
    }

    protected User createBuyer(String email) {
        return userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .firstName("Offer")
                .lastName("Buyer")
                .build());
    }

    protected ResultActions submitOffer(User author, UUID listingId, String body) throws Exception {
        return mockMvc.perform(post("/api/listings/{id}/offers", listingId)
                .with(authenticatedUser(author))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    protected ResultActions updateDeliveryOptions(List<ContactType> options) throws Exception {
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

    protected int offerCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM offers", Integer.class);
    }
}
