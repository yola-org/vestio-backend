package org.app.vestiobackend.service;

import org.app.vestiobackend.config.properties.ImageUploadProperties;
import org.app.vestiobackend.service.AssetService.UploadedAsset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    private static final int MAX_IMAGE_BYTES = 8;

    @Mock
    private S3AssetsService storage;

    @Mock
    private SqsEventPublisher eventPublisher;

    private AssetService assetService;
    private final UUID listingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ImageUploadProperties properties = new ImageUploadProperties(DataSize.ofBytes(MAX_IMAGE_BYTES), Set.of("png"));
        assetService = new AssetService(properties, storage, eventPublisher);
    }

    @Test
    void uploadsNonEmptyImageAtSizeLimitAndReturnsItsIdAndKey() {
        byte[] content = new byte[MAX_IMAGE_BYTES];
        MockMultipartFile image = new MockMultipartFile("images", "C:\\fakepath\\photo.PNG", "image/png", content);
        MockMultipartFile empty = new MockMultipartFile("images", "empty.png", "image/png", new byte[0]);
        String objectKey = "listings/test/original.png";
        when(storage.putListingAsset(eq(listingId), any(UUID.class), eq("photo.PNG"), eq(content), eq("image/png")))
                .thenReturn(objectKey);

        List<UploadedAsset> assets = assetService.uploadListingImages(listingId, List.of(empty, image));

        assertThat(assets).hasSize(1);
        UploadedAsset asset = assets.getFirst();
        assertThat(asset.id()).isNotNull();
        assertThat(asset.objectKey()).isEqualTo(objectKey);
        verify(storage).putListingAsset(listingId, asset.id(), "photo.PNG", content, "image/png");
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void rejectsBatchWithOnlyEmptyFiles() {
        MockMultipartFile empty = new MockMultipartFile("images", "empty.png", "image/png", new byte[0]);

        assertUploadRejected(List.of(empty), HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsEntireBatchBeforeUploadingIfAnyImageExceedsLimit() {
        MockMultipartFile valid = new MockMultipartFile("images", "valid.png", "image/png", new byte[1]);
        MockMultipartFile oversized = new MockMultipartFile(
                "images", "large.png", "image/png", new byte[MAX_IMAGE_BYTES + 1]);

        assertUploadRejected(List.of(valid, oversized), HttpStatus.CONTENT_TOO_LARGE);
    }

    @ParameterizedTest
    @CsvSource({"photo.jpg, image/jpeg", "photo.png, text/plain", "photo, image/png"})
    void rejectsUnsupportedExtensionsAndContentTypes(String filename, String contentType) {
        MockMultipartFile image = new MockMultipartFile("images", filename, contentType, new byte[1]);

        assertUploadRejected(List.of(image), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void reportsUnreadableImageWithoutCallingStorage() throws IOException {
        MultipartFile image = mock(MultipartFile.class);
        IOException readFailure = new IOException("Read failed");
        when(image.getOriginalFilename()).thenReturn("photo.png");
        when(image.getContentType()).thenReturn("image/png");
        when(image.getBytes()).thenThrow(readFailure);

        assertThatThrownBy(() -> assetService.uploadListingImages(listingId, List.of(image)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasCause(readFailure);
        verifyNoInteractions(storage, eventPublisher);
    }

    @Test
    void queuesOptimizationWithS3ObjectKeysInUploadOrder() {
        UUID userId = UUID.randomUUID();
        String firstKey = "listings/test/first/original.png";
        String secondKey = "listings/test/second/original.png";
        List<UploadedAsset> assets = List.of(
                new UploadedAsset(UUID.randomUUID(), firstKey),
                new UploadedAsset(UUID.randomUUID(), secondKey));

        assetService.queueListingImageOptimization(userId, listingId, assets);

        verify(eventPublisher).publishListingAssetsAdded(userId, listingId, List.of(firstKey, secondKey));
        verifyNoInteractions(storage);
    }

    private void assertUploadRejected(List<MultipartFile> files, HttpStatus expectedStatus) {
        assertThatThrownBy(() -> assetService.uploadListingImages(listingId, files))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(expectedStatus));
        verifyNoInteractions(storage, eventPublisher);
    }
}
