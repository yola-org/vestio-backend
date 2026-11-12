package org.app.vestiobackend.repository;

import jakarta.persistence.LockModeType;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Listing> findListingById(UUID id);

    /**
     * Locks the listing for the duration of a favorite mutation.
     *
     * @param id The UUID of the listing.
     * @return The locked listing, if it exists.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Listing> findForFavoriteMutationById(UUID id);
}
