package org.app.vestiobackend.service;

import org.app.vestiobackend.entity.Size;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Favorite;
import org.app.vestiobackend.repository.FavoriteRepository;
import org.app.vestiobackend.dto.ListingSearchFilters;
import org.app.vestiobackend.dto.ListingSummaryDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.dto.PaginatedResult;
import org.hibernate.SessionFactory;
import org.hibernate.Hibernate;
import com.vestio.common.entity.enums.ListingStatus;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Transactional
class ListingServiceQueryIT extends AbstractServiceIT {
    @Autowired
    private ListingService listingService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private FavoriteRepository favoriteRepository;

    private final Map<UUID, UUID> coverKeys = new HashMap<>();

    @Test
    void publicPagesProjectCardFieldsWithoutHydratingEntities() {
        Statistics statistics = seedListingsAndResetStatistics();

        PaginatedResult<ListingSummaryDto> page = listingService.getAllListings(12, 1);

        assertEquals(30, page.totalElements());
        assertEquals(3, page.totalPages());
        assertEquals(12, page.items().size());
        page.items().forEach(listing -> {
            assertEquals(coverKeys.get(listing.getId()), listing.getCoverAssetKey());
            assertEquals(2, listing.getColors().size());
            assertEquals(List.of(testSize.getName()), listing.getSizeNames());
            assertEquals(List.of(testCity.getName()), listing.getCityNames());
        });
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        // Card projection + count + three scalar collection queries.
        assertTrue(statistics.getPrepareStatementCount() <= 5,
                "Unexpected query count: " + statistics.getPrepareStatementCount());
    }

    @Test
    void ownerPagesCountAndLoadOnlyRequestedListings() {
        Statistics statistics = seedListingsAndResetStatistics();

        PaginatedResult<ListingSummaryDto> page = listingService.getListingsForCurrentUser(12, 1, null);

        assertEquals(12, page.items().size());
        assertEquals(30, page.totalElements());
        assertEquals(3, page.totalPages());
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        // Current user lookup + card projection + count + three scalar collection queries.
        assertTrue(statistics.getPrepareStatementCount() <= 6,
                "Unexpected query count: " + statistics.getPrepareStatementCount());
    }

    @Test
    void searchPagesKeepSqlLimitsAndCountWithoutHydratingEntities() {
        Statistics statistics = seedListingsAndResetStatistics();
        ListingSearchFilters filters = new ListingSearchFilters("Listing", null, null, null, null,
                null, null, null, null, null, null, null);

        PaginatedResult<ListingSummaryDto> page = listingService.searchListings(filters, 12, 1);

        assertEquals(12, page.items().size());
        assertEquals(30, page.totalElements());
        assertEquals(3, page.totalPages());
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        assertTrue(statistics.getPrepareStatementCount() <= 5,
                "Unexpected query count: " + statistics.getPrepareStatementCount());
    }

    @Test
    void favoritesProjectSummariesWithoutHydratingListingsOrCollections() {
        Statistics statistics = seedListingsAndResetStatistics();
        User buyer = createTestUser("summary-buyer@example.com");
        setupSecurityContext(buyer);
        for (UUID id : coverKeys.keySet().stream().limit(2).toList()) {
            Favorite favorite = new Favorite();
            favorite.setUser(buyer);
            favorite.setListing(entityManager.getReference(Listing.class, id));
            favoriteRepository.save(favorite);
        }
        entityManager.flush();
        entityManager.clear();
        statistics.clear();

        PaginatedResult<ListingSummaryDto> favorites = listingService.getFavorites(12, 0, null);

        assertEquals(2, favorites.items().size());
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        assertTrue(statistics.getPrepareStatementCount() <= 6,
                "Unexpected query count: " + statistics.getPrepareStatementCount());
    }

    @Test
    void emptySearchSkipsAllCollectionQueries() {
        Statistics statistics = seedListingsAndResetStatistics();
        ListingSearchFilters filters = new ListingSearchFilters("no-such-listing", null, null, null, null,
                null, null, null, null, null, null, null);

        assertTrue(listingService.searchListings(filters, 12, 0).items().isEmpty());

        assertEquals(2, statistics.getPrepareStatementCount());
        assertEquals(0, statistics.getEntityLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
    }

    @Test
    void detailStillLoadsFullEagerCollections() {
        Statistics statistics = seedListingsAndResetStatistics();
        UUID id = coverKeys.keySet().iterator().next();

        Listing listing = listingRepository.findByIdAndStatus(id, ListingStatus.AVAILABLE).orElseThrow();

        assertTrue(Hibernate.isInitialized(listing.getCities()));
        assertTrue(Hibernate.isInitialized(listing.getSizes()));
        assertTrue(Hibernate.isInitialized(listing.getColors()));
        assertTrue(Hibernate.isInitialized(listing.getAssetKeys()));
        assertTrue(Hibernate.isInitialized(listing.getContactTypes()));
        assertEquals(1, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(2, listing.getAssetKeys().size());
    }

    private Statistics seedListingsAndResetStatistics() {
        for (int i = 0; i < 30; i++) {
            Listing listing = createTestListing("Listing " + i, testUser, testCity, testCategory);
            listing.setSizes(new LinkedHashSet<>(List.of(
                    entityManager.getReference(Size.class, testSize.getId()))));
            listing.setColors(List.of("Black", "White"));
            UUID cover = UUID.randomUUID();
            listing.setAssetKeys(List.of(cover, UUID.randomUUID()));
            coverKeys.put(listing.getId(), cover);
        }
        entityManager.flush();
        entityManager.clear();
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        return statistics;
    }
}
