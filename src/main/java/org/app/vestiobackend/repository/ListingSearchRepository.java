package org.app.vestiobackend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.app.vestiobackend.entity.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Executes specification-based listing searches with an entity graph applied,
 * avoiding N+1 queries for associations (user, category, cities, sizes).
 */
@Repository
public class ListingSearchRepository {

    @PersistenceContext
    private EntityManager em;

    public Slice<Listing> search(Specification<Listing> spec, Pageable pageable) {
        CriteriaQuery<Listing> query = em.getCriteriaBuilder().createQuery(Listing.class);
        Root<Listing> root = query.from(Listing.class);
        Predicate predicate = spec.toPredicate(root, query, em.getCriteriaBuilder());
        if (predicate != null) query.where(predicate);

        TypedQuery<Listing> typedQuery = em.createQuery(query)
                .setHint("jakarta.persistence.fetchgraph", em.getEntityGraph("listing-with-associations"))
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize() + 1); // +1 to detect hasNext

        List<Listing> results = typedQuery.getResultList();
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) results = results.subList(0, pageable.getPageSize());
        return new SliceImpl<>(results, pageable, hasNext);
    }
}
