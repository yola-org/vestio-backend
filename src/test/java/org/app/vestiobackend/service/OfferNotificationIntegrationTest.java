package org.app.vestiobackend.service;

import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.NotificationType;
import org.app.vestiobackend.dto.CreateOfferRequest;
import org.app.vestiobackend.dto.OfferDto;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.http.errors.exception.ResourceAlreadyExistsException;
import org.app.vestiobackend.repository.NotificationRepository;
import org.app.vestiobackend.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OfferNotificationIntegrationTest extends AbstractServiceTest {
    @Autowired OfferService offers;
    @Autowired OfferRepository offerRepository;
    @Autowired NotificationRepository notifications;
    @Autowired PlatformTransactionManager transactions;

    @Test
    void newOfferNotifiesOnlyTheListingOwnerAfterCommit() {
        Listing listing = createTestListing("Veste en jean", testUser, testCity, testCategory);
        User buyer = buyer();
        OfferDto offer = new TransactionTemplate(transactions).execute(status -> {
            var saved = offers.createOffer(listing.getId(), request());
            verifyNoInteractions(publisher);
            return saved;
        });
        var notification = notifications.findAll().getFirst();
        assertThat(notification.getUserId()).isEqualTo(testUser.getId());
        assertThat(notifications.countByUserIdAndReadAtIsNull(buyer.getId())).isZero();
        assertThat(notification.getType()).isEqualTo(NotificationType.OFFER_SENT);
        assertThat(notification.getListingId()).isEqualTo(listing.getId());
        assertThat(notification.getPayload()).containsEntry("offerId", offer.id().toString())
                .containsEntry("senderId", buyer.getId().toString())
                .containsEntry("senderName", "Sara Benali")
                .containsEntry("listingTitle", "Veste en jean");
        assertThat(new BigDecimal(notification.getPayload().get("amount").toString())).isEqualByComparingTo("125.50");
        verify(publisher).dispatchNotificationEvent(argThat(n -> n.getId().equals(notification.getId())
                && n.getUserId().equals(testUser.getId())));
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void duplicateOfferDoesNotCreateOrSendAnotherNotification() {
        Listing listing = createTestListing("Shirt", testUser, testCity, testCategory);
        buyer();
        offers.createOffer(listing.getId(), request());
        assertThatThrownBy(() -> offers.createOffer(listing.getId(), request()))
                .isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(notifications.count()).isEqualTo(1);
        verify(publisher).dispatchNotificationEvent(any());
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void rolledBackOfferDoesNotSaveOrSendNotification() {
        Listing listing = createTestListing("Shirt", testUser, testCity, testCategory);
        buyer();
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            offers.createOffer(listing.getId(), request());
            status.setRollbackOnly();
        });
        assertThat(offerRepository.count()).isZero();
        assertThat(notifications.count()).isZero();
        verifyNoInteractions(publisher);
    }

    @Test
    void failedRealtimeSendLeavesOfferAndInboxNotificationSaved() {
        Listing listing = createTestListing("Shirt", testUser, testCity, testCategory);
        buyer();
        when(publisher.dispatchNotificationEvent(any()))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("AppSync unavailable")));
        assertThatCode(() -> offers.createOffer(listing.getId(), request())).doesNotThrowAnyException();
        assertThat(offerRepository.count()).isEqualTo(1);
        assertThat(notifications.countByUserIdAndReadAtIsNull(testUser.getId())).isEqualTo(1);
    }

    private User buyer() {
        User buyer = createTestUser("sara@example.com");
        buyer.setFirstName("Sara");
        buyer.setLastName("Benali");
        buyer = userRepository.saveAndFlush(buyer);
        setupSecurityContext(buyer);
        return buyer;
    }

    private CreateOfferRequest request() {
        return new CreateOfferRequest(new BigDecimal("125.50"), List.of(ContactType.MEETUP), "Can we meet?");
    }
}
