package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Offer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {
    boolean existsByUserIdAndListingId(UUID userId, UUID listingId);

    List<Offer> findByListingId(UUID listingId);

    Optional<Offer> findByIdAndListingId(UUID id, UUID listingId);

    @EntityGraph(attributePaths = {"listing", "user"})
    Page<Offer> findByUser_IdOrderByCreatedAtDescIdDesc(UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"listing", "user"})
    Page<Offer> findByListing_User_IdOrderByCreatedAtDescIdDesc(UUID userId, Pageable pageable);
}
