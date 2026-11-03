package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.SizeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SizeGroupRepository extends JpaRepository<SizeGroup, UUID> {
}
