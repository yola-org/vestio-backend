package org.app.vestiobackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pins")
@Getter @Setter @NoArgsConstructor
public class Pin {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PinPurpose purpose;
    @Column(nullable = false, length = 64)
    private String pinHash;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private int attempts;
}
