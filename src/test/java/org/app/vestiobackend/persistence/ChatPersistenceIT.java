package org.app.vestiobackend.persistence;

import org.app.vestiobackend.service.AbstractServiceIT;

import org.hibernate.Session;
import tools.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import org.app.vestiobackend.entity.Chat;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Message;
import org.app.vestiobackend.entity.Offer;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.SystemMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ChatPersistenceIT extends AbstractServiceIT {
    @Autowired EntityManager entityManager;
    @Autowired PlatformTransactionManager transactionManager;

    private TransactionTemplate transactions;
    private Listing listing;
    private User buyer;

    @BeforeEach
    void prepareChatParticipants() {
        transactions = new TransactionTemplate(transactionManager);
        listing = createTestListing("Shirt", testUser, testCity, testCategory);
        buyer = createTestUser("buyer@example.com");
    }

    @Test
    void persistsBothParticipantsMessagesAndUtcTimestampsAndCascadesListingDeletion() {
        Instant before = Instant.now().minusSeconds(1);
        Chat chat = saveChat(testUser, buyer);
        Message question = saveMessage(chat, buyer, "Is this available?");
        Message reply = saveMessage(chat, testUser, "Yes, it is.");

        transactions.executeWithoutResult(status -> {
            Chat stored = entityManager.find(Chat.class, chat.getId());
            assertThat(stored.getListing().getId()).isEqualTo(listing.getId());
            assertThat(stored.getOwner().getId()).isEqualTo(testUser.getId());
            assertThat(stored.getBuyer().getId()).isEqualTo(buyer.getId());
            assertThat(stored.getCreatedAt()).isBetween(before, Instant.now());
            Message message = entityManager.find(Message.class, question.getId());
            assertThat(message.getChat().getId()).isEqualTo(chat.getId());
            assertThat(message.getSender().getId()).isEqualTo(buyer.getId());
            assertThat(message.getContent()).isEqualTo("Is this available?");
            assertThat(message.getCreatedAt()).isBetween(before, Instant.now());
            assertThat(entityManager.find(Message.class, reply.getId()).getSender().getId()).isEqualTo(testUser.getId());
        });

        listingRepository.deleteById(listing.getId());
        transactions.executeWithoutResult(status -> {
            assertThat(entityManager.find(Chat.class, chat.getId())).isNull();
            assertThat(entityManager.find(Message.class, question.getId())).isNull();
            assertThat(entityManager.find(Message.class, reply.getId())).isNull();
        });
    }

    @Test
    void rejectsDuplicateConversationForTheSameListingAndBuyer() {
        saveChat(testUser, buyer);
        assertThatThrownBy(() -> saveChat(testUser, buyer)).isInstanceOf(RuntimeException.class);
        // A different buyer may have their own conversation about the same listing.
        assertThat(saveChat(testUser, createTestUser("other-buyer@example.com")).getId()).isNotNull();
    }

    @Test
    void rejectsOwnerWhoDoesNotOwnTheListing() {
        User stranger = createTestUser("stranger@example.com");
        assertThatThrownBy(() -> saveChat(stranger, buyer)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void rejectsConversationWithTheSameOwnerAndBuyer() {
        assertThatThrownBy(() -> saveChat(testUser, testUser)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void rejectsMessagesFromUsersOutsideTheChat() {
        Chat chat = saveChat(testUser, buyer);
        User stranger = createTestUser("stranger@example.com");
        assertThatThrownBy(() -> saveMessage(chat, stranger, "Hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("chat owner or buyer");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    void rejectsBlankMessages(String content) {
        Chat chat = saveChat(testUser, buyer);
        assertThatThrownBy(() -> saveMessage(chat, buyer, content)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void persistsTypedSystemMessagesWithoutSenderOrTextAndSerializesTheirType() {
        Chat chat = saveChat(testUser, buyer);
        Message saved = saveSystemMessage(chat, SystemMessageType.OFFER_ACCEPTED,
                Map.of("amount", new BigDecimal("125.50"), "currency", "MAD",
                        "ownerName", "Alex Owner", "buyerName", "Sara Buyer", "listingTitle", "Shirt"));
        transactions.executeWithoutResult(status -> {
            Message stored = entityManager.find(Message.class, saved.getId());
            assertThat(stored.isSystem()).isTrue();
            assertThat(stored.getSystemType()).isEqualTo(SystemMessageType.OFFER_ACCEPTED);
            assertThat(stored.getSender()).isNull();
            assertThat(stored.getContent()).isNull();
            assertThat(stored.getParams()).containsEntry("currency", "MAD");
            assertThat(new BigDecimal(stored.getParams().get("amount").toString())).isEqualByComparingTo("125.50");
            assertThat(stored.getCreatedAt()).isNotNull();
        });
        JsonMapper mapper = JsonMapper.builder().build();
        assertThat(mapper.writeValueAsString(saved.getSystemType())).isEqualTo("\"offer-accepted\"");
        assertThat(mapper.readValue("\"offer-accepted\"", SystemMessageType.class)).isEqualTo(SystemMessageType.OFFER_ACCEPTED);
    }

    @Test
    void requiresTypeAndParamsForSystemMessages() {
        Chat chat = saveChat(testUser, buyer);
        assertThatThrownBy(() -> saveSystemMessage(chat, null, Map.of()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("system type and params");
        assertThatThrownBy(() -> saveSystemMessage(chat, SystemMessageType.OFFER_ACCEPTED, null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("system type and params");
    }

    @Test
    void validatesOfferAcceptedParametersBeforeSaving() {
        Chat chat = saveChat(testUser, buyer);
        for (Map<String, Object> params : List.<Map<String, Object>>of(
                Map.of(), Map.of("amount", "125", "currency", "MAD"),
                Map.of("amount", -1, "currency", "MAD"),
                Map.of("amount", Double.NaN, "currency", "MAD"),
                Map.of("amount", 125), Map.of("amount", 125, "currency", ""))) {
            assertThatThrownBy(() -> saveSystemMessage(chat, SystemMessageType.OFFER_ACCEPTED, params))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void regularMessagesCannotCarrySystemMetadataOrOmitTheirSender() {
        Chat chat = saveChat(testUser, buyer);
        assertThatThrownBy(() -> saveMessage(chat, null, "Hello"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("requires a sender");
        Message message = saveMessage(chat, buyer, "Hello");
        transactions.executeWithoutResult(status -> {
            Message stored = entityManager.find(Message.class, message.getId());
            assertThat(stored.isSystem()).isFalse();
            assertThat(stored.getSystemType()).isNull();
            assertThat(stored.getParams()).isNull();
        });
        assertThatThrownBy(() -> transactions.executeWithoutResult(status -> {
            Message stored = entityManager.find(Message.class, message.getId());
            stored.setSystemType(SystemMessageType.OFFER_ACCEPTED);
            entityManager.flush();
        })).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Only system messages");
    }

    @Test
    void deletingAnOfferDeletesItsChatAndMessagesButKeepsTheListing() {
        Chat chat = saveChat(testUser, buyer);
        Message message = saveMessage(chat, buyer, "Hello");
        UUID offerId = transactions.execute(status -> entityManager.find(Chat.class, chat.getId()).getOffer().getId());
        transactions.executeWithoutResult(status -> entityManager.remove(entityManager.find(Offer.class, offerId)));
        transactions.executeWithoutResult(status -> {
            assertThat(entityManager.find(Chat.class, chat.getId())).isNull();
            assertThat(entityManager.find(Message.class, message.getId())).isNull();
            assertThat(entityManager.find(Listing.class, listing.getId())).isNotNull();
        });
    }

    @Test
    void offerIdChangesCascadeIntoTheChat() {
        Chat chat = saveChat(testUser, buyer);
        Message message = saveMessage(chat, buyer, "Hello");
        UUID newId = UUID.randomUUID();
        transactions.executeWithoutResult(status -> {
            Chat stored = entityManager.find(Chat.class, chat.getId());
            UUID oldId = stored.getOffer().getId();
            entityManager.unwrap(Session.class).createNativeMutationQuery("UPDATE offers SET id = :newId WHERE id = :oldId")
                    .setParameter("newId", newId).setParameter("oldId", oldId).executeUpdate();
            entityManager.clear();
            assertThat(entityManager.find(Chat.class, chat.getId()).getOffer().getId()).isEqualTo(newId);
            assertThat(entityManager.find(Message.class, message.getId()).getChat().getId()).isEqualTo(chat.getId());
        });
    }

    @Test
    // Verify overlapping composite cascade paths on the deployed database engine.
    @EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL", matches = "jdbc:postgresql:.*")
    void listingIdChangesCascadeThroughOffersChatsAndListingCollections() {
        listing.setAssetKeys(List.of(UUID.randomUUID()));
        listingRepository.saveAndFlush(listing);
        Chat chat = saveChat(testUser, buyer);
        Message message = saveMessage(chat, buyer, "Hello");
        UUID newId = UUID.randomUUID();
        transactions.executeWithoutResult(status -> {
            entityManager.unwrap(Session.class).createNativeMutationQuery("UPDATE listings SET id = :newId WHERE id = :oldId")
                    .setParameter("newId", newId).setParameter("oldId", listing.getId()).executeUpdate();
            entityManager.clear();
            Chat stored = entityManager.find(Chat.class, chat.getId());
            assertThat(stored.getListing().getId()).isEqualTo(newId);
            assertThat(stored.getOffer().getListing().getId()).isEqualTo(newId);
            Listing updated = entityManager.find(Listing.class, newId);
            assertThat(updated.getColors()).containsExactly("Black");
            assertThat(updated.getContactTypes()).hasSize(1);
            assertThat(updated.getCities()).hasSize(1);
            assertThat(updated.getAssetKeys()).hasSize(1);
            assertThat(entityManager.find(Message.class, message.getId()).getChat().getId()).isEqualTo(chat.getId());
            // Native deletion also verifies the complete listing -> offer/chat -> message chain.
            entityManager.unwrap(Session.class).createNativeMutationQuery("DELETE FROM listings WHERE id = :id")
                    .setParameter("id", newId).executeUpdate();
            entityManager.clear();
            assertThat(entityManager.find(Chat.class, chat.getId())).isNull();
            assertThat(entityManager.find(Message.class, message.getId())).isNull();
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejectsAnOfferThatDoesNotMatchTheChatsListingOrBuyer(boolean otherListing) {
        Listing offerListing = otherListing ? createTestListing("Other", testUser, testCity, testCategory) : listing;
        User offerBuyer = otherListing ? buyer : createTestUser("other-buyer@example.com");
        transactions.executeWithoutResult(status -> {
            Offer offer = new Offer();
            offer.setListing(entityManager.getReference(Listing.class, offerListing.getId()));
            offer.setUser(entityManager.getReference(User.class, offerBuyer.getId()));
            offer.setPrice(BigDecimal.TEN);
            offer.setMessage("Offer");
            offer.setDeliveryTypes(new String[]{"MEETUP"});
            entityManager.persist(offer);
            entityManager.flush();
        });
        assertThatThrownBy(() -> transactions.executeWithoutResult(status -> {
            Offer offer = entityManager.createQuery("from Offer", Offer.class).getSingleResult();
            Chat chat = new Chat();
            chat.setOffer(offer);
            chat.setListing(entityManager.getReference(Listing.class, listing.getId()));
            chat.setOwner(entityManager.getReference(User.class, testUser.getId()));
            chat.setBuyer(entityManager.getReference(User.class, buyer.getId()));
            entityManager.persist(chat);
            entityManager.flush();
        })).isInstanceOf(RuntimeException.class);
    }

    private Message saveSystemMessage(Chat chat, SystemMessageType type, Map<String, Object> params) {
        return transactions.execute(status -> {
            Message message = new Message();
            message.setChat(entityManager.getReference(Chat.class, chat.getId()));
            message.setSystem(true);
            message.setSystemType(type);
            message.setParams(params);
            entityManager.persist(message);
            entityManager.flush();
            return message;
        });
    }

    private Chat saveChat(User owner, User chatBuyer) {
        return transactions.execute(status -> {
            Offer offer = entityManager.createQuery("from Offer o where o.listing.id = :listing and o.user.id = :buyer", Offer.class)
                    .setParameter("listing", listing.getId()).setParameter("buyer", chatBuyer.getId())
                    .getResultStream().findFirst().orElseGet(() -> {
                        Offer created = new Offer();
                        created.setListing(entityManager.getReference(Listing.class, listing.getId()));
                        created.setUser(entityManager.getReference(User.class, chatBuyer.getId()));
                        created.setPrice(BigDecimal.TEN);
                        created.setDeliveryTypes(new String[]{"MEETUP"});
                        created.setMessage("Offer");
                        entityManager.persist(created);
                        return created;
                    });
            Chat chat = new Chat();
            chat.setOffer(offer);
            chat.setListing(entityManager.getReference(Listing.class, listing.getId()));
            chat.setOwner(entityManager.getReference(User.class, owner.getId()));
            chat.setBuyer(entityManager.getReference(User.class, chatBuyer.getId()));
            entityManager.persist(chat);
            entityManager.flush();
            return chat;
        });
    }

    private Message saveMessage(Chat chat, User sender, String content) {
        return transactions.execute(status -> {
            Message message = new Message();
            message.setChat(entityManager.getReference(Chat.class, chat.getId()));
            message.setSender(sender == null ? null : entityManager.getReference(User.class, sender.getId()));
            message.setContent(content);
            entityManager.persist(message);
            entityManager.flush();
            return message;
        });
    }
}
