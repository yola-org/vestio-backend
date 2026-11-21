package org.app.vestiobackend.dto;

import com.vestio.common.entity.Notification;
import com.vestio.common.entity.enums.NotificationType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationDto(UUID id, UUID userId, NotificationType type, Map<String, Object> payload, UUID listingId,
                              Instant readAt, Instant createdAt) {
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getUserId(), notification.getType(),
                notification.getPayload(), notification.getListingId(), notification.getReadAt(), notification.getCreatedAt());
    }
}
