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
import org.springframework.data.domain.Page;
import org.hibernate.SessionFactory;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
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
class ListingQueryIntegrationTest extends AbstractServiceTest {
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

        Page<ListingSummaryDto> page = listingService.getAllListings(12, 1);

        assertEquals(30, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertEquals(12, page.getContent().size());
        page.forEach(listing -> {
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
    void ownerPagesAvoidCountQueriesAndLoadOnlyRequestedListings() {
        Statistics statistics = seedListingsAndResetStatistics();

        List<ListingSummaryDto> page = listingService.getListingsForCurrentUser(12, 1);

        assertEquals(12, page.size());
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        // Current user lookup + card projection + three scalar collection queries.
        assertTrue(statistics.getPrepareStatementCount() <= 5,
                "Unexpected query count: " + statistics.getPrepareStatementCount());
    }

    @Test
    void searchPagesKeepSqlLimitsAndBatchCollectionsWithoutCounting() {
        Statistics statistics = seedListingsAndResetStatistics();
        ListingSearchFilters filters = new ListingSearchFilters("Listing", null, null, null, null,
                null, null, null, null, null, null, null);

        List<ListingSummaryDto> page = listingService.searchListings(filters, 12, 1);

        assertEquals(12, page.size());
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        assertTrue(statistics.getPrepareStatementCount() <= 4,
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

        List<ListingSummaryDto> favorites = listingService.getFavorites();

        assertEquals(2, favorites.size());
        assertEquals(0, statistics.getEntityStatistics(Listing.class.getName()).getLoadCount());
        assertEquals(0, statistics.getCollectionLoadCount());
        assertTrue(statistics.getPrepareStatementCount() <= 5,
                "Unexpected query count: " + statistics.getPrepareStatementCount());
    }

    @Test
    void emptySearchSkipsAllCollectionQueries() {
        Statistics statistics = seedListingsAndResetStatistics();
        ListingSearchFilters filters = new ListingSearchFilters("no-such-listing", null, null, null, null,
                null, null, null, null, null, null, null);

        assertTrue(listingService.searchListings(filters, 12, 0).isEmpty());

        assertEquals(1, statistics.getPrepareStatementCount());
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
