package org.app.vestiobackend.controller;

import java.util.ArrayList;
import org.hamcrest.Matchers;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.Condition;
import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.ListingStatus;
import org.app.vestiobackend.service.S3AssetsService;
import org.app.vestiobackend.service.SqsEventPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "app.image-upload.max-file-size=1KB")
public class ListingControllerTest extends AbstractControllerTest {

    @MockitoBean
    private S3AssetsService assetsService;

    @MockitoBean
    private SqsEventPublisher eventPublisher;

    @Test
    public void createListingShouldReturnOk() throws Exception {
        ListingDto dto = ListingDto.builder()
                .title("Nice Jeans")
                .price(new BigDecimal("25.50"))
                .quantity(1)
                .contactTypes(List.of(ContactType.PARCEL))
                .condition(Condition.VERY_GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .sizeIds(List.of(testSize.getId()))
                .build();

        mockMvc.perform(post("/api/listings")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nice Jeans"))
                .andExpect(jsonPath("$.price").value(25.50))
                .andExpect(jsonPath("$.sizeIds[0]").value(testSize.getId().toString()));
    }

    @Test
    void updateListingShouldSaveGenderBrandAndColors() throws Exception {
        Listing listing = createTestListing("Original", testUser, testCity, testCategory);
        UUID brandId = UUID.randomUUID();
        ListingDto dto = ListingDto.builder()
                .title("Updated")
                .price(BigDecimal.TEN)
                .gender("unisex")
                .brandId(brandId)
                .colors(List.of("Blue"))
                .quantity(1)
                .condition(Condition.GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();

        mockMvc.perform(put("/api/listings/{id}", listing.getId())
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender").value("unisex"))
                .andExpect(jsonPath("$.brandId").value(brandId.toString()))
                .andExpect(jsonPath("$.colors[0]").value("Blue"));
    }

    @Test
    void replaceAssetsShouldPersistOnlyNewKeysAndQueueOptimization() throws Exception {
        Listing listing = createTestListing("Listing", testUser, testCity, testCategory);
        UUID id = listing.getId();
        MockMultipartFile image = new MockMultipartFile("images", "photo.PNG", "image/png", new byte[]{1});
        when(assetsService.putListingAsset(eq(id), any(UUID.class), eq("photo.PNG"), any(byte[].class), eq("image/png")))
                .thenAnswer(upload -> "listings/" + id + "/" + upload.getArgument(1) + ".png");

        mockMvc.perform(multipart("/api/listings/{id}/assets", id).file(image)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetKeys.length()").value(1))
                .andExpect(jsonPath("$.status").value("AWAITING_ASSETS"));

        mockMvc.perform(multipart(PUT, "/api/listings/{id}/assets", id).file(image)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AWAITING_ASSETS"));

        ArgumentCaptor<UUID> assetIds = ArgumentCaptor.forClass(UUID.class);
        verify(assetsService, times(2)).putListingAsset(eq(id), assetIds.capture(), eq("photo.PNG"),
                any(byte[].class), eq("image/png"));
        UUID replacementId = assetIds.getValue();
        mockMvc.perform(get("/api/listings/{id}", id).with(authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/listings/mine").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetKeys").doesNotExist())
                .andExpect(jsonPath("$[0].coverAssetKey").value(replacementId.toString()));
        verify(eventPublisher).publishListingAssetsAdded(testUser.getId(), id,
                List.of("listings/" + id + "/" + replacementId + ".png"));
    }

    @Test
    void assetUploadsShouldEnforceConfiguredSizeAndExtensions() throws Exception {
        Listing listing = createTestListing("Listing", testUser, testCity, testCategory);
        MockMultipartFile oversized = new MockMultipartFile("images", "photo.png", "image/png", new byte[1025]);
        MockMultipartFile valid = new MockMultipartFile("images", "photo.png", "image/png", new byte[]{1});
        MockMultipartFile unsupported = new MockMultipartFile("images", "photo.svg", "image/svg+xml", new byte[]{1});

        mockMvc.perform(multipart("/api/listings/{id}/assets", listing.getId()).file(oversized)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().is(413));

        mockMvc.perform(multipart(PUT, "/api/listings/{id}/assets", listing.getId()).file(valid).file(unsupported)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(assetsService, eventPublisher);
    }

    @Test
    void listingReadsShouldAllowAnonymousRequests() throws Exception {
        Listing listing = createTestListing("Nice Jeans", testUser, testCity, testCategory);

        mockMvc.perform(get("/api/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Nice Jeans"))
                .andExpect(jsonPath("$.content[0].description").doesNotExist())
                .andExpect(jsonPath("$.content[0].contactTypes").doesNotExist())
                .andExpect(jsonPath("$.content[0].assetKeys").doesNotExist())
                .andExpect(jsonPath("$.content[0].categoryId").doesNotExist())
                .andExpect(jsonPath("$.content[0].descriptionPreview").value("Default description"));

        mockMvc.perform(get("/api/listings/{id}", listing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nice Jeans"));
    }

    @Test
    void publicListingPagesReturnMetadataAndDoNotOverlap() throws Exception {
        for (int i = 0; i < 3; i++) {
            createTestListing("Page listing " + i, testUser, testCity, testCategory);
        }
        MvcResult first = mockMvc.perform(get("/api/listings").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andReturn();
        String firstId = objectMapper.readTree(first.getResponse().getContentAsString())
                .get("content").get(0).get("id").asText();
        String secondId = objectMapper.readTree(first.getResponse().getContentAsString())
                .get("content").get(1).get("id").asText();
        mockMvc.perform(get("/api/listings").param("size", "2").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id", Matchers.not(Matchers.isOneOf(firstId, secondId))))
                .andExpect(jsonPath("$.number").value(1));
        mockMvc.perform(get("/api/listings").param("size", "101"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/listings").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publicListingReadsOnlyReturnAvailableListings() throws Exception {
        Listing ready = createTestListing("Ready", testUser, testCity, testCategory);
        for (ListingStatus state : ListingStatus.values()) {
            if (state == ListingStatus.AVAILABLE) continue;
            Listing unavailable = createTestListing("Unavailable " + state, testUser, testCity, testCategory);
            unavailable.setStatus(state);
            listingRepository.saveAndFlush(unavailable);
            mockMvc.perform(get("/api/listings/{id}", unavailable.getId()))
                    .andExpect(status().isNotFound());
            mockMvc.perform(get("/api/listings/{id}", unavailable.getId()).with(authenticatedUser(testUser)))
                    .andExpect(status().isNotFound());
        }
        for (String endpoint : List.of("/api/listings", "/api/listings/search")) {
            String root = endpoint.equals("/api/listings") ? "$.content" : "$";
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(root + ".length()").value(1))
                    .andExpect(jsonPath(root + "[0].id").value(ready.getId().toString()))
                    .andExpect(jsonPath(root + "[0].status").value("AVAILABLE"));
        }
    }

    @Test
    void ownListingsIncludeEveryStatusAndPendingIncludesProcessingListings() throws Exception {
        User other = createSeller();
        List<String> ownIds = new ArrayList<>();
        List<String> pendingIds = new ArrayList<>();
        for (ListingStatus state : ListingStatus.values()) {
            Listing own = createTestListing("My " + state, testUser, testCity, testCategory);
            own.setStatus(state);
            listingRepository.saveAndFlush(own);
            ownIds.add(own.getId().toString());
            if (state == ListingStatus.AWAITING_ASSETS || state == ListingStatus.PENDING) pendingIds.add(own.getId().toString());
            Listing privateListing = createTestListing("Other " + state, other, testCity, testCategory);
            privateListing.setStatus(state);
            listingRepository.saveAndFlush(privateListing);
        }
        mockMvc.perform(get("/api/listings/mine"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/listings/mine").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", Matchers.containsInAnyOrder(ownIds.toArray())));
        mockMvc.perform(get("/api/listings/pending").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", Matchers.containsInAnyOrder(pendingIds.toArray())));
        mockMvc.perform(get("/api/listings/mine").param("size", "1").param("page", "0").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ownIds.getLast()));
        for (String query : List.of("size=0", "size=101", "page=-1")) {
            mockMvc.perform(get("/api/listings/mine?" + query).with(authenticatedUser(testUser)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void favoritesOnlyReturnListingsThatAreStillAvailable() throws Exception {
        User seller = createSeller();
        UUID availableId = null;
        for (ListingStatus state : ListingStatus.values()) {
            Listing listing = createTestListing("Favorite " + state, seller, testCity, testCategory);
            mockMvc.perform(post("/api/listings/{id}/favorite", listing.getId()).with(authenticatedUser(testUser)))
                    .andExpect(status().isOk());
            listing.setStatus(state);
            listingRepository.saveAndFlush(listing);
            if (state == ListingStatus.AVAILABLE) availableId = listing.getId();
        }
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(availableId.toString()));
    }

    @Test
    void listingWritesShouldRequireAuthentication() throws Exception {
        UUID id = UUID.randomUUID();
        MockMultipartFile image = new MockMultipartFile("images", "photo.png", "image/png", new byte[]{1});

        mockMvc.perform(post("/api/listings"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/listings/{id}", id))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/listings/{id}", id))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(multipart("/api/listings/{id}/assets", id).file(image))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(multipart(PUT, "/api/listings/{id}/assets", id).file(image))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(assetsService, eventPublisher);
    }

    @Test
    void favoritesShouldRequireAuthentication() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/listings/favorites"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/listings/{id}/favorite", id))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/listings/{id}/favorite", id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void favoriteMutationsShouldReturnNotFoundForMissingListings() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void favoritesShouldBeScopedToTheCurrentUserAndSupportRetries() throws Exception {
        User seller = createSeller();
        Listing listing = createTestListing("Favorite listing", seller, testCity, testCategory);
        createTestListing("Other listing", seller, testCity, testCategory);
        UUID id = listing.getId();

        mockMvc.perform(post("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(seller)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(post("/api/listings/{id}/favorite", id).with(authenticatedUser(seller)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_CANNOT_FAVORITE_OWN_LISTING"));
        mockMvc.perform(delete("/api/listings/{id}/favorite", id).with(authenticatedUser(seller)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ERR_CANNOT_FAVORITE_OWN_LISTING"));

        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].title").value(listing.getTitle()))
                .andExpect(jsonPath("$[0].cityNames[0]").value(testCity.getName()));

        mockMvc.perform(delete("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/listings/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void concurrentFavoriteRequestsShouldSaveTheListingOnce() throws Exception {
        User seller = createSeller();
        Listing listing = createTestListing("Favorite listing", seller, testCity, testCategory);
        CyclicBarrier startRequests = new CyclicBarrier(2);
        Callable<Void> favoriteRequest = () -> {
            startRequests.await(5, TimeUnit.SECONDS);
            mockMvc.perform(post("/api/listings/{id}/favorite", listing.getId())
                            .with(authenticatedUser(testUser)))
                    .andExpect(status().isOk());
            return null;
        };

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<Void> firstRequest = executor.submit(favoriteRequest);
            Future<Void> secondRequest = executor.submit(favoriteRequest);
            firstRequest.get(10, TimeUnit.SECONDS);
            secondRequest.get(10, TimeUnit.SECONDS);
        }

        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(listing.getId().toString()));
    }

    @Test
    void deletingAListingShouldRemoveItsFavorites() throws Exception {
        User seller = createSeller();
        Listing listing = createTestListing("Favorite listing", seller, testCity, testCategory);
        UUID id = listing.getId();

        mockMvc.perform(post("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/listings/{id}", id).with(authenticatedUser(seller)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void pendingListingsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/listings/pending"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void pendingListingsShouldReturnOnlyPendingListingsOwnedByTheCurrentUser() throws Exception {
        Listing ownPending = createPendingListing("My pending listing", testUser);
        createPendingListing("Another user's pending listing", createSeller());
        createTestListing("My available listing", testUser, testCity, testCategory);

        mockMvc.perform(get("/api/listings/pending").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ownPending.getId().toString()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void pendingListingsShouldPaginateNewestFirst() throws Exception {
        Listing older = createPendingListing("Older listing", testUser);
        Listing newer = createPendingListing("Newer listing", testUser);

        mockMvc.perform(get("/api/listings/pending").param("size", "1").param("page", "0")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(newer.getId().toString()));
        mockMvc.perform(get("/api/listings/pending").param("size", "1").param("page", "1")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(older.getId().toString()));
        mockMvc.perform(get("/api/listings/pending").param("size", "1").param("page", "2")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void pendingListingsShouldRejectInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/listings/pending").param("size", "0").with(authenticatedUser(testUser)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/listings/pending").param("page", "-1").with(authenticatedUser(testUser)))
                .andExpect(status().isBadRequest());
    }

    private Listing createPendingListing(String title, User user) {
        Listing listing = createTestListing(title, user, testCity, testCategory);
        listing.setStatus(ListingStatus.PENDING);
        return listingRepository.save(listing);
    }

    private User createSeller() {
        return userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .email("seller@example.com")
                .firstName("Test")
                .lastName("Seller")
                .build());
    }

    @Test
    public void deleteListingShouldReturnNoContent() throws Exception {
        ListingDto dto = ListingDto.builder()
                .title("Delete me")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .contactTypes(List.of(ContactType.MEETUP))
                .condition(Condition.GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();

        MvcResult result = mockMvc.perform(post("/api/listings")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(delete("/api/listings/" + id)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isNoContent());
    }
}
