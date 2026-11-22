package org.app.vestiobackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chats", uniqueConstraints = {
        @UniqueConstraint(name = "uq_chats_listing_buyer", columnNames = {"listing_id", "buyer_id"}),
        @UniqueConstraint(name = "uq_chats_offer", columnNames = "offer_id")
}, indexes = {
        @Index(name = "idx_chats_owner", columnList = "owner_id"),
        @Index(name = "idx_chats_buyer", columnList = "buyer_id")
})
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_id", nullable = false, updatable = false)
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false, updatable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false, updatable = false)
    private User buyer;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
