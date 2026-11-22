package org.app.vestiobackend.controller;

import org.app.vestiobackend.entity.Listing;
import tools.jackson.databind.JsonNode;
import com.vestio.common.entity.enums.OfferStatus;
import org.app.vestiobackend.entity.Chat;
import org.app.vestiobackend.entity.Message;
import org.app.vestiobackend.entity.Offer;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.SystemMessageType;
import org.app.vestiobackend.repository.ChatRepository;
import org.app.vestiobackend.repository.MessageRepository;
import org.app.vestiobackend.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChatControllerTest extends AbstractControllerTest {
    @Autowired ChatRepository chats;
    @Autowired MessageRepository messages;
    @Autowired OfferRepository offers;
    @Autowired JdbcTemplate jdbc;

    private User buyer;
    private User stranger;
    private Chat chat;
    private Chat privateChat;

    @BeforeEach
    void prepareConversations() {
        buyer = user("buyer@example.com", "Sara", "Buyer");
        buyer.setProfileImageKey(buyer.getId() + "/profile/" + UUID.randomUUID() + ".webp");
        userRepository.saveAndFlush(buyer);
        stranger = user("stranger@example.com", "Other", "User");
        chat = conversation(testUser, buyer, "My listing");
        privateChat = conversation(stranger, user("private@example.com", "Private", "Buyer"), "Private listing");
    }

    @Test
    void summariesIncludeTheFirstListingImageWithoutDuplicatingChatsAndAllowMissingCovers() throws Exception {
        Listing listing = listingRepository.findById(chat.getListing().getId()).orElseThrow();
        UUID cover = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        listing.setAssetKeys(List.of(cover, second));
        listingRepository.saveAndFlush(listing);
        mockMvc.perform(get("/api/chats").with(authenticatedUser(buyer)).param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].listingCoverAssetKey").value(cover.toString()));
        mockMvc.perform(get("/api/chats/{id}", chat.getId()).with(authenticatedUser(buyer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.listingCoverAssetKey").value(cover.toString()));
        listing.setAssetKeys(List.of(second));
        listingRepository.saveAndFlush(listing);
        mockMvc.perform(get("/api/chats/{id}", chat.getId()).with(authenticatedUser(buyer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.listingCoverAssetKey").value(second.toString()));
        listing.setAssetKeys(List.of());
        listingRepository.saveAndFlush(listing);
        mockMvc.perform(get("/api/chats").with(authenticatedUser(buyer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].listingCoverAssetKey").isEmpty());
    }

    @Test
    void allEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/chats")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/chats/{id}", chat.getId())).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/chats/{id}/messages", chat.getId())).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/chats/{id}/messages", chat.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Hello\"}"))
                .andExpect(status().isUnauthorized());
        assertThat(messages.count()).isZero();
    }

    @Test
    void chatListsAreScopedAndBothParticipantsCanReadDetails() throws Exception {
        for (User participant : List.of(testUser, buyer)) {
            mockMvc.perform(get("/api/chats").with(authenticatedUser(participant)).param("size", "1"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(chat.getId().toString()))
                    .andExpect(jsonPath("$.items[0].lastMessage").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1));
            String json = mockMvc.perform(get("/api/chats/{id}", chat.getId()).with(authenticatedUser(participant)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.offerId").value(chat.getOffer().getId().toString()))
                    .andExpect(jsonPath("$.listingId").value(chat.getListing().getId().toString()))
                    .andExpect(jsonPath("$.listingTitle").value("My listing"))
                    .andExpect(jsonPath("$.ownerId").value(testUser.getId().toString()))
                    .andExpect(jsonPath("$.ownerName").value("Test User"))
                    .andExpect(jsonPath("$.buyerName").value("Sara Buyer"))
                    .andExpect(jsonPath("$.buyerProfileImageKey").value(buyer.getProfileImageKey()))
                    .andExpect(jsonPath("$.createdAt").value(endsWith("Z")))
                    .andExpect(jsonPath("$.lastMessage").isEmpty())
                    .andReturn().getResponse().getContentAsString();
            assertThat(json).doesNotContain("email", testUser.getEmail(), buyer.getEmail());
        }
        mockMvc.perform(get("/api/chats").with(authenticatedUser(stranger)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].id").value(privateChat.getId().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
        mockMvc.perform(get("/api/chats").with(authenticatedUser(testUser)).param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void nonparticipantsAndMissingChatsReturn404ForReadAndSend() throws Exception {
        for (UUID id : List.of(chat.getId(), UUID.randomUUID())) {
            mockMvc.perform(get("/api/chats/{id}", id).with(authenticatedUser(stranger)))
                    .andExpect(status().isNotFound());
            mockMvc.perform(get("/api/chats/{id}/messages", id).with(authenticatedUser(stranger)))
                    .andExpect(status().isNotFound());
            mockMvc.perform(post("/api/chats/{id}/messages", id).with(authenticatedUser(stranger))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Hello\"}"))
                    .andExpect(status().isNotFound());
        }
        mockMvc.perform(get("/api/chats/{id}", privateChat.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
        assertThat(messages.count()).isZero();
    }

    @Test
    void bothParticipantsCanSendAndIdentityIsDerivedFromAuthentication() throws Exception {
        for (User participant : List.of(testUser, buyer)) {
            mockMvc.perform(post("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(participant))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Hello\"}"))
                    .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.chatId").value(chat.getId().toString()))
                    .andExpect(jsonPath("$.senderId").value(participant.getId().toString()))
                    .andExpect(jsonPath("$.content").value("Hello"))
                    .andExpect(jsonPath("$.system").value(false))
                    .andExpect(jsonPath("$.systemType").isEmpty()).andExpect(jsonPath("$.params").isEmpty())
                    .andExpect(jsonPath("$.createdAt").value(endsWith("Z")));
        }
        assertThat(messages.count()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"senderId", "sender", "system", "systemType", "params", "chatId", "createdAt", "id"})
    void ignoresClientSuppliedIdentityAndSystemMetadata(String field) throws Exception {
        mockMvc.perform(post("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("content", "Hello", field, "forged"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senderId").value(buyer.getId().toString()))
                .andExpect(jsonPath("$.chatId").value(chat.getId().toString()))
                .andExpect(jsonPath("$.system").value(false))
                .andExpect(jsonPath("$.systemType").isEmpty())
                .andExpect(jsonPath("$.params").isEmpty());
        assertThat(messages.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"content\":null}", "{\"content\":\"\"}", "{\"content\":\"   \"}", "{\"content\":{}}"})
    void rejectsMissingBlankAndInvalidContent(String body) throws Exception {
        mockMvc.perform(post("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        assertThat(messages.count()).isZero();
    }

    @Test
    void enforcesTheMessageLengthLimit() throws Exception {
        mockMvc.perform(post("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("content", "a".repeat(4001)))))
                .andExpect(status().isBadRequest());
        assertThat(messages.count()).isZero();
        mockMvc.perform(post("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(buyer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("content", "a".repeat(4000)))))
                .andExpect(status().isCreated());
        assertThat(messages.findAll().getFirst().getContent()).hasSize(4000);
    }

    @ParameterizedTest
    @ValueSource(strings = {"page=-1", "size=0", "size=101", "page=bad", "size=bad"})
    void validatesPagination(String input) throws Exception {
        String[] pair = input.split("=");
        mockMvc.perform(get("/api/chats").with(authenticatedUser(buyer)).param(pair[0], pair[1]))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(buyer)).param(pair[0], pair[1]))
                .andExpect(status().isBadRequest());
    }

    @Test
    void messageHistoryIncludesSystemMessagesAndHasStableScopedPagination() throws Exception {
        Message system = new Message();
        system.setChat(chat);
        system.setSystem(true);
        system.setSystemType(SystemMessageType.OFFER_ACCEPTED);
        system.setParams(Map.of("amount", BigDecimal.TEN, "currency", "MAD", "ownerName", "Test User",
                "buyerName", "Sara Buyer", "listingTitle", "My listing"));
        system = messages.saveAndFlush(system);
        Message regular = new Message();
        regular.setChat(chat);
        regular.setSender(buyer);
        regular.setContent("Hello");
        regular = messages.saveAndFlush(regular);
        Message privateMessage = new Message();
        privateMessage.setChat(privateChat);
        privateMessage.setSender(stranger);
        privateMessage.setContent("Private text");
        messages.saveAndFlush(privateMessage);
        for (Message message : List.of(system, regular)) {
            jdbc.update("UPDATE messages SET created_at = ? WHERE id = ?", OffsetDateTime.parse("2026-11-21T12:00:00Z"), message.getId());
        }
        List<String> ids = List.of(system.getId().toString(), regular.getId().toString()).stream()
                .sorted(Comparator.reverseOrder()).toList();
        for (int page = 0; page < 2; page++) {
            mockMvc.perform(get("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(buyer))
                            .param("page", String.valueOf(page)).param("size", "1"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(ids.get(page)))
                    .andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(2));
        }
        String json = mockMvc.perform(get("/api/chats/{id}/messages", chat.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode items = objectMapper.readTree(json).path("items");
        assertThat(items.size()).isEqualTo(2);
        JsonNode savedSystem = items.get(ids.indexOf(system.getId().toString()));
        assertThat(savedSystem.path("systemType").asString()).isEqualTo("offer-accepted");
        assertThat(savedSystem.path("senderId").isNull()).isTrue();
        assertThat(savedSystem.path("params").path("listingTitle").asString()).isEqualTo("My listing");
        assertThat(json).doesNotContain("Private text");
    }

    @Test
    void embedsLatestSystemMessageInListAndDetailsWithoutLeakingPrivateMessages() throws Exception {
        Message first = regularMessage(chat, buyer, "Earlier message");
        Message system = new Message();
        system.setChat(chat);
        system.setSystem(true);
        system.setSystemType(SystemMessageType.OFFER_ACCEPTED);
        system.setParams(Map.of("amount", BigDecimal.TEN, "currency", "MAD", "ownerName", "Test User",
                "buyerName", "Sara Buyer", "listingTitle", "My listing"));
        system = messages.saveAndFlush(system);
        Message hidden = regularMessage(privateChat, stranger, "Private message");
        OffsetDateTime base = OffsetDateTime.now().withNano(0);
        messageTime(first, base.minusHours(2));
        messageTime(system, base.minusHours(1));
        messageTime(hidden, base);
        for (User participant : List.of(testUser, buyer)) {
            mockMvc.perform(get("/api/chats").with(authenticatedUser(participant)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.items[0].lastMessage.id").value(system.getId().toString()))
                    .andExpect(jsonPath("$.items[0].lastMessage.systemType").value("offer-accepted"))
                    .andExpect(jsonPath("$.items[0].lastMessage.params.listingTitle").value("My listing"));
            mockMvc.perform(get("/api/chats/{id}", chat.getId()).with(authenticatedUser(participant)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lastMessage.id").value(system.getId().toString()))
                    .andExpect(jsonPath("$.lastMessage.chatId").value(chat.getId().toString()))
                    .andExpect(jsonPath("$.lastMessage.senderId").isEmpty())
                    .andExpect(jsonPath("$.lastMessage.system").value(true))
                    .andExpect(jsonPath("$.lastMessage.createdAt").value(endsWith("Z")));
        }
    }

    @Test
    void paginatesChatsByLatestMessageAndSendingMovesTheChatToTheTop() throws Exception {
        Chat newer = conversation(testUser, user("new-buyer@example.com", "New", "Buyer"), "Newer chat");
        Chat empty = conversation(testUser, user("empty-buyer@example.com", "Empty", "Buyer"), "Empty chat");
        OffsetDateTime base = OffsetDateTime.now().minusDays(2).withNano(0);
        chatTime(chat, base);
        chatTime(newer, base.plusHours(1));
        chatTime(empty, base.plusHours(2));
        Message latest = regularMessage(chat, buyer, "Most recent message");
        messageTime(latest, base.plusHours(4));
        messageTime(regularMessage(newer, testUser, "Older message"), base.plusHours(3));
        messageTime(regularMessage(privateChat, stranger, "Not your chat"), base.plusDays(5));
        List<UUID> expected = List.of(chat.getId(), newer.getId(), empty.getId());
        for (int page = 0; page < 3; page++) {
            mockMvc.perform(get("/api/chats").with(authenticatedUser(testUser)).param("size", "1").param("page", String.valueOf(page)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].id").value(expected.get(page).toString()))
                    .andExpect(jsonPath("$.totalElements").value(3)).andExpect(jsonPath("$.totalPages").value(3));
        }
        mockMvc.perform(get("/api/chats/{id}", empty.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastMessage").isEmpty());
        mockMvc.perform(post("/api/chats/{id}/messages", newer.getId()).with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"Fresh message\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/chats").with(authenticatedUser(testUser)).param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].id").value(newer.getId().toString()))
                .andExpect(jsonPath("$.items[0].lastMessage.content").value("Fresh message"))
                .andExpect(jsonPath("$.items[0].lastMessage.senderId").value(testUser.getId().toString()));
    }

    @Test
    void equalTimestampsSelectOneMessagePerChatAndKeepPaginationStable() throws Exception {
        Chat other = conversation(testUser, user("other-buyer@example.com", "Other", "Buyer"), "Other chat");
        Message one = regularMessage(chat, buyer, "One");
        Message two = regularMessage(chat, testUser, "Two");
        Message three = regularMessage(other, testUser, "Three");
        OffsetDateTime time = OffsetDateTime.now().plusHours(1).withNano(0);
        for (Message message : List.of(one, two, three)) messageTime(message, time);
        String expectedMessageId = List.of(one.getId().toString(), two.getId().toString()).stream().max(Comparator.naturalOrder()).orElseThrow();
        List<String> ids = List.of(chat.getId().toString(), other.getId().toString()).stream().sorted(Comparator.reverseOrder()).toList();
        for (int page = 0; page < 2; page++) {
            mockMvc.perform(get("/api/chats").with(authenticatedUser(testUser)).param("size", "1").param("page", String.valueOf(page)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].id").value(ids.get(page)))
                    .andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(2));
        }
        mockMvc.perform(get("/api/chats/{id}", chat.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastMessage.id").value(expectedMessageId));
    }

    @Test
    void deletingLatestMessageFallsBackToPreviousMessageAndThenNull() throws Exception {
        Message first = regularMessage(chat, buyer, "First");
        Message last = regularMessage(chat, testUser, "Last");
        OffsetDateTime time = OffsetDateTime.now().withNano(0);
        messageTime(first, time.minusHours(1));
        messageTime(last, time);
        messages.deleteById(last.getId());
        mockMvc.perform(get("/api/chats/{id}", chat.getId()).with(authenticatedUser(buyer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastMessage.id").value(first.getId().toString()));
        messages.deleteById(first.getId());
        mockMvc.perform(get("/api/chats").with(authenticatedUser(buyer)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].lastMessage").isEmpty());
    }

    private Message regularMessage(Chat target, User sender, String content) {
        Message message = new Message();
        message.setChat(target);
        message.setSender(sender);
        message.setContent(content);
        return messages.saveAndFlush(message);
    }

    private void messageTime(Message message, OffsetDateTime time) {
        jdbc.update("UPDATE messages SET created_at = ? WHERE id = ?", time, message.getId());
    }

    private void chatTime(Chat target, OffsetDateTime time) {
        jdbc.update("UPDATE chats SET created_at = ? WHERE id = ?", time, target.getId());
    }

    private User user(String email, String firstName, String lastName) {
        return userRepository.saveAndFlush(User.builder().id(UUID.randomUUID()).email(email)
                .firstName(firstName).lastName(lastName).build());
    }

    private Chat conversation(User owner, User chatBuyer, String title) {
        Listing listing = createTestListing(title, owner, testCity, testCategory);
        Offer offer = new Offer();
        offer.setListing(listing);
        offer.setUser(chatBuyer);
        offer.setPrice(BigDecimal.TEN);
        offer.setMessage("Offer");
        offer.setDeliveryTypes(new String[]{"MEETUP"});
        offer.setStatus(OfferStatus.ACCEPTED);
        offer = offers.saveAndFlush(offer);
        Chat created = new Chat();
        created.setListing(listing);
        created.setOffer(offer);
        created.setOwner(owner);
        created.setBuyer(chatBuyer);
        return chats.saveAndFlush(created);
    }
}
