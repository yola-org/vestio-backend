package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.config.properties.AwsProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3AssetsService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public String putUserAsset(UUID userId, UUID assetId, String fileName, byte[] content, String contentType) {
        String key = assetKey(awsProperties.s3().usersPrefix(), userId, assetId, fileName);
        putObject(key, content, contentType);
        return key;
    }

    public String putListingAsset(UUID listingId, UUID assetId, String fileName, byte[] content, String contentType) {
        String key = assetKey(awsProperties.s3().listingsPrefix(), listingId, assetId, fileName);
        putObject(key, content, contentType);
        return key;
    }

    public byte[] getUserAsset(UUID userId, UUID assetId, String fileName) {
        return getObject(assetKey(awsProperties.s3().usersPrefix(), userId, assetId, fileName));
    }

    public byte[] getListingAsset(UUID listingId, UUID assetId, String fileName) {
        return getObject(assetKey(awsProperties.s3().listingsPrefix(), listingId, assetId, fileName));
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

    private String assetKey(String assetPrefix, UUID ownerId, UUID assetId, String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Asset file name must be a single non-empty path segment");
        }
        String extension = extension(fileName);
        return "%s/%s/%s/original%s".formatted(assetPrefix, ownerId, assetId, extension);
    }

    private String prefix(String assetPrefix, UUID ownerId) {
        return "%s/%s/".formatted(assetPrefix, ownerId);
    }

    private String extension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}
