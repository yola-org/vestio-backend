package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {
    List<UserPreference> findByUser(User user);
    List<UserPreference> findByUserEmail(String email);
    void deleteByUser(User user);
}
