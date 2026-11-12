package org.app.vestiobackend.service;

import org.app.vestiobackend.config.properties.AwsProperties;
import org.app.vestiobackend.config.properties.AwsProperties.Queues;
import org.app.vestiobackend.config.properties.AwsProperties.Sqs;
import org.app.vestiobackend.http.errors.exception.InternalException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SqsEventPublisherTest {

    private static final String QUEUE_URL = "https://sqs.example.test/image-processing";

    private final SqsClient sqs = mock(SqsClient.class);
    private final AwsProperties properties = new AwsProperties("ca-central-1", null, new Sqs(new Queues(QUEUE_URL)));
    private final UUID userId = UUID.randomUUID();
    private final UUID listingId = UUID.randomUUID();

    @Test
    void sendsListingAssetEventToImageProcessingQueue() {
        ObjectMapper mapper = JsonMapper.builder().build();
        SqsEventPublisher publisher = new SqsEventPublisher(sqs, properties, mapper);
        List<String> keys = List.of("listings/one", "listings/two");

        publisher.publishListingAssetsAdded(userId, listingId, keys);

        ArgumentCaptor<SendMessageRequest> request = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqs).sendMessage(request.capture());
        assertThat(request.getValue().queueUrl()).isEqualTo(QUEUE_URL);
        assertThat(mapper.readTree(request.getValue().messageBody())).isEqualTo(mapper.valueToTree(
                new ExpectedEvent(userId, listingId, keys)));
    }

    @Test
    void wrapsSerializationFailureWithoutSendingMessage() {
        ObjectMapper mapper = mock(ObjectMapper.class);
        InvalidDefinitionException failure = mock(InvalidDefinitionException.class);
        when(mapper.writeValueAsString(any())).thenThrow(failure);
        SqsEventPublisher publisher = new SqsEventPublisher(sqs, properties, mapper);

        assertThatThrownBy(() -> publisher.publishListingAssetsAdded(userId, listingId, List.of("listings/one")))
                .hasCause(failure)
                .isInstanceOfSatisfying(InternalException.class, exception -> {
                    assertThat(exception.getMessage()).isEqualTo("Could not serialize listing-assets event");
                    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
        verifyNoInteractions(sqs);
    }

    private record ExpectedEvent(UUID userId, UUID listingId, List<String> assetKeys) {
    }
}
