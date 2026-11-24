package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.EmailVerificationPin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationPinRepository extends JpaRepository<EmailVerificationPin, UUID> {
    Optional<EmailVerificationPin> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);
    void deleteByUserId(UUID userId);
}
