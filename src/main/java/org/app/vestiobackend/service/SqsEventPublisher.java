package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.config.AwsProperties;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@ConditionalOnProperty(prefix = "aws.s3", name = "assets-bucket")
@RequiredArgsConstructor
public class SqsEventPublisher {

    private final SqsClient sqsClient;
    private final AwsProperties awsProperties;

    public void publishUserEvent(String messageBody) {
        publish(awsProperties.sqs().queues().userEventsUrl(), messageBody);
    }

    public void publishListingEvent(String messageBody) {
        publish(awsProperties.sqs().queues().listingEventsUrl(), messageBody);
    }

    private void publish(String queueUrl, String messageBody) {
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());
    }
}
