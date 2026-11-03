package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findByUser(User user);
}
