package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vestio.common.entity.Notification;
import com.vestio.common.entity.enums.NotificationType;
import com.vestio.common.publisher.AppSyncEventsPublisher;
import org.app.vestiobackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.app.vestiobackend.dto.NotificationDto;
import org.app.vestiobackend.dto.NotificationPageDto;
import org.app.vestiobackend.security.UserContext;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import com.vestio.common.event.payload.NotificationPayload;
import com.vestio.common.event.payload.ListingNotificationPayload;
import tools.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final AppSyncEventsPublisher publisher;
    private final UserContext userContext;

    @Transactional
    public Notification notifyUser(UUID userId, NotificationType type, NotificationPayload payload) {
        return notifyUser(userId, type, payload, null);
    }

    @Transactional
    public Notification notifyUser(UUID userId, NotificationType type, NotificationPayload payload, UUID listingId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .payload(Objects.requireNonNull(payload, "Notification payload is required").toMap(objectMapper))
                .listingId(listingId)
                .build();
        Notification saved = notificationRepository.saveAndFlush(notification);
        publisher.dispatchNotificationEvent(saved).join();
        return saved;
    }

    @Transactional
    public Notification notifyUserListingCreated(UUID userId, String listingTitle, UUID listingId) {
        NotificationPayload payload = new ListingNotificationPayload(listingTitle);
        return notifyUser(userId, NotificationType.LISTING_CREATED, payload, listingId);
    }

    @Transactional(readOnly = true)
    public NotificationPageDto list(int page, int size, boolean unreadOnly) {
        UUID userId = userContext.getCurrentUser().getId();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var notifications = unreadOnly
                ? notificationRepository.findByUserIdAndReadAtIsNull(userId, pageable)
                : notificationRepository.findByUserId(userId, pageable);
        return new NotificationPageDto(notifications.getContent().stream().map(NotificationDto::from).toList(),
                page, size, notifications.getTotalElements(), notifications.getTotalPages(),
                notificationRepository.countByUserIdAndReadAtIsNull(userId));
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return notificationRepository.countByUserIdAndReadAtIsNull(userContext.getCurrentUser().getId());
    }

    @Transactional
    public NotificationDto markRead(UUID id) {
        UUID userId = userContext.getCurrentUser().getId();
        notificationRepository.markRead(id, userId, LocalDateTime.now());
        return notificationRepository.findByIdAndUserId(id, userId).map(NotificationDto::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Notification not found"));
    }

    @Transactional
    public void markAllRead() {
        notificationRepository.markAllRead(userContext.getCurrentUser().getId(), LocalDateTime.now());
    }

}
