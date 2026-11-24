package org.app.vestiobackend.flow;

import com.vestio.common.entity.enums.ListingStatus;
import org.app.vestiobackend.controller.AbstractControllerTest;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ListingPaginationFlowIT extends AbstractControllerTest {
    @Test
    void listingTotalsRespectSearchOwnerAndStatusFilters() throws Exception {
        for (int index = 0; index < 5; index++) createTestListing("Paged coat " + index, testUser, testCity, testCategory);
        Listing pending = createTestListing("Paged coat pending", testUser, testCity, testCategory);
        pending.setStatus(ListingStatus.PENDING);
        listingRepository.saveAndFlush(pending);
        User other = otherUser();
        createTestListing("Paged coat other owner", other, testCity, testCategory);
        mockMvc.perform(get("/api/listings/search").param("query", "Paged coat").param("size", "2").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.pageable").doesNotExist())
                .andExpect(jsonPath("$.sort").doesNotExist());
        mockMvc.perform(get("/api/listings/mine").with(authenticatedUser(testUser))
                        .param("status", "AVAILABLE").param("size", "2").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.pageable").doesNotExist())
                .andExpect(jsonPath("$.sort").doesNotExist());
        mockMvc.perform(get("/api/listings/pending").with(authenticatedUser(testUser)).param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void favoritesRemainScopedAndCountedAfterRemovingTheLastItemOnAPage() throws Exception {
        User owner = otherUser();
        Listing last = null;
        for (int index = 0; index < 5; index++) {
            last = createTestListing("Saved " + index, owner, testCity, testCategory);
            mockMvc.perform(post("/api/listings/{id}/favorite", last.getId()).with(authenticatedUser(testUser)))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)).param("size", "2").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.pageable").doesNotExist())
                .andExpect(jsonPath("$.sort").doesNotExist());
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser))
                        .param("listingIds", last.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(last.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
        mockMvc.perform(delete("/api/listings/{id}/favorite", last.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)).param("size", "2").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2));
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(owner)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void rejectsInvalidPaginationOnEveryListingCollection() throws Exception {
        for (String path : List.of("/api/listings", "/api/listings/search", "/api/listings/mine", "/api/listings/pending", "/api/listings/favorites")) {
            mockMvc.perform(get(path).with(authenticatedUser(testUser)).param("size", "0"))
                    .andExpect(status().isBadRequest());
            mockMvc.perform(get(path).with(authenticatedUser(testUser)).param("size", "101"))
                    .andExpect(status().isBadRequest());
            mockMvc.perform(get(path).with(authenticatedUser(testUser)).param("page", "-1"))
                    .andExpect(status().isBadRequest());
        }
    }

    private User otherUser() {
        return userRepository.saveAndFlush(User.builder().id(UUID.randomUUID()).email("pagination-owner@example.com")
                .firstName("Other").lastName("Owner").emailVerified(true).build());
    }
}
