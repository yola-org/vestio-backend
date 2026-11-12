package org.app.vestiobackend.service;

import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
public class ListingServiceIntegrationTest extends AbstractServiceTest {

    @Autowired
    private ListingService listingService;

    @Test
    public void createListingShouldPersistListing() {
        ListingDto dto = ListingDto.builder()
                .title("Cool T-shirt")
                .price(new BigDecimal("19.99"))
                .quantity(1)
                .contactTypes(List.of(ContactType.MEETUP))
                .condition(Condition.NEW_WITH_TAGS)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .colors(List.of("Blue", "White"))
                .build();

        ListingDto created = listingService.createListing(dto);

        assertNotNull(created.getId());
        assertEquals("Cool T-shirt", created.getTitle());
        assertEquals(ListingStatus.AWAITING_ASSETS, created.getStatus());
        assertEquals(testUser.getId(), created.getUserId());
        assertTrue(created.getCityIds().contains(testCity.getId()));
    }

    @Test
    public void createListingWithSizeShouldPersistSize() {
        ListingDto dto = ListingDto.builder()
                .title("T-shirt with size")
                .price(new BigDecimal("25.00"))
                .quantity(1)
                .contactTypes(List.of(ContactType.DELIVERY))
                .condition(Condition.NEW_WITH_TAGS)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .sizeIds(List.of(testSize.getId()))
                .build();

        ListingDto created = listingService.createListing(dto);

        assertTrue(created.getSizeIds().contains(testSize.getId()));
    }

    @Test
    public void updateListingShouldModifyExistingListing() {
        ListingDto createDto = ListingDto.builder()
                .title("Original Title")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .contactTypes(List.of(ContactType.DELIVERY))
                .condition(Condition.GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();
        ListingDto created = listingService.createListing(createDto);

        ListingDto updateDto = ListingDto.builder()
                .title("Updated Title")
                .price(new BigDecimal("15.00"))
                .quantity(2)
                .contactTypes(List.of(ContactType.PARCEL))
                .condition(Condition.VERY_GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();

        ListingDto updated = listingService.updateListing(created.getId(), updateDto);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals(new BigDecimal("15.00"), updated.getPrice());
        assertEquals(ListingStatus.AWAITING_ASSETS, updated.getStatus());
    }

    @Test
    public void deleteListingShouldRemoveFromDb() {
        ListingDto dto = ListingDto.builder()
                .title("To be deleted")
                .price(new BigDecimal("5.00"))
                .quantity(1)
                .contactTypes(List.of(ContactType.MEETUP))
                .condition(Condition.SATISFACTORY)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();
        ListingDto created = listingService.createListing(dto);

        listingService.deleteListing(created.getId());

        assertThrows(ResourceNotFoundException.class, () -> listingService.getListing(created.getId()));
    }

    @Test
    public void getListingWithNonExistentIdShouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> listingService.getListing(UUID.randomUUID()));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void getPendingListingsShouldFilterByCurrentUserAndStatusAndMapListingDetails() {
        Listing pending = createPendingListing("My pending listing", testUser);
        User otherUser = createTestUser("other@example.com");
        Listing otherPending = createPendingListing("Other pending listing", otherUser);
        createTestListing("My available listing", testUser, testCity, testCategory);

        List<ListingDto> listings = listingService.getPendingListingsForUser(12, 0);

        assertEquals(1, listings.size());
        ListingDto result = listings.getFirst();
        assertEquals(pending.getId(), result.getId());
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals(ListingStatus.PENDING, result.getStatus());
        assertEquals(testCategory.getId(), result.getCategoryId());
        assertEquals(List.of(testCity.getId()), result.getCityIds());
        assertEquals(List.of(testSize.getId()), result.getSizeIds());

        setupSecurityContext(otherUser);
        assertEquals(List.of(otherPending.getId()), listingService.getPendingListingsForUser(12, 0)
                .stream().map(ListingDto::getId).toList());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void getPendingListingsShouldReturnNewestFirstAcrossPages() {
        assertTrue(listingService.getPendingListingsForUser(1, 0).isEmpty());
        Listing older = createPendingListing("Older pending listing", testUser);
        Listing newer = createPendingListing("Newer pending listing", testUser);

        assertEquals(List.of(newer.getId()), listingService.getPendingListingsForUser(1, 0)
                .stream().map(ListingDto::getId).toList());
        assertEquals(List.of(older.getId()), listingService.getPendingListingsForUser(1, 1)
                .stream().map(ListingDto::getId).toList());
        assertTrue(listingService.getPendingListingsForUser(1, 2).isEmpty());
    }

    private Listing createPendingListing(String title, User owner) {
        Listing listing = createTestListing(title, owner, testCity, testCategory);
        listing.setStatus(ListingStatus.PENDING);
        listing.setSizes(List.of(testSize));
        return listingRepository.save(listing);
    }

    @Test
    public void updateListingByNonOwnerShouldThrowException() {
        // Given
        Listing listing = createTestListing("Other's Listing", testUser, testCity, testCategory);
        
        // Switch user
        setupSecurityContext(createTestUser("other@example.com"));

        ListingDto updateDto = ListingDto.builder()
                .title("Try to update")
                .build();

        // When & Then
        assertThrows(UnAuthorizedException.class, () -> listingService.updateListing(listing.getId(), updateDto));
    }

    @Test
    public void deleteListingByNonOwnerShouldThrowException() {
        // Given
        Listing listing = createTestListing("Other's Listing", testUser, testCity, testCategory);
        
        // Switch user
        setupSecurityContext(createTestUser("other@example.com"));

        // When & Then
        assertThrows(UnAuthorizedException.class, () -> listingService.deleteListing(listing.getId()));
    }
}
