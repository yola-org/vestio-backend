package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.config.properties.ImageUploadProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final ImageUploadProperties imageUploadProperties;
    private final S3AssetsService s3AssetsService;
    private final SqsEventPublisher sqsEventPublisher;

    public List<UploadedAsset> uploadListingImages(UUID listingId, List<MultipartFile> images) {
        List<MultipartFile> files = images.stream()
                .filter(image -> !image.isEmpty())
                .toList();
        if (files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one non-empty image is required");
        }
        files.forEach(this::validateImage);

        return files.stream()
                .map(image -> uploadListingImage(listingId, image))
                .toList();
    }

    public void queueListingImageOptimization(UUID userId, UUID listingId, List<UploadedAsset> assets) {
        List<String> objectKeys = assets.stream().map(UploadedAsset::objectKey).toList();
        sqsEventPublisher.publishListingAssetsAdded(userId, listingId, objectKeys);
    }

    private void validateImage(MultipartFile image) {
        long maxBytes = imageUploadProperties.maxFileSize().toBytes();
        if (image.getSize() > maxBytes) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, "Images must be at most " + maxBytes + " bytes");
        }
        String contentType = image.getContentType();
        String filename = imageFilename(image);
        int dotIndex = filename.lastIndexOf('.');
        String extension = dotIndex > 0 ? filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT) : "";
        if (contentType == null || !contentType.startsWith("image/")
                || !imageUploadProperties.allowedExtensions().contains(extension)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Allowed image extensions: " + String.join(", ", imageUploadProperties.allowedExtensions().stream().sorted().toList()));
        }
    }

    private String imageFilename(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String normalizedFilename = originalFilename == null ? "" : originalFilename.replace('\\', '/');
        return normalizedFilename.substring(normalizedFilename.lastIndexOf('/') + 1);
    }

    private UploadedAsset uploadListingImage(UUID listingId, MultipartFile image) {
        UUID assetId = UUID.randomUUID();
        try {
            String objectKey = s3AssetsService.putListingAsset(listingId, assetId, imageFilename(image),
                    image.getBytes(), image.getContentType());
            return new UploadedAsset(assetId, objectKey);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read uploaded image", exception);
        }
    }

    public record UploadedAsset(UUID id, String objectKey) {
    }
}
