package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.config.properties.AwsProperties;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.InternalException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SqsEventPublisher {

    private final SqsClient sqsClient;
    private final AwsProperties awsProperties;
    private final ObjectMapper objectMapper;

    public void publishListingAssetsAdded(UUID userId, UUID listingId, List<String> objectKeys) {
        try {
            publish(awsProperties.sqs().queues().imageProcessing(), objectMapper.writeValueAsString(
                    new ListingAssetsAddedEvent(userId, listingId, objectKeys)));
        } catch (JacksonException exception) {
            throw new InternalException(ErrorCode.ERR_INTERNAL_SERVER_ERROR,
                    "Could not serialize listing-assets event", exception);
        }
    }

    private void publish(String queueUrl, String messageBody) {
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());
    }

    private record ListingAssetsAddedEvent(UUID userId, UUID listingId, List<String> assetKeys) {
    }
}
