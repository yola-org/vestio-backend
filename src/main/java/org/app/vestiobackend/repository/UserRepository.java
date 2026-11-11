package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findAllByEmailIn(Collection<String> emails);

    Optional<User> findByCognitoSub(String cognitoSub);

    @Transactional
    @Modifying
    @Query("""
            update User u set u.cognitoSub = :cognitoSub
            where u.id = :id and (u.cognitoSub is null or u.cognitoSub = :cognitoSub)
            """)
    int linkCognitoSub(@Param("id") UUID id, @Param("cognitoSub") String cognitoSub);
}
