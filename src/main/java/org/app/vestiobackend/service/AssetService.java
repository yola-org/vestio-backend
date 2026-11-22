package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkException;
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

    public String uploadUserProfileImage(UUID userId, MultipartFile image) {
        if (image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A non-empty image is required");
        }
        validateImage(image);
        try {
            byte[] content = image.getBytes();
            String extension;
            String contentType;
            if (content.length >= 8 && content[0] == (byte) 0x89 && content[1] == 'P' && content[2] == 'N'
                    && content[3] == 'G' && content[4] == 13 && content[5] == 10 && content[6] == 26 && content[7] == 10) {
                extension = "png";
                contentType = "image/png";
            } else if (content.length >= 3 && content[0] == (byte) 0xff && content[1] == (byte) 0xd8 && content[2] == (byte) 0xff) {
                extension = "jpg";
                contentType = "image/jpeg";
            } else if (content.length >= 12 && content[0] == 'R' && content[1] == 'I' && content[2] == 'F' && content[3] == 'F'
                    && content[8] == 'W' && content[9] == 'E' && content[10] == 'B' && content[11] == 'P') {
                extension = "webp";
                contentType = "image/webp";
            } else {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Use a JPEG, PNG or WebP profile image");
            }
            if (!contentType.equals(image.getContentType())) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Image content does not match its content type");
            }
            return s3AssetsService.putUserProfileImage(userId, UUID.randomUUID(), content, extension, contentType);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read uploaded image", exception);
        } catch (SdkException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Could not store profile image", exception);
        }
    }

    public void deleteUserProfileImage(UUID userId, String key) {
        s3AssetsService.deleteUserProfileImage(userId, key);
    }

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
