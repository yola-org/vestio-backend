package org.app.vestiobackend.service;

import com.vestio.common.entity.Notification;
import org.app.vestiobackend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationDeliveryIntegrationTest extends AbstractServiceTest {

    @Autowired private NotificationService notificationService;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private jakarta.persistence.EntityManager entityManager;

    @Test
    void publishesAutomaticallyAfterCommitToUserChannel() {
        Notification notification = new TransactionTemplate(transactionManager).execute(status -> {
            Notification saved = createNotification();
            verifyNoInteractions(publisher);
            return saved;
        });

        assertThat(notification.getId()).isNotNull();
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notificationRepository.findById(notification.getId())).isPresent();
        assertThat(notification.getUserId()).isEqualTo(testUser.getId());
        verify(publisher).dispatchNotificationEvent(
                argThat(value -> value.getId().equals(notification.getId())
                        && value.getCreatedAt() != null
                        && value.getUserId().equals(testUser.getId())));
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void rolledBackNotificationIsNeverPublished() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            createNotification();
            verifyNoInteractions(publisher);
            status.setRollbackOnly();
        });

        assertThat(notificationRepository.count()).isZero();
        verifyNoInteractions(publisher);
    }

    @Test
    void asynchronousPublishFailureKeepsCommittedNotification() {
        when(publisher.dispatchNotificationEvent(any()))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("AppSync unavailable")));

        assertThatCode(this::createNotification).doesNotThrowAnyException();

        assertThat(notificationRepository.count()).isEqualTo(1);
        verify(publisher, times(1)).dispatchNotificationEvent(any());
    }

    @Test
    void synchronousPublishFailureKeepsCommittedNotification() {
        when(publisher.dispatchNotificationEvent(any()))
                .thenThrow(new IllegalStateException("AppSync unavailable"));

        assertThatCode(this::createNotification).doesNotThrowAnyException();

        assertThat(notificationRepository.count()).isEqualTo(1);
        verify(publisher, times(1)).dispatchNotificationEvent(any());
    }

    @Test
    void persistsAndPublishesGenericNotificationWithoutListingId() {
        var notification = notificationService.notifyUser(testUser.getId(),
                com.vestio.common.entity.enums.NotificationType.OFFER_CREATED, java.util.Map.of("message", "Offer changed"));
        assertThat(notification.getPayload()).doesNotContainKey("listingId");
        assertThat(notificationRepository.findById(notification.getId())).isPresent();
        verify(publisher).dispatchNotificationEvent(argThat(value -> value.getId().equals(notification.getId())));
        assertThat(notification.getListingId()).isNull();
    }

    private Notification createNotification() {
        return notificationService.notifyUserListingCreated(testUser.getId(), "A shirt", UUID.randomUUID());
    }
}
