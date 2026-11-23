package org.app.vestiobackend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.vestio.common.entity.enums.Condition;
import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.ListingStatus;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Collections load eagerly through batched secondary selects, preserving SQL pagination. */
@Entity
@Table(name = "listings", indexes = {
        @Index(name = "idx_listing_category", columnList = "category_id"),
        @Index(name = "idx_listing_user_created_id", columnList = "user_id, createdAt DESC, id DESC"),
        @Index(name = "idx_listing_status_created_id", columnList = "status, createdAt DESC, id DESC")
})
@NamedEntityGraph(
        name = "listing-with-associations",
        attributeNodes = {
                @NamedAttributeNode("user"),
                @NamedAttributeNode("category"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "CLOB")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String gender;

    @Column(name = "brand_id")
    private UUID brandId;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listing_colors", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "color")
    private List<String> colors;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listing_asset_keys", joinColumns = @JoinColumn(name = "listing_id"))
    @OrderColumn(name = "position")
    @Column(name = "asset_key", nullable = false)
    private List<UUID> assetKeys;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listing_contact_types", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "contact_type")
    @Enumerated(EnumType.STRING)
    private List<ContactType> contactTypes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condition condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "listing_cities",
            joinColumns = @JoinColumn(name = "listing_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private Set<City> cities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "listing_sizes",
            joinColumns = @JoinColumn(name = "listing_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private Set<Size> sizes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
