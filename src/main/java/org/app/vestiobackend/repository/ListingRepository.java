package org.app.vestiobackend.repository;

import jakarta.persistence.LockModeType;
import org.app.vestiobackend.entity.Listing;
import com.vestio.common.entity.enums.ListingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {

    @EntityGraph(type = EntityGraph.EntityGraphType.LOAD, attributePaths = {"user", "category"})
    Optional<Listing> findByIdAndStatus(UUID id, ListingStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Listing> findForUpdateById(UUID id);

    /**
     * Locks the listing for the duration of a favorite mutation.
     *
     * @param id The UUID of the listing.
     * @return The locked listing, if it exists.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Listing> findForFavoriteMutationById(UUID id);

}
