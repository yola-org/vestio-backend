package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);

    @Query("select category.id from Category category where category.parent.id in :parentIds")
    List<UUID> findChildIds(Collection<UUID> parentIds);
}
