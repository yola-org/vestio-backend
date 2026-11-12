package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Favorite;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Persists user-to-listing favorites.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    /**
     * @param user The user who may have saved the listing.
     * @param listing The listing to check.
     * @return Whether the user has favorited the listing.
     */
    boolean existsByUserAndListing(User user, Listing listing);

    /**
     * Deletes the user's favorite for the listing, if present.
     *
     * @param user The user whose favorite should be deleted.
     * @param listing The listing to remove from favorites.
     */
    @Modifying
    @Query("delete from Favorite favorite where favorite.user = :user and favorite.listing = :listing")
    void deleteByUserAndListing(User user, Listing listing);

    /**
     * @param user The user whose favorites should be returned.
     * @return The user's favorites with their listings loaded, newest first.
     */
    @Query("""
            select favorite from Favorite favorite
            join fetch favorite.listing
            where favorite.user = :user
            order by favorite.createdAt desc, favorite.id
            """)
    List<Favorite> findByUser(User user);
}
