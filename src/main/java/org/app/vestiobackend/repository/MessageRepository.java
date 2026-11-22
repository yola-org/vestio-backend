package org.app.vestiobackend.repository;

import org.app.vestiobackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByChat_Id(UUID chatId, Pageable pageable);
}
