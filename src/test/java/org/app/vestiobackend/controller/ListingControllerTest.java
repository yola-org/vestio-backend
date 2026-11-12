package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
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
                .thenAnswer(upload -> "listings/" + id + "/" + upload.getArgument(1) + "/original.png");

        mockMvc.perform(multipart("/api/listings/{id}/assets", id).file(image)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetKeys.length()").value(1));

        mockMvc.perform(multipart(PUT, "/api/listings/{id}/assets", id).file(image)
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        ArgumentCaptor<UUID> assetIds = ArgumentCaptor.forClass(UUID.class);
        verify(assetsService, times(2)).putListingAsset(eq(id), assetIds.capture(), eq("photo.PNG"),
                any(byte[].class), eq("image/png"));
        UUID replacementId = assetIds.getValue();
        mockMvc.perform(get("/api/listings/{id}", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetKeys.length()").value(1))
                .andExpect(jsonPath("$.assetKeys[0]").value(replacementId.toString()));
        verify(eventPublisher).publishListingAssetsAdded(testUser.getId(), id,
                List.of("listings/" + id + "/" + replacementId + "/original.png"));
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
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Nice Jeans"));

        mockMvc.perform(get("/api/listings/{id}", listing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nice Jeans"));
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
        User seller = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .cognitoSub(UUID.randomUUID().toString())
                .email("seller@example.com")
                .firstName("Test")
                .lastName("Seller")
                .build());
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
        mockMvc.perform(delete("/api/listings/{id}/favorite", id).with(authenticatedUser(seller)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].title").value(listing.getTitle()))
                .andExpect(jsonPath("$[0].cityIds[0]").value(testCity.getId().toString()));

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
        Listing listing = createTestListing("Favorite listing", testUser, testCity, testCategory);
        CyclicBarrier startRequests = new CyclicBarrier(2);
        Callable<Void> favoriteRequest = () -> {
            startRequests.await(5, TimeUnit.SECONDS);
            mockMvc.perform(post("/api/listings/{id}/favorite", listing.getId())
                            .with(authenticatedUser(testUser)))
                    .andExpect(status().isOk());
            return null;
        };

        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstRequest = executor.submit(favoriteRequest);
            var secondRequest = executor.submit(favoriteRequest);
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
        Listing listing = createTestListing("Favorite listing", testUser, testCity, testCategory);
        UUID id = listing.getId();

        mockMvc.perform(post("/api/listings/{id}/favorite", id).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/listings/{id}", id).with(authenticatedUser(testUser)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/listings/favorites").with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
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
