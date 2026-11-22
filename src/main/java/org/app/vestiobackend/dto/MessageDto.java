package org.app.vestiobackend.dto;

import org.app.vestiobackend.entity.Message;
import org.app.vestiobackend.entity.enums.SystemMessageType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record MessageDto(UUID id, UUID chatId, UUID senderId, String content, boolean system,
                         SystemMessageType systemType, Map<String, Object> params, Instant createdAt) {
    public static MessageDto from(Message message) {
        return new MessageDto(message.getId(), message.getChat().getId(),
                message.getSender() == null ? null : message.getSender().getId(), message.getContent(),
                message.isSystem(), message.getSystemType(), message.getParams(), message.getCreatedAt());
    }
}
