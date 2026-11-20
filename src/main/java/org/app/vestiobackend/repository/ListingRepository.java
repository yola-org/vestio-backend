package org.app.vestiobackend.repository;

import jakarta.persistence.LockModeType;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {
    List<Listing> findByUser(User user);

    Page<Listing> findByUser(User user, Pageable pageable);

    List<Listing> findByStatus(ListingStatus status);

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

    Page<Listing> findByUserAndStatusIn(User user, List<ListingStatus> statuses, Pageable pageable);
}
