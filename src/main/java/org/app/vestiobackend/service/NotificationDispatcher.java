package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vestio.common.event.NotificationCreated;
import com.vestio.common.publisher.AppSyncEventsPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final AppSyncEventsPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(NotificationCreated event) {
        try {
            publisher.dispatchNotificationEvent(event.notification())
                    .whenComplete((ignored, error) -> {
                        if (error != null) {
                            log.warn("AppSync publication failed for notification {}",
                                    event.notification().getId(), error);
                        }
                    });
        } catch (Exception exception) {
            // Publication failure must not turn a committed listing into an API error.
            log.warn("AppSync publication failed for notification {}",
                    event.notification().getId(), exception);
        }
    }
}
