package org.app.vestiobackend.controller;

import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.app.vestiobackend.entity.SizeGroup;
import org.app.vestiobackend.entity.Size;
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
import org.junit.jupiter.api.Nested;
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
    void createAndUpdateRejectSizesFromAnotherCategoryGroup() throws Exception {
        var shoeGroup = sizeGroupRepository.save(SizeGroup.builder()
                .id(UUID.randomUUID()).name("Shoes").build());
        var shoe = sizeRepository.save(Size.builder()
                .id(UUID.randomUUID()).name("EU 40").sizeGroup(shoeGroup).build());
        ListingDto dto = ListingDto.builder().title("Wrong size").price(new BigDecimal("20"))
                .quantity(1).condition(Condition.GOOD).contactTypes(List.of(ContactType.MEETUP))
                .cityIds(List.of(testCity.getId())).categoryId(testCategory.getId())
                .sizeIds(List.of(shoe.getId())).build();
        mockMvc.perform(post("/api/listings").with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        Listing listing = createTestListing("Original", testUser, testCity, testCategory);
        mockMvc.perform(put("/api/listings/" + listing.getId()).with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

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

    @Nested
    class Search {

        @ParameterizedTest
        @CsvSource({"colors, false", "colors, true", "cityIds, false", "cityIds, true", "sizeIds, false", "sizeIds, true", "conditions, false", "conditions, true",
                "contactTypes, false", "contactTypes, true"})
        void matchesAnySelectedValueWithoutDuplicateResults(String filter, boolean commaSeparated) throws Exception {
            Size otherSize = sizeRepository.save(Size.builder().id(UUID.randomUUID())
                    .name("L").sizeGroup(testSizeGroup).build());
            City otherCity = cityRepository.save(City.builder().id(UUID.randomUUID()).name("Rabat").build());
            Listing first = matchingListing();
            first.setColors(List.of("Blue", "BLUE", "Red"));
            first.setCities(new LinkedHashSet<>(List.of(testCity, otherCity)));
            first.setSizes(new LinkedHashSet<>(List.of(testSize, otherSize)));
            listingRepository.save(first);
            Listing second = matchingListing();
            second.setColors(List.of("Red"));
            second.setCities(new LinkedHashSet<>(List.of(otherCity)));
            second.setSizes(new LinkedHashSet<>(List.of(otherSize)));
            second.setCondition(Condition.GOOD);
            second.setContactTypes(List.of(ContactType.PARCEL));
            listingRepository.save(second);
            Listing excluded = matchingListing();
            excluded.setColors(List.of("Black"));
            excluded.setCities(new LinkedHashSet<>(List.of()));
            excluded.setSizes(new LinkedHashSet<>(List.of()));
            excluded.setCondition(Condition.NEW_WITH_TAGS);
            excluded.setContactTypes(List.of(ContactType.MEETUP));
            listingRepository.save(excluded);

            String[] values = switch (filter) {
                case "colors" -> new String[]{"  bLuE  ", " RED "};
                case "cityIds" -> new String[]{testCity.getId().toString(), otherCity.getId().toString()};
                case "sizeIds" -> new String[]{testSize.getId().toString(), otherSize.getId().toString()};
                case "conditions" -> new String[]{"VERY_GOOD", "GOOD"};
                case "contactTypes" -> new String[]{"DELIVERY", "PARCEL"};
                default -> throw new IllegalArgumentException(filter);
            };
            String[] parameters = commaSeparated ? new String[]{String.join(",", values)} : values;
            MockHttpServletRequestBuilder request = get("/api/listings/search").param(filter, parameters);
            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[*].id", containsInAnyOrder(first.getId().toString(), second.getId().toString())));
            mockMvc.perform(get("/api/listings/search").param(filter, parameters).param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(second.getId().toString()));
            mockMvc.perform(get("/api/listings/search").param(filter, parameters)
                            .param("size", "1").param("page", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(first.getId().toString()));
            mockMvc.perform(get("/api/listings/search").param(filter, parameters)
                            .param("size", "1").param("page", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void combinesMultipleSelections() throws Exception {
            Listing match = listingRepository.save(matchingListing());
            Listing wrongColor = matchingListing();
            wrongColor.setColors(List.of("Black"));
            listingRepository.save(wrongColor);
            Listing wrongCity = matchingListing();
            wrongCity.setCities(new LinkedHashSet<>(List.of()));
            listingRepository.save(wrongCity);
            Listing wrongSize = matchingListing();
            wrongSize.setSizes(new LinkedHashSet<>(List.of()));
            listingRepository.save(wrongSize);
            Listing wrongCondition = matchingListing();
            wrongCondition.setCondition(Condition.NEW_WITH_TAGS);
            listingRepository.save(wrongCondition);
            Listing wrongContact = matchingListing();
            wrongContact.setContactTypes(List.of(ContactType.MEETUP));
            listingRepository.save(wrongContact);

            mockMvc.perform(get("/api/listings/search")
                            .param("colors", "Blue", "Red")
                            .param("cityIds", UUID.randomUUID().toString(), testCity.getId().toString())
                            .param("sizeIds", UUID.randomUUID().toString(), testSize.getId().toString())
                            .param("conditions", "GOOD", "VERY_GOOD")
                            .param("contactTypes", "DELIVERY", "PARCEL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
        }

        @Test
        void emptySelectionsDoNotRestrictResults() throws Exception {
            listingRepository.save(matchingListing());
            mockMvc.perform(get("/api/listings/search")
                            .param("colors", "").param("cityIds", "").param("sizeIds", "").param("conditions", "").param("contactTypes", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @ParameterizedTest
        @CsvSource({
                "query, '  jAcKeT  '",
                "categoryId, fixture",
                "brandId, fixture",
                "cityIds, fixture",
                "sizeIds, fixture",
                "gender, '  WOMEN  '",
                "colors, '  bLuE  '",
                "conditions, VERY_GOOD",
                "contactTypes, DELIVERY",
                "minPrice, 50",
                "maxPrice, 50"
        })
        void filtersIndividually(String name, String value) throws Exception {
            Listing match = listingRepository.save(matchingListing());
            Listing distractor = createTestListing("Unrelated listing", testUser, testCity, testCategory);
            distractor.setCategory(categoryRepository.save(Category.builder()
                    .id(UUID.randomUUID()).name("Accessories").build()));
            distractor.setCities(new LinkedHashSet<>(List.of(cityRepository.save(City.builder()
                    .id(UUID.randomUUID()).name("Rabat").build()))));
            distractor.setPrice(new BigDecimal(name.equals("maxPrice") ? "75" : "25"));
            listingRepository.save(distractor);

            String parameter = value.equals("fixture") ? filtersFor(match).get(name) : value;
            mockMvc.perform(get("/api/listings/search").param(name, parameter))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
        }

        @Test
        void categoryBrowsingIncludesDescendantsWhileExactSearchDoesNot() throws Exception {
            Category child = categoryRepository.save(Category.builder()
                    .id(UUID.randomUUID()).name("Jackets").parent(testCategory).build());
            Category grandchild = categoryRepository.save(Category.builder()
                    .id(UUID.randomUUID()).name("Denim").parent(child).build());
            Category unrelated = categoryRepository.save(Category.builder()
                    .id(UUID.randomUUID()).name("Accessories").build());

            Listing parentListing = listingRepository.save(matchingListing());
            Listing grandchildListing = matchingListing();
            grandchildListing.setCategory(grandchild);
            listingRepository.save(grandchildListing);
            Listing hidden = matchingListing();
            hidden.setCategory(child);
            hidden.setStatus(ListingStatus.HIDDEN);
            listingRepository.save(hidden);
            Listing unrelatedListing = matchingListing();
            unrelatedListing.setCategory(unrelated);
            listingRepository.save(unrelatedListing);

            mockMvc.perform(get("/api/listings/search")
                            .param("categoryId", testCategory.getId().toString())
                            .param("includeSubcategories", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
            mockMvc.perform(get("/api/listings/search")
                            .param("categoryId", child.getId().toString())
                            .param("includeSubcategories", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(grandchildListing.getId().toString()));
            mockMvc.perform(get("/api/listings/search")
                            .param("categoryId", testCategory.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(parentListing.getId().toString()));
        }

        @Test
        void combinesEveryFilterAndExcludesListingsThatFailAnyOne() throws Exception {
            Listing match = listingRepository.save(matchingListing());
            Map<String, String> filters = filtersFor(match);
            for (String name : filters.keySet()) {
                Listing distractor = matchingListing();
                switch (name) {
                    case "query" -> distractor.setTitle("Coat");
                    case "categoryId" -> distractor.setCategory(categoryRepository.save(Category.builder()
                            .id(UUID.randomUUID()).name("Accessories").build()));
                    case "brandId" -> distractor.setBrandId(null);
                    case "cityIds" -> distractor.setCities(new LinkedHashSet<>(List.of()));
                    case "sizeIds" -> distractor.setSizes(new LinkedHashSet<>(List.of()));
                    case "gender" -> distractor.setGender("Men");
                    case "colors" -> distractor.setColors(List.of("Black"));
                    case "conditions" -> distractor.setCondition(Condition.GOOD);
                    case "contactTypes" -> distractor.setContactTypes(List.of(ContactType.MEETUP));
                    case "minPrice" -> distractor.setPrice(new BigDecimal("49.99"));
                    case "maxPrice" -> distractor.setPrice(new BigDecimal("50.01"));
                    default -> throw new IllegalArgumentException(name);
                }
                listingRepository.save(distractor);
            }
            Listing hidden = matchingListing();
            hidden.setStatus(ListingStatus.HIDDEN);
            listingRepository.save(hidden);

            MockHttpServletRequestBuilder request = get("/api/listings/search").param("size", "1");
            filters.forEach(request::param);
            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
            mockMvc.perform(request.param("page", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void searchesDescriptionsCaseInsensitively() throws Exception {
            Listing match = matchingListing();
            match.setTitle("Coat");
            match.setDescription("Contains 50% WOOL");
            listingRepository.save(match);
            Listing distractor = matchingListing();
            distractor.setDescription("Contains 50% cotton");
            listingRepository.save(distractor);

            mockMvc.perform(get("/api/listings/search").param("query", "wool"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL", matches = "jdbc:postgresql:.*")
        void matchesTermsAcrossFieldsAndRefreshesTheSearchVectorOnUpdate() throws Exception {
            Listing match = matchingListing();
            match.setDescription("Lined with wool");
            listingRepository.save(match);
            Listing partialMatch = matchingListing();
            partialMatch.setDescription("Lined with cotton");
            listingRepository.save(partialMatch);

            mockMvc.perform(get("/api/listings/search").param("query", "WOOL jacket").param("colors", "blue"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(match.getId().toString()));

            match.setDescription(null);
            listingRepository.save(match);
            mockMvc.perform(get("/api/listings/search").param("query", "wool jacket"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL", matches = "jdbc:postgresql:.*")
        void ranksTitleAndCatalogMatchesAcrossCitiesAndWithinOneCity() throws Exception {
            City otherCity = cityRepository.save(City.builder().id(UUID.randomUUID()).name("Rabat").build());
            Category tShirts = categoryRepository.save(Category.builder().id(UUID.randomUUID()).name("T-shirts").build());

            Listing catalogMatch = matchingListing();
            catalogMatch.setTitle("White T-shirt");
            catalogMatch.setCategory(tShirts);
            catalogMatch.setColors(List.of("White", "WHITE"));
            catalogMatch.setCities(new LinkedHashSet<>(List.of(testCity, otherCity)));
            listingRepository.save(catalogMatch);

            Listing titleMatch = matchingListing();
            titleMatch.setTitle("White tee");
            listingRepository.save(titleMatch);

            Listing descriptionMatch = matchingListing();
            descriptionMatch.setDescription("White tshirt");
            descriptionMatch.setCities(new LinkedHashSet<>(List.of(otherCity)));
            listingRepository.save(descriptionMatch);

            mockMvc.perform(get("/api/listings/search").param("query", "white tshirt"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].id").value(catalogMatch.getId().toString()))
                    .andExpect(jsonPath("$[1].id").value(titleMatch.getId().toString()))
                    .andExpect(jsonPath("$[2].id").value(descriptionMatch.getId().toString()));

            mockMvc.perform(get("/api/listings/search").param("query", "white tee")
                            .param("cityIds", otherCity.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(catalogMatch.getId().toString()))
                    .andExpect(jsonPath("$[1].id").value(descriptionMatch.getId().toString()));
        }

        @Test
        void paginatesAvailableListingsNewestFirstWithoutFilters() throws Exception {
            Listing older = listingRepository.save(matchingListing());
            Listing newer = listingRepository.save(matchingListing());
            for (ListingStatus state : ListingStatus.values()) {
                if (state != ListingStatus.AVAILABLE) {
                    Listing unavailable = matchingListing();
                    unavailable.setStatus(state);
                    listingRepository.save(unavailable);
                }
            }

            mockMvc.perform(get("/api/listings/search").param("size", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(newer.getId().toString()));
            mockMvc.perform(get("/api/listings/search").param("size", "1").param("page", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(older.getId().toString()));
            mockMvc.perform(get("/api/listings/search").param("query", "  "))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @ParameterizedTest
        @CsvSource({"colors, 'Blue,   '", "categoryId, invalid", "conditions, invalid", "contactTypes, invalid",
                "cityIds, invalid", "sizeIds, invalid", "conditions, 'GOOD,invalid'", "contactTypes, 'DELIVERY,invalid'",
                "minPrice, -1", "maxPrice, -1", "page, -1", "size, 0", "size, 101"})
        void rejectsInvalidParameters(String name, String value) throws Exception {
            mockMvc.perform(get("/api/listings/search").param(name, value))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void rejectsInvertedPriceRange() throws Exception {
            mockMvc.perform(get("/api/listings/search").param("minPrice", "51").param("maxPrice", "50"))
                    .andExpect(status().isBadRequest());
        }

        private Listing matchingListing() {
            return Listing.builder()
                    .id(UUID.randomUUID())
                    .title("Denim Jacket")
                    .price(new BigDecimal("50"))
                    .quantity(1)
                    .status(ListingStatus.AVAILABLE)
                    .user(testUser)
                    .category(testCategory)
                    .brandId(testSizeGroup.getId())
                    .cities(new LinkedHashSet<>(List.of(testCity)))
                    .sizes(new LinkedHashSet<>(List.of(testSize)))
                    .gender("Women")
                    .colors(List.of("Blue", "BLUE"))
                    .condition(Condition.VERY_GOOD)
                    .contactTypes(List.of(ContactType.DELIVERY, ContactType.PARCEL))
                    .build();
        }

        private Map<String, String> filtersFor(Listing listing) {
            Map<String, String> filters = new LinkedHashMap<>();
            filters.put("query", "jacket");
            filters.put("categoryId", testCategory.getId().toString());
            filters.put("brandId", listing.getBrandId().toString());
            filters.put("cityIds", testCity.getId().toString());
            filters.put("sizeIds", testSize.getId().toString());
            filters.put("gender", "women");
            filters.put("colors", "blue");
            filters.put("conditions", "VERY_GOOD");
            filters.put("contactTypes", "DELIVERY");
            filters.put("minPrice", "50");
            filters.put("maxPrice", "50");
            return filters;
        }

    }
}
