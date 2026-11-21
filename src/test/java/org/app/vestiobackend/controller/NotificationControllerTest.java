package org.app.vestiobackend.controller;

import com.vestio.common.entity.Notification;
import org.app.vestiobackend.entity.User;
import com.vestio.common.entity.enums.NotificationType;
import org.app.vestiobackend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest extends AbstractControllerTest {
    @Autowired NotificationRepository notifications;

    @Test
    void historyIsPaginatedAndScopedAndUnreadCountIncludesOtherPages() throws Exception {
        Notification older = create(testUser, "Older", false);
        Notification newer = create(testUser, "Newer", true);
        create(otherUser(), "Private", false);

        mockMvc.perform(get("/api/notifications").with(authenticatedUser(testUser)).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value(newer.getId().toString()))
                .andExpect(jsonPath("$.items[0].payload.listingTitle").value("Newer"))
                .andExpect(jsonPath("$.items[0].userId").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.items[0].type").value("listing-created"))
                .andExpect(jsonPath("$.items[0].createdAt").value(org.hamcrest.Matchers.endsWith("Z")))
                .andExpect(jsonPath("$.items[0].listingId").value(newer.getListingId().toString()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.unreadCount").value(1));
        mockMvc.perform(get("/api/notifications").with(authenticatedUser(testUser)).param("size", "1").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].id").value(older.getId().toString()));
        mockMvc.perform(get("/api/notifications").with(authenticatedUser(testUser)).param("unreadOnly", "true"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].id").value(older.getId().toString()));
        mockMvc.perform(get("/api/notifications/unread-count").with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.unreadCount").value(1));
    }

    @Test
    void markingOneReadIsIdempotentAndPersistsAcrossRequests() throws Exception {
        var notification = create(testUser, "Mine", false);
        String response = mockMvc.perform(put("/api/notifications/{id}/read", notification.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.readAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        var readAt = objectMapper.readTree(response).path("readAt").asString();
        assertThat(readAt).endsWith("Z");
        assertThat(Instant.parse(readAt)).isBeforeOrEqualTo(Instant.now());
        mockMvc.perform(put("/api/notifications/{id}/read", notification.getId()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.readAt").value(readAt));
        mockMvc.perform(get("/api/notifications").with(authenticatedUser(testUser)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].readAt").value(readAt))
                .andExpect(jsonPath("$.unreadCount").value(0));
    }

    @Test
    void readAllOnlyAffectsCurrentUserAndIsRetrySafe() throws Exception {
        var read = create(testUser, "Already read", true);
        var unread = create(testUser, "Unread", false);
        var privateNotification = create(otherUser(), "Private", false);
        for (int attempt = 0; attempt < 2; attempt++) {
            mockMvc.perform(put("/api/notifications/read-all").with(authenticatedUser(testUser))).andExpect(status().isNoContent());
        }
        assertThat(notifications.findById(unread.getId()).orElseThrow().getReadAt()).isNotNull();
        assertThat(notifications.findById(privateNotification.getId()).orElseThrow().getReadAt()).isNull();
        assertThat(notifications.findById(read.getId()).orElseThrow().getReadAt()).isEqualTo(read.getReadAt());
    }

    @Test
    void cannotReadAnotherUsersNotificationAndMissingIdsReturn404() throws Exception {
        var notification = create(otherUser(), "Private", false);
        for (UUID id : new UUID[]{notification.getId(), UUID.randomUUID()}) {
            mockMvc.perform(put("/api/notifications/{id}/read", id).with(authenticatedUser(testUser))).andExpect(status().isNotFound());
        }
        assertThat(notifications.findById(notification.getId()).orElseThrow().getReadAt()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"page=-1", "size=0", "size=101", "page=bad", "unreadOnly=bad"})
    void validatesPaginationAndFilters(String query) throws Exception {
        mockMvc.perform(get("/api/notifications?" + query).with(authenticatedUser(testUser))).andExpect(status().isBadRequest());
    }

    @Test
    void endpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/notifications/unread-count")).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/notifications/{id}/read", UUID.randomUUID())).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/notifications/read-all")).andExpect(status().isUnauthorized());
    }

    private User otherUser() {
        return userRepository.save(User.builder().id(UUID.randomUUID())
                .email("other@example.com").firstName("Other").lastName("User").build());
    }

    private Notification create(User user, String title, boolean read) {
        return notifications.saveAndFlush(Notification.builder().userId(user.getId())
                .type(NotificationType.LISTING_CREATED).payload(Map.of("listingTitle", title)).listingId(UUID.randomUUID())
                .readAt(read ? Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS).truncatedTo(java.time.temporal.ChronoUnit.SECONDS) : null).build());
    }
}
