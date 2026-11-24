package org.app.vestiobackend.flow;

import org.app.vestiobackend.service.AbstractServiceIT;
import org.app.vestiobackend.service.ChatService;
import org.app.vestiobackend.service.OfferService;

import com.vestio.common.event.ChatEvent;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Offer;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.repository.ChatRepository;
import org.app.vestiobackend.repository.MessageRepository;
import org.app.vestiobackend.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.app.vestiobackend.dto.SendMessageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatMessagingFlowIT extends AbstractServiceIT {
    @Autowired OfferService offers;
    @Autowired OfferRepository offerRepository;
    @Autowired ChatRepository chats;
    @Autowired MessageRepository messages;
    @Autowired ChatService chatService;
    @Autowired PlatformTransactionManager transactions;

    private Listing listing;
    private User buyer;
    private Offer offer;

    @Test
    void regularMessagesPublishToBothParticipantsOnlyAfterCommitAndNeverAfterRollback() {
        offers.acceptOffer(listing.getId(), offer.getId());
        UUID chatId = chats.findAll().getFirst().getId();
        clearInvocations(publisher);
        setupSecurityContext(buyer);
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello owner");
        TransactionTemplate transaction = new TransactionTemplate(transactions);
        transaction.executeWithoutResult(status -> {
            chatService.send(chatId, request);
            verifyNoInteractions(publisher);
            status.setRollbackOnly();
        });
        verifyNoInteractions(publisher);
        assertThat(messages.count()).isEqualTo(1);
        transaction.executeWithoutResult(status -> {
            chatService.send(chatId, request);
            verifyNoInteractions(publisher);
        });
        for (UUID participant : new UUID[]{testUser.getId(), buyer.getId()}) {
            verify(publisher).dispatchEvent(argThat(event -> event instanceof ChatEvent chatEvent
                    && chatEvent.payload().chatId().equals(chatId)
                    && chatEvent.payload().senderId().equals(buyer.getId())
                    && chatEvent.payload().content().equals("Hello owner")
                    && !chatEvent.payload().system()
                    && chatEvent.payload().systemType() == null), eq(participant));
        }
        verifyNoMoreInteractions(publisher);
        assertThat(messages.count()).isEqualTo(2);
    }

    @Test
    void failedChatPublicationKeepsSavedMessageAndStillAttemptsOtherParticipant() {
        offers.acceptOffer(listing.getId(), offer.getId());
        UUID chatId = chats.findAll().getFirst().getId();
        clearInvocations(publisher);
        when(publisher.dispatchEvent(any(), eq(testUser.getId()))).thenThrow(new IllegalStateException("Offline"));
        when(publisher.dispatchEvent(any(), eq(buyer.getId())))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("Offline")));
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Saved despite transport outage");
        assertThat(chatService.send(chatId, request).content()).isEqualTo(request.getContent());
        assertThat(messages.count()).isEqualTo(2);
        verify(publisher).dispatchEvent(any(ChatEvent.class), eq(testUser.getId()));
        verify(publisher).dispatchEvent(any(ChatEvent.class), eq(buyer.getId()));
    }

    @BeforeEach
    void preparePendingOffer() {
        testUser.setFirstName("  Alex  ");
        testUser.setLastName(" Owner ");
        testUser = userRepository.saveAndFlush(testUser);
        listing = createTestListing("Veste en jean", testUser, testCity, testCategory);
        buyer = createTestUser("sara@example.com");
        buyer.setFirstName("Sara");
        buyer.setLastName("Benali");
        buyer = userRepository.saveAndFlush(buyer);
        offer = new Offer();
        offer.setListing(listing);
        offer.setUser(buyer);
        offer.setPrice(new BigDecimal("125.50"));
        offer.setMessage("Can we meet?");
        offer.setDeliveryTypes(new String[]{"MEETUP"});
        offer = offerRepository.saveAndFlush(offer);
    }

}
