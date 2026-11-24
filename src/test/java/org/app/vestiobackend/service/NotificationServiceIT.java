package org.app.vestiobackend.service;

import com.vestio.common.entity.Notification;
import com.vestio.common.entity.enums.NotificationType;
import com.vestio.common.event.payload.NotificationPayload;
import org.app.vestiobackend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceIT extends AbstractServiceIT {
    @Autowired private NotificationService notificationService;
    @Autowired private NotificationRepository notificationRepository;

    @Test
    void savesAndPublishesDirectlyToTheUserChannel() {
        Notification notification = createNotification();
        assertThat(notification.getId()).isNotNull();
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notificationRepository.findById(notification.getId())).isPresent();
        verify(publisher).dispatchNotificationEvent(argThat(value -> value.getId().equals(notification.getId())
                && value.getCreatedAt() != null && value.getUserId().equals(testUser.getId())));
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void asynchronousPublishFailureRollsBackNotification() {
        when(publisher.dispatchNotificationEvent(any()))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("AppSync unavailable")));
        assertThatThrownBy(this::createNotification).isInstanceOf(CompletionException.class);
        assertThat(notificationRepository.count()).isZero();
        verify(publisher).dispatchNotificationEvent(any());
    }

    @Test
    void synchronousPublishFailureRollsBackNotification() {
        when(publisher.dispatchNotificationEvent(any())).thenThrow(new IllegalStateException("AppSync unavailable"));
        assertThatThrownBy(this::createNotification).isInstanceOf(IllegalStateException.class);
        assertThat(notificationRepository.count()).isZero();
        verify(publisher).dispatchNotificationEvent(any());
    }

    @Test
    void persistsTypedGenericPayloadWithoutListingId() {
        Notification notification = notificationService.notifyUser(testUser.getId(), NotificationType.OFFER_CREATED,
                new MessagePayload("Offer changed"));
        assertThat(notification.getPayload()).containsEntry("message", "Offer changed").doesNotContainKey("listingId");
        assertThat(notificationRepository.findById(notification.getId())).isPresent();
        assertThat(notification.getListingId()).isNull();
        verify(publisher).dispatchNotificationEvent(argThat(value -> value.getId().equals(notification.getId())));
    }

    private Notification createNotification() {
        return notificationService.notifyUserListingCreated(testUser.getId(), "A shirt", UUID.randomUUID());
    }

    private record MessagePayload(String message) implements NotificationPayload {}
}
