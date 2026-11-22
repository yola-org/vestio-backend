package org.app.vestiobackend.events;

import tools.jackson.databind.JsonNode;
import java.io.InputStream;
import com.vestio.common.event.Event;
import com.vestio.common.event.EventType;

import com.vestio.common.entity.Notification;
import com.vestio.common.entity.enums.NotificationType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;

class NotificationEventContractTest {
    @ParameterizedTest
    @CsvSource({"notification-event.json, OFFER_CREATED, false", "offer-sent-event.json, OFFER_SENT, true"})
    void roundTripsNotificationEnvelope(String fixture, NotificationType type, boolean hasListingId) throws Exception {
        JsonMapper mapper = JsonMapper.builder().build();
        try (InputStream input = getClass().getResourceAsStream("/contracts/" + fixture)) {
            assertNotNull(input);
            JsonNode expected = mapper.readTree(input);
            Event<?> event = mapper.treeToValue(expected, Event.class);
            assertEquals(EventType.NOTIFICATION, event.type());
            Notification notification = mapper.convertValue(event.payload(), Notification.class);
            assertEquals(hasListingId, notification.getListingId() != null);
            assertEquals(type, notification.getType());
            assertEquals(notification.getId(), event.id());
            assertEquals(expected, mapper.valueToTree(event));
        }
    }
}
