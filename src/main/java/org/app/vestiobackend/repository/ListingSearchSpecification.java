package org.app.vestiobackend.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.app.vestiobackend.dto.ListingSearchFilters;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.enums.ListingStatus;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ListingSearchSpecification {
    private static final Pattern WHITE = Pattern.compile("\\bwhite\\b");
    private static final Pattern T_SHIRT = Pattern.compile("\\b(t[- ]?shirts?|tees?)\\b");

    private ListingSearchSpecification() {
    }

    public static Specification<Listing> matching(ListingSearchFilters filters, boolean postgres) {
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
                        if (WHITE.matcher(normalized).find() && !StringUtils.hasText(filters.color())) {
                            catalogMatches.add(colorMatches(query, listing, builder, "white"));
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
            if (filters.categoryId() != null) {
                predicates.add(builder.equal(listing.get("category").get("id"), filters.categoryId()));
            }
            if (filters.brandId() != null) {
                predicates.add(builder.equal(listing.get("brandId"), filters.brandId()));
            }
            if (filters.cityId() != null) {
                var city = query.subquery(Integer.class);
                city.select(builder.literal(1)).where(
                        builder.equal(city.correlate(listing).join("cities").get("id"), filters.cityId()));
                predicates.add(builder.exists(city));
            }
            if (filters.sizeId() != null) {
                var size = query.subquery(Integer.class);
                size.select(builder.literal(1)).where(
                        builder.equal(size.correlate(listing).join("sizes").get("id"), filters.sizeId()));
                predicates.add(builder.exists(size));
            }
            if (StringUtils.hasText(filters.gender())) {
                predicates.add(builder.equal(builder.lower(listing.get("gender")), normalize(filters.gender())));
            }
            if (StringUtils.hasText(filters.color())) {
                predicates.add(colorMatches(query, listing, builder, normalize(filters.color())));
            }
            if (filters.condition() != null) {
                predicates.add(builder.equal(listing.get("condition"), filters.condition()));
            }
            if (filters.contactType() != null) {
                predicates.add(builder.isMember(filters.contactType(), listing.get("contactTypes")));
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
            CriteriaQuery<?> query, Root<Listing> listing, CriteriaBuilder builder, String color) {
        var colors = query.subquery(Integer.class);
        colors.select(builder.literal(1)).where(
                builder.equal(builder.lower(colors.correlate(listing).join("colors")), color));
        return builder.exists(colors);
    }

    private static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }
}
