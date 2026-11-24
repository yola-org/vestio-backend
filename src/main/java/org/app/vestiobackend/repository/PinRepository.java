package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Pin;
import org.app.vestiobackend.entity.PinPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PinRepository extends JpaRepository<Pin, UUID> {
    Optional<Pin> findFirstByUserIdAndPurposeOrderByCreatedAtDesc(UUID userId, PinPurpose purpose);
    void deleteByUserIdAndPurpose(UUID userId, PinPurpose purpose);
}
