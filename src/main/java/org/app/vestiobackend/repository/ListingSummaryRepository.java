package org.app.vestiobackend.repository;

import java.util.ArrayList;
import com.vestio.common.entity.enums.ListingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Predicate;
import org.app.vestiobackend.dto.ListingSummaryDto;
import org.app.vestiobackend.entity.Favorite;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Scalar projections bypass eager entity hydration and never fetch-join collections. */
@Repository
public class ListingSummaryRepository {
    @PersistenceContext
    private EntityManager em;

    public Page<ListingSummaryDto> available(Pageable pageable) {
        return page((listing, query, builder) ->
                builder.equal(listing.get("status"), ListingStatus.AVAILABLE), pageable);
    }

    public Page<ListingSummaryDto> forUser(UUID userId, List<ListingStatus> statuses, Pageable pageable) {
        return page((listing, query, builder) -> {
            Predicate owner = builder.equal(listing.get("user").get("id"), userId);
            return statuses.isEmpty() ? owner : builder.and(owner, listing.get("status").in(statuses));
        }, pageable);
    }

    public Page<ListingSummaryDto> favorites(UUID userId, List<UUID> listingIds, Pageable pageable) {
        return page((listing, query, builder) -> {
            Root<Favorite> favorite = query.from(Favorite.class);
            query.orderBy(builder.desc(favorite.get("createdAt")), builder.asc(favorite.get("id")));
            return builder.and(builder.equal(favorite.get("listing").get("id"), listing.get("id")),
                    builder.equal(favorite.get("user").get("id"), userId),
                    builder.equal(listing.get("status"), ListingStatus.AVAILABLE),
                    listingIds == null ? builder.conjunction() : listing.get("id").in(listingIds));
        }, pageable);
    }

    public Page<ListingSummaryDto> page(Specification<Listing> spec, Pageable pageable) {
        List<ListingSummaryDto> content = search(spec, pageable);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> count = builder.createQuery(Long.class);
        Root<Listing> listing = count.from(Listing.class);
        Predicate predicate = spec.toPredicate(listing, count, builder);
        if (predicate != null) count.where(predicate);
        count.select(builder.countDistinct(listing.get("id")));
        count.orderBy(List.of());
        return new PageImpl<>(content, pageable, em.createQuery(count).getSingleResult());
    }

    public List<ListingSummaryDto> search(Specification<Listing> spec, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ListingSummaryDto> query = builder.createQuery(ListingSummaryDto.class);
        Root<Listing> listing = query.from(Listing.class);
        Join<Listing, User> user = listing.join("user");
        // The (listing_id, position) primary key guarantees at most one cover row.
        ListJoin<Listing, UUID> cover = listing.joinList("assetKeys", JoinType.LEFT);
        cover.on(builder.equal(cover.index(), 0));
        query.select(builder.construct(ListingSummaryDto.class,
                listing.get("id"), listing.get("title"), listing.get("price"), listing.get("condition"),
                listing.get("status"), user.get("id"), user.get("firstName"), user.get("lastName"),
                user.get("profileImageKey"), cover, listing.get("createdAt"),
                listing.get("category").get("name"), builder.substring(listing.get("description"), 1, 160)));
        Predicate predicate = spec.toPredicate(listing, query, builder);
        if (predicate != null) query.where(predicate);
        if (query.getOrderList().isEmpty()) {
            query.orderBy(builder.desc(listing.get("createdAt")), builder.desc(listing.get("id")));
        }
        TypedQuery<ListingSummaryDto> typedQuery = em.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult(Math.toIntExact(pageable.getOffset()));
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        List<ListingSummaryDto> summaries = typedQuery.getResultList();
        if (summaries.isEmpty()) return summaries;
        // Keep collection enrichment queries bounded to one page.
        for (int start = 0; start < summaries.size(); start += 100) {
            enrich(summaries.subList(start, Math.min(start + 100, summaries.size())));
        }
        return summaries;
    }

    private void enrich(List<ListingSummaryDto> summaries) {
        List<UUID> ids = summaries.stream().map(ListingSummaryDto::getId).toList();
        Map<UUID, List<String>> colors = values("""
                select l.id as listingId, color as value from Listing l join l.colors color
                where l.id in :ids order by l.id, color
                """, ids);
        Map<UUID, List<String>> cities = values("""
                select l.id as listingId, city.name as value from Listing l join l.cities city
                where l.id in :ids order by l.id, city.name, city.id
                """, ids);
        Map<UUID, List<String>> sizes = values("""
                select l.id as listingId, size.name as value from Listing l join l.sizes size
                where l.id in :ids order by l.id, size.name, size.id
                """, ids);
        for (ListingSummaryDto summary : summaries) {
            summary.getColors().addAll(colors.getOrDefault(summary.getId(), List.of()));
            summary.getCityNames().addAll(cities.getOrDefault(summary.getId(), List.of()));
            summary.getSizeNames().addAll(sizes.getOrDefault(summary.getId(), List.of()));
        }
    }

    private Map<UUID, List<String>> values(String jpql, List<UUID> ids) {
        Map<UUID, List<String>> result = new HashMap<>();
        for (Tuple row : em.createQuery(jpql, Tuple.class).setParameter("ids", ids).getResultList()) {
            result.computeIfAbsent(row.get("listingId", UUID.class), ignored -> new ArrayList<>())
                    .add(row.get("value", String.class));
        }
        return result;
    }
}
