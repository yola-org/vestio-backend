package org.app.vestiobackend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties(String region, S3 s3, Sqs sqs) {

    public record S3(String assetsBucket, String usersPrefix, String listingsPrefix) {
    }

    public record Sqs(Queues queues) {
    }

    public record Queues(String imageProcessing) {
    }
}
