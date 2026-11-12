package org.app.vestiobackend.service;

import org.app.vestiobackend.config.properties.AwsProperties;
import org.app.vestiobackend.config.properties.AwsProperties.S3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class S3AssetsServiceTest {

    private static final String BUCKET = "assets-test";
    private static final byte[] CONTENT = {1, 2, 3};

    private final S3Client s3 = mock(S3Client.class);
    private final S3AssetsService assets = new S3AssetsService(
            s3, new AwsProperties("ca-central-1", new S3(BUCKET, "users", "listings"), null));
    private final UUID ownerId = UUID.randomUUID();
    private final UUID assetId = UUID.randomUUID();

    @Test
    void uploadsUserAssetWithNormalizedExtensionAndOriginalContent() throws Exception {
        String key = "users/%s/%s/original.png".formatted(ownerId, assetId);
        ArgumentCaptor<RequestBody> body = ArgumentCaptor.forClass(RequestBody.class);
        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);

        assertThat(assets.putUserAsset(ownerId, assetId, "Photo.PNG", CONTENT, "image/png")).isEqualTo(key);

        verify(s3).putObject(request.capture(), body.capture());
        assertThat(request.getValue().bucket()).isEqualTo(BUCKET);
        assertThat(request.getValue().key()).isEqualTo(key);
        assertThat(request.getValue().contentType()).isEqualTo("image/png");
        try (var stream = body.getValue().contentStreamProvider().newStream()) {
            assertThat(stream.readAllBytes()).containsExactly(CONTENT);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"photo", ".hidden", "photo."})
    void storesListingAssetWithoutExtensionWhenFilenameHasNoExtension(String filename) {
        String key = "listings/%s/%s/original".formatted(ownerId, assetId);

        assertThat(assets.putListingAsset(ownerId, assetId, filename, CONTENT, "image/png")).isEqualTo(key);

        verify(s3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "folder/photo.png", "folder\\photo.png"})
    void rejectsInvalidPathSegmentsBeforeCallingS3(String filename) {
        assertThatIllegalArgumentException().isThrownBy(() -> assets.getUserAsset(ownerId, assetId, filename));
        verifyNoInteractions(s3);
    }

    @Test
    void downloadsUserAndListingAssetsFromTheirOwnPrefixes() {
        GetObjectRequest userRequest = downloadRequest("users");
        GetObjectRequest listingRequest = downloadRequest("listings");
        when(s3.getObjectAsBytes(userRequest)).thenReturn(
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), CONTENT));
        when(s3.getObjectAsBytes(listingRequest)).thenReturn(
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), new byte[]{4}));

        assertThat(assets.getUserAsset(ownerId, assetId, "photo.PNG")).containsExactly(CONTENT);
        assertThat(assets.getListingAsset(ownerId, assetId, "photo.png")).containsExactly((byte) 4);
    }

    @Test
    void listsAssetsByOwnerPrefixAcrossPages() {
        S3Object first = S3Object.builder().key("first").build();
        S3Object second = S3Object.builder().key("second").build();
        when(s3.listObjectsV2(any(ListObjectsV2Request.class))).thenAnswer(invocation -> {
            ListObjectsV2Request request = invocation.getArgument(0);
            if (request.continuationToken() == null) {
                return ListObjectsV2Response.builder().contents(first)
                        .isTruncated(true).nextContinuationToken("next").build();
            }
            assertThat(request.continuationToken()).isEqualTo("next");
            return ListObjectsV2Response.builder().contents(second).isTruncated(false).build();
        });
        when(s3.listObjectsV2Paginator(ArgumentMatchers.<Consumer<ListObjectsV2Request.Builder>>any())).thenAnswer(invocation -> {
            Consumer<ListObjectsV2Request.Builder> configure = invocation.getArgument(0);
            ListObjectsV2Request.Builder request = ListObjectsV2Request.builder();
            configure.accept(request);
            return new ListObjectsV2Iterable(s3, request.build());
        });

        assertThat(assets.listUserAssets(ownerId)).containsExactly(first, second);
        assertThat(assets.listListingAssets(ownerId)).containsExactly(first, second);

        ArgumentCaptor<ListObjectsV2Request> pages = ArgumentCaptor.forClass(ListObjectsV2Request.class);
        verify(s3, times(4)).listObjectsV2(pages.capture());
        assertThat(pages.getAllValues()).allMatch(request -> BUCKET.equals(request.bucket()))
                .extracting(ListObjectsV2Request::prefix)
                .containsExactly("users/" + ownerId + "/", "users/" + ownerId + "/",
                        "listings/" + ownerId + "/", "listings/" + ownerId + "/");
    }

    private GetObjectRequest downloadRequest(String prefix) {
        return GetObjectRequest.builder().bucket(BUCKET)
                .key("%s/%s/%s/original.png".formatted(prefix, ownerId, assetId)).build();
    }
}
