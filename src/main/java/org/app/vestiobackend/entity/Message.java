package org.app.vestiobackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.app.vestiobackend.entity.enums.SystemMessageType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_chat_created", columnList = "chat_id, created_at, id"),
        @Index(name = "idx_messages_sender", columnList = "sender_id")
})
public class Message {
    public static final int MAX_CONTENT_LENGTH = 4000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", updatable = false)
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_system", nullable = false)
    private boolean system;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_type", length = 50)
    private SystemMessageType systemType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> params;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    @PreUpdate
    private void validateMessage() {
        if (chat == null) {
            throw new IllegalArgumentException("A message requires a chat");
        }
        if (system) {
            if (systemType == null || params == null) {
                throw new IllegalArgumentException("A system message requires a system type and params");
            }
            systemType.validateParams(params);
        } else {
            if (sender == null || content == null || content.isBlank()) {
                throw new IllegalArgumentException("A regular message requires a sender and nonblank content");
            }
            if (content.length() > MAX_CONTENT_LENGTH) {
                throw new IllegalArgumentException("Message content cannot exceed 4000 characters");
            }
            if (systemType != null || params != null) {
                throw new IllegalArgumentException("Only system messages may have a system type and params");
            }
        }
        if (sender == null) {
            return;
        }
        UUID senderId = sender.getId();
        if (senderId == null || (!senderId.equals(chat.getOwner().getId())
                && !senderId.equals(chat.getBuyer().getId()))) {
            throw new IllegalArgumentException("The sender must be the chat owner or buyer");
        }
    }
}
