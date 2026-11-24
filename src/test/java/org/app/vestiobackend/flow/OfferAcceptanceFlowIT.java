package org.app.vestiobackend.flow;

import org.app.vestiobackend.service.AbstractServiceIT;
import org.app.vestiobackend.service.OfferService;

import java.util.function.Function;
import com.vestio.common.event.ChatEvent;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.BadRequestException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import com.vestio.common.entity.Notification;
import com.vestio.common.entity.enums.NotificationType;
import com.vestio.common.entity.enums.OfferStatus;
import org.app.vestiobackend.entity.Chat;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Message;
import org.app.vestiobackend.entity.Offer;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.SystemMessageType;
import org.app.vestiobackend.http.errors.exception.UnAuthorizedException;
import org.app.vestiobackend.repository.ChatRepository;
import org.app.vestiobackend.repository.MessageRepository;
import org.app.vestiobackend.repository.NotificationRepository;
import org.app.vestiobackend.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OfferAcceptanceFlowIT extends AbstractServiceIT {
    @Autowired OfferService offers;
    @Autowired OfferRepository offerRepository;
    @Autowired ChatRepository chats;
    @Autowired MessageRepository messages;
    @Autowired NotificationRepository notifications;

    private Listing listing;
    private User buyer;
    private Offer offer;

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

    @Test
    void acceptsCreatesSystemMessageAndNotifiesOnlyBuyerWithServerDerivedNames() {
        assertThat(offers.acceptOffer(listing.getId(), offer.getId()).status()).isEqualTo(OfferStatus.ACCEPTED);
        assertAcceptedConversation();
        Notification notification = notifications.findAll().getFirst();
        verify(publisher).dispatchNotificationEvent(argThat(n -> n.getId().equals(notification.getId())
                && n.getUserId().equals(buyer.getId()) && n.getType() == NotificationType.OFFER_ACCEPTED));
        Message message = messages.findAll().getFirst();
        for (UUID participant : new UUID[]{testUser.getId(), buyer.getId()}) {
            verify(publisher).dispatchEvent(argThat(event -> event instanceof ChatEvent chatEvent
                    && chatEvent.id().equals(message.getId()) && chatEvent.payload().system()
                    && chatEvent.payload().systemType().equals("offer-accepted")
                    && chatEvent.payload().params().get("ownerName").equals("Alex Owner")
                    && chatEvent.payload().params().get("buyerName").equals("Sara Benali")
                    && chatEvent.payload().params().get("listingTitle").equals(listing.getTitle())
                    && ((Number) chatEvent.payload().params().get("amount")).doubleValue() == 125.5), eq(participant));
        }
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void repeatedAcceptanceDoesNotDuplicateChatMessageOrNotification() {
        for (int i = 0; i < 3; i++) {
            assertThat(offers.acceptOffer(listing.getId(), offer.getId()).status()).isEqualTo(OfferStatus.ACCEPTED);
        }
        assertAcceptedConversation();
        verify(publisher).dispatchNotificationEvent(any());
    }

    @Test
    void concurrentAcceptanceProducesOneConversationAndOneNotification() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CyclicBarrier start = new CyclicBarrier(2);
            Callable<OfferStatus> accept = () -> {
                setupSecurityContext(testUser);
                try {
                    start.await(5, TimeUnit.SECONDS);
                    return offers.acceptOffer(listing.getId(), offer.getId()).status();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
            Future<OfferStatus> first = executor.submit(accept);
            Future<OfferStatus> second = executor.submit(accept);
            assertThat(first.get(15, TimeUnit.SECONDS)).isEqualTo(OfferStatus.ACCEPTED);
            assertThat(second.get(15, TimeUnit.SECONDS)).isEqualTo(OfferStatus.ACCEPTED);
        }
        assertAcceptedConversation();
        verify(publisher).dispatchNotificationEvent(any());
    }

    @Test
    void buyerAndOtherUsersCannotAcceptAndCannotReplayAnAcceptedOffer() {
        User stranger = createTestUser("stranger@example.com");
        for (User user : new User[]{buyer, stranger}) {
            setupSecurityContext(user);
            assertThatThrownBy(() -> offers.acceptOffer(listing.getId(), offer.getId())).isInstanceOf(UnAuthorizedException.class);
        }
        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getStatus()).isEqualTo(OfferStatus.PENDING);
        assertThat(chats.count()).isZero();
        assertThat(messages.count()).isZero();
        assertThat(notifications.count()).isZero();
        verifyNoInteractions(publisher);
        setupSecurityContext(testUser);
        offers.acceptOffer(listing.getId(), offer.getId());
        setupSecurityContext(buyer);
        assertThatThrownBy(() -> offers.acceptOffer(listing.getId(), offer.getId())).isInstanceOf(UnAuthorizedException.class);
        assertAcceptedConversation();
        verify(publisher).dispatchNotificationEvent(any());
    }

    @Test
    void reusesExistingChatAndPreservesItsEarlierMessages() {
        Chat chat = new Chat();
        chat.setOffer(offer);
        chat.setListing(listing);
        chat.setOwner(testUser);
        chat.setBuyer(buyer);
        chat = chats.saveAndFlush(chat);
        Message earlier = new Message();
        earlier.setChat(chat);
        earlier.setSender(buyer);
        earlier.setContent("Hello");
        earlier = messages.saveAndFlush(earlier);
        offers.acceptOffer(listing.getId(), offer.getId());
        assertThat(chats.count()).isEqualTo(1);
        assertThat(messages.count()).isEqualTo(2);
        assertThat(messages.findById(earlier.getId()).orElseThrow().getContent()).isEqualTo("Hello");
        assertThat(notifications.findAll().getFirst().getPayload()).containsEntry("chatId", chat.getId().toString());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedNotificationRollsBackAcceptanceAndAllConversationWritesThenAllowsRetry(boolean synchronousFailure) {
        if (synchronousFailure) {
            when(publisher.dispatchNotificationEvent(any())).thenThrow(new IllegalStateException("AppSync unavailable"));
        } else {
            when(publisher.dispatchNotificationEvent(any())).thenReturn(CompletableFuture.failedFuture(new IllegalStateException("AppSync unavailable")));
        }
        assertThatThrownBy(() -> offers.acceptOffer(listing.getId(), offer.getId()))
                .isInstanceOf(synchronousFailure ? IllegalStateException.class : CompletionException.class);
        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getStatus()).isEqualTo(OfferStatus.PENDING);
        assertThat(chats.count()).isZero();
        assertThat(messages.count()).isZero();
        assertThat(notifications.count()).isZero();
        doReturn(CompletableFuture.completedFuture(null)).when(publisher).dispatchNotificationEvent(any());
        offers.acceptOffer(listing.getId(), offer.getId());
        assertAcceptedConversation();
        verify(publisher, times(2)).dispatchNotificationEvent(any());
    }

    @Test
    void concurrentAcceptAndRefuseHaveExactlyOneFinalDecision() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CyclicBarrier start = new CyclicBarrier(2);
            Function<Boolean, Callable<OfferStatus>> respond = accept -> () -> {
                setupSecurityContext(testUser);
                try {
                    start.await(5, TimeUnit.SECONDS);
                    return (accept ? offers.acceptOffer(listing.getId(), offer.getId())
                            : offers.refuseOffer(listing.getId(), offer.getId())).status();
                } catch (BadRequestException error) {
                    assertThat(error.getErrorCode()).isEqualTo(ErrorCode.ERR_OFFER_NOT_PENDING);
                    return null;
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
            Future<OfferStatus> accepting = executor.submit(respond.apply(true));
            Future<OfferStatus> refusing = executor.submit(respond.apply(false));
            OfferStatus accepted = accepting.get(15, TimeUnit.SECONDS);
            OfferStatus refused = refusing.get(15, TimeUnit.SECONDS);
            assertThat((accepted == null) != (refused == null)).isTrue();
            OfferStatus stored = offerRepository.findById(offer.getId()).orElseThrow().getStatus();
            if (accepted != null) {
                assertThat(stored).isEqualTo(OfferStatus.ACCEPTED);
                assertAcceptedConversation();
                verify(publisher).dispatchNotificationEvent(any());
            } else {
                assertThat(stored).isEqualTo(OfferStatus.REFUSED);
                assertThat(chats.count()).isZero();
                assertThat(messages.count()).isZero();
                assertThat(notifications.count()).isZero();
                verifyNoInteractions(publisher);
            }
        }
    }

    private void assertAcceptedConversation() {
        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getStatus()).isEqualTo(OfferStatus.ACCEPTED);
        assertThat(chats.count()).isEqualTo(1);
        Chat chat = chats.findAll().getFirst();
        assertThat(chat.getOffer().getId()).isEqualTo(offer.getId());
        assertThat(chat.getListing().getId()).isEqualTo(listing.getId());
        assertThat(chat.getOwner().getId()).isEqualTo(testUser.getId());
        assertThat(chat.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(messages.count()).isEqualTo(1);
        Message message = messages.findAll().getFirst();
        assertThat(message.getChat().getId()).isEqualTo(chat.getId());
        assertThat(message.isSystem()).isTrue();
        assertThat(message.getSystemType()).isEqualTo(SystemMessageType.OFFER_ACCEPTED);
        assertThat(message.getSender()).isNull();
        assertThat(message.getContent()).isNull();
        assertThat(message.getParams()).containsEntry("offerId", offer.getId().toString())
                .containsEntry("chatId", chat.getId().toString()).containsEntry("ownerName", "Alex Owner")
                .containsEntry("buyerName", "Sara Benali").containsEntry("listingTitle", "Veste en jean")
                .containsEntry("currency", "MAD");
        assertThat(new BigDecimal(message.getParams().get("amount").toString())).isEqualByComparingTo("125.50");
        assertThat(notifications.count()).isEqualTo(1);
        Notification notification = notifications.findAll().getFirst();
        assertThat(notification.getUserId()).isEqualTo(buyer.getId());
        assertThat(notification.getListingId()).isEqualTo(listing.getId());
        assertThat(notification.getType()).isEqualTo(NotificationType.OFFER_ACCEPTED);
        assertThat(notification.getPayload()).isEqualTo(message.getParams());
    }
}
