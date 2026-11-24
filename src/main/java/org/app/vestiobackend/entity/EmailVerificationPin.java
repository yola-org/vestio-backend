package org.app.vestiobackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verification_pins")
@Getter @Setter @NoArgsConstructor
public class EmailVerificationPin {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false, length = 64)
    private String pinHash;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private int attempts;
}
