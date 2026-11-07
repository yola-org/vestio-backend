package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.config.AwsProperties;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "aws.s3", name = "assets-bucket")
@RequiredArgsConstructor
public class S3AssetsService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public void putUserAsset(UUID userId, String fileName, byte[] content, String contentType) {
        putObject(userKey(userId, fileName), content, contentType);
    }

    public void putListingAsset(UUID listingId, String fileName, byte[] content, String contentType) {
        putObject(listingKey(listingId, fileName), content, contentType);
    }

    public byte[] getUserAsset(UUID userId, String fileName) {
        return getObject(userKey(userId, fileName));
    }

    public byte[] getListingAsset(UUID listingId, String fileName) {
        return getObject(listingKey(listingId, fileName));
    }

    public List<S3Object> listUserAssets(UUID userId) {
        return listObjects(prefix(awsProperties.s3().usersPrefix(), userId));
    }

    public List<S3Object> listListingAssets(UUID listingId) {
        return listObjects(prefix(awsProperties.s3().listingsPrefix(), listingId));
    }

    private void putObject(String key, byte[] content, String contentType) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(awsProperties.s3().assetsBucket())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(content));
    }

    private byte[] getObject(String key) {
        return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                        .bucket(awsProperties.s3().assetsBucket())
                        .key(key)
                        .build())
                .asByteArray();
    }

    private List<S3Object> listObjects(String prefix) {
        return s3Client.listObjectsV2Paginator(request -> request
                        .bucket(awsProperties.s3().assetsBucket())
                        .prefix(prefix))
                .contents()
                .stream()
                .toList();
    }

    private String userKey(UUID userId, String fileName) {
        return key(awsProperties.s3().usersPrefix(), userId, fileName);
    }

    private String listingKey(UUID listingId, String fileName) {
        return key(awsProperties.s3().listingsPrefix(), listingId, fileName);
    }

    private String key(String assetPrefix, UUID ownerId, String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Asset file name must be a single non-empty path segment");
        }
        return "%s/%s/%s".formatted(assetPrefix, ownerId, fileName);
    }

    private String prefix(String assetPrefix, UUID ownerId) {
        return "%s/%s/".formatted(assetPrefix, ownerId);
    }
}
