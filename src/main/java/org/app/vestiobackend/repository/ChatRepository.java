package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Chat;
import org.app.vestiobackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    interface ChatSummary {
        Chat getChat();
        Message getLastMessage();
        UUID getListingCoverAssetKey();
    }

    @Query(value = """
            SELECT c AS chat, m AS lastMessage, cover AS listingCoverAssetKey FROM Chat c
            JOIN FETCH c.listing JOIN FETCH c.owner JOIN FETCH c.buyer
            LEFT JOIN c.listing.assetKeys cover ON INDEX(cover) = 0
            LEFT JOIN Message m ON m.chat = c AND NOT EXISTS (
                SELECT newer.id FROM Message newer WHERE newer.chat = c
                AND (newer.createdAt > m.createdAt OR (newer.createdAt = m.createdAt AND newer.id > m.id))
            )
            WHERE c.owner.id = :userId OR c.buyer.id = :userId
            ORDER BY COALESCE(m.createdAt, c.createdAt) DESC, c.id DESC
            """, countQuery = "SELECT COUNT(c) FROM Chat c WHERE c.owner.id = :userId OR c.buyer.id = :userId")
    Page<ChatSummary> findForParticipant(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT c AS chat, m AS lastMessage, cover AS listingCoverAssetKey FROM Chat c
            JOIN FETCH c.listing JOIN FETCH c.owner JOIN FETCH c.buyer
            LEFT JOIN c.listing.assetKeys cover ON INDEX(cover) = 0
            LEFT JOIN Message m ON m.chat = c AND NOT EXISTS (
                SELECT newer.id FROM Message newer WHERE newer.chat = c
                AND (newer.createdAt > m.createdAt OR (newer.createdAt = m.createdAt AND newer.id > m.id))
            )
            WHERE c.id = :id AND (c.owner.id = :userId OR c.buyer.id = :userId)
            """)
    Optional<ChatSummary> findSummaryForParticipant(@Param("id") UUID id, @Param("userId") UUID userId);

    @EntityGraph(attributePaths = {"listing", "owner", "buyer"})
    @Query("SELECT c FROM Chat c WHERE c.id = :id AND (c.owner.id = :userId OR c.buyer.id = :userId)")
    Optional<Chat> findByIdForParticipant(@Param("id") UUID id, @Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Chat c WHERE c.id = :id AND (c.owner.id = :userId OR c.buyer.id = :userId)")
    Optional<Chat> findForMessage(@Param("id") UUID id, @Param("userId") UUID userId);

    Optional<Chat> findByOffer_Id(UUID offerId);

}
