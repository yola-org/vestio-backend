package org.app.vestiobackend.service;

import com.vestio.common.event.ChatEvent;
import com.vestio.common.publisher.AppSyncEventsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.entity.Message;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageEvents {
    private final ApplicationEventPublisher events;
    private final AppSyncEventsPublisher publisher;

    public void publish(Message message) {
        ChatEvent.Payload payload = new ChatEvent.Payload(message.getId(), message.getChat().getId(),
                message.getSender() == null ? null : message.getSender().getId(), message.getContent(),
                message.isSystem(), message.getSystemType() == null ? null : message.getSystemType().getValue(),
                message.getParams(), message.getCreatedAt());
        events.publishEvent(new SavedMessage(new ChatEvent(payload),
                message.getChat().getOwner().getId(), message.getChat().getBuyer().getId()));
    }

    @TransactionalEventListener
    public void afterCommit(SavedMessage saved) {
        // Snapshot IDs and payload before commit; never dereference JPA entities here.
        dispatch(saved.event(), saved.ownerId());
        dispatch(saved.event(), saved.buyerId());
    }

    private void dispatch(ChatEvent event, UUID userId) {
        try {
            publisher.dispatchEvent(event, userId).whenComplete((ignored, failure) -> {
                if (failure != null) log.error("Failed to publish chat message {} to {}", event.id(), userId, failure);
            });
        } catch (RuntimeException failure) {
            log.error("Failed to publish chat message {} to {}", event.id(), userId, failure);
        }
    }

    public record SavedMessage(ChatEvent event, UUID ownerId, UUID buyerId) { }
}
