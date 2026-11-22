package org.app.vestiobackend.repository;

import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.app.vestiobackend.dto.ListingSearchFilters;
import org.app.vestiobackend.entity.Listing;
import com.vestio.common.entity.enums.ListingStatus;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ListingSearchSpecification {
    private static final Pattern WHITE = Pattern.compile("\\bwhite\\b");
    private static final Pattern T_SHIRT = Pattern.compile("\\b(t[- ]?shirts?|tees?)\\b");

    private ListingSearchSpecification() {
    }

    public static Specification<Listing> matching(
            ListingSearchFilters filters, boolean postgres, Set<UUID> categoryIds) {
        return (listing, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(listing.get("status"), ListingStatus.AVAILABLE));

            List<Order> ordering = new ArrayList<>();
            if (StringUtils.hasText(filters.query())) {
                if (postgres) {
                    HibernateCriteriaBuilder hibernate = (HibernateCriteriaBuilder) builder;
                    Expression<String> search = hibernate.value(filters.query().strip());
                    predicates.add(builder.isTrue(hibernate.sql("""
                            search_vector @@ plainto_tsquery('pg_catalog.simple', normalize_listing_search_text(?))
                            """, Boolean.class, search)));
                    if (Listing.class.equals(query.getResultType())) {
                        ordering.add(builder.desc(hibernate.sql("""
                                ts_filter(search_vector, '{A}') @@
                                    plainto_tsquery('pg_catalog.simple', normalize_listing_search_text(?))
                                """, Boolean.class, search)));
                        ordering.add(builder.desc(hibernate.sql("""
                                ts_rank_cd(search_vector,
                                    plainto_tsquery('pg_catalog.simple', normalize_listing_search_text(?)))
                                """, Float.class, search)));

                        String normalized = normalize(filters.query());
                        List<Expression<Boolean>> catalogMatches = new ArrayList<>();
                        if (WHITE.matcher(normalized).find() && filters.colors().isEmpty()) {
                            catalogMatches.add(colorMatches(query, listing, builder, List.of("white")));
                        }
                        if (T_SHIRT.matcher(normalized).find() && filters.categoryId() == null) {
                            catalogMatches.add(hibernate.sql("normalize_listing_search_text(?) = 'tshirt'",
                                    Boolean.class, listing.get("category").get("name")));
                        }
                        if (!catalogMatches.isEmpty()) {
                            Expression<Integer> boost = builder.literal(0);
                            for (Expression<Boolean> catalogMatch : catalogMatches) {
                                boost = builder.sum(boost, builder.<Integer>selectCase()
                                        .when(catalogMatch, 1).otherwise(0));
                            }
                            ordering.add(builder.desc(boost));
                        }
                    }
                } else {
                    String pattern = "%" + normalize(filters.query())
                            .replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
                    predicates.add(builder.or(
                            builder.like(builder.lower(listing.get("title")), pattern, '!'),
                            builder.like(builder.lower(listing.get("description")), pattern, '!')));
                }
            }
            if (!categoryIds.isEmpty()) {
                predicates.add(listing.get("category").get("id").in(categoryIds));
            }
            if (filters.brandId() != null) {
                predicates.add(builder.equal(listing.get("brandId"), filters.brandId()));
            }
            if (!filters.cityIds().isEmpty()) {
                Subquery<Integer> city = query.subquery(Integer.class);
                city.select(builder.literal(1)).where(
                        city.correlate(listing).join("cities").get("id").in(filters.cityIds()));
                predicates.add(builder.exists(city));
            }
            if (!filters.sizeIds().isEmpty()) {
                Subquery<Integer> size = query.subquery(Integer.class);
                size.select(builder.literal(1)).where(
                        size.correlate(listing).join("sizes").get("id").in(filters.sizeIds()));
                predicates.add(builder.exists(size));
            }
            if (StringUtils.hasText(filters.gender())) {
                predicates.add(builder.equal(builder.lower(listing.get("gender")), normalize(filters.gender())));
            }
            if (!filters.colors().isEmpty()) {
                predicates.add(colorMatches(query, listing, builder, filters.colors()));
            }
            if (!filters.conditions().isEmpty()) {
                predicates.add(listing.get("condition").in(filters.conditions()));
            }
            if (!filters.contactTypes().isEmpty()) {
                Subquery<Integer> contactType = query.subquery(Integer.class);
                contactType.select(builder.literal(1)).where(
                        contactType.correlate(listing).join("contactTypes").in(filters.contactTypes()));
                predicates.add(builder.exists(contactType));
            }
            if (filters.minPrice() != null) {
                predicates.add(builder.ge(listing.get("price"), filters.minPrice()));
            }
            if (filters.maxPrice() != null) {
                predicates.add(builder.le(listing.get("price"), filters.maxPrice()));
            }

            if (Listing.class.equals(query.getResultType())) {
                ordering.add(builder.desc(listing.get("createdAt")));
                ordering.add(builder.desc(listing.get("id")));
                query.orderBy(ordering);
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate colorMatches(
            CriteriaQuery<?> query, Root<Listing> listing, CriteriaBuilder builder, List<String> selectedColors) {
        Subquery<Integer> colors = query.subquery(Integer.class);
        colors.select(builder.literal(1)).where(
                builder.lower(colors.correlate(listing).join("colors"))
                        .in(selectedColors.stream().map(ListingSearchSpecification::normalize).distinct().toList()));
        return builder.exists(colors);
    }

    private static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }
}
