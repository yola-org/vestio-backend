package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.UserProfileDto;
import org.app.vestiobackend.dto.UserPreferencesDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.app.vestiobackend.service.SqsEventPublisher;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import java.util.Base64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerTest extends AbstractControllerTest {

    @Autowired
    private PlatformTransactionManager transactions;

    @MockitoBean
    private S3Client s3Client;
    @MockitoBean
    private SqsEventPublisher sqsPublisher;

    private MockMultipartFile profileImage() {
        return new MockMultipartFile("image", "photo.png", "image/png", Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+j/1sAAAAASUVORK5CYII="));
    }

    @Test
    void uploadsProfileImageDirectlyAndDeletesPreviousOnlyAfterReplacement() throws Exception {
        mockMvc.perform(multipart("/api/profile/image").file(profileImage()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileImageKey").isString());
        String firstKey = userRepository.findById(testUser.getId()).orElseThrow().getProfileImageKey();
        assertThat(firstKey).startsWith(testUser.getId().toString() + "/profile/").endsWith(".png");
        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getValue().key()).isEqualTo("users/" + firstKey);
        assertThat(request.getValue().contentType()).isEqualTo("image/png");
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));

        mockMvc.perform(multipart("/api/profile/image").file(profileImage()).with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
        String secondKey = userRepository.findById(testUser.getId()).orElseThrow().getProfileImageKey();
        assertThat(secondKey).isNotEqualTo(firstKey);
        ArgumentCaptor<DeleteObjectRequest> deletion = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(deletion.capture());
        assertThat(deletion.getValue().key()).isEqualTo("users/" + firstKey);
        verifyNoInteractions(sqsPublisher);
    }

    @Test
    void failedUploadPreservesExistingProfileImage() throws Exception {
        testUser.setProfileImageKey(testUser.getId().toString() + "/profile/old.png");
        userRepository.save(testUser);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(software.amazon.awssdk.services.s3.model.S3Exception.builder().statusCode(503).message("Storage unavailable").build());
        mockMvc.perform(multipart("/api/profile/image").file(profileImage()).with(authenticatedUser(testUser)))
                .andExpect(status().is5xxServerError());
        assertThat(userRepository.findById(testUser.getId()).orElseThrow().getProfileImageKey())
                .isEqualTo(testUser.getProfileImageKey());
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verifyNoInteractions(sqsPublisher);
    }

    @Test
    void rollbackKeepsPreviousImageAndRemovesUncommittedUpload() throws Exception {
        String previousKey = testUser.getId().toString() + "/profile/previous.png";
        testUser.setProfileImageKey(previousKey);
        userRepository.save(testUser);
        AtomicReference<String> uploadedKey = new AtomicReference<>();
        new TransactionTemplate(transactions).executeWithoutResult(transaction -> {
            try {
                var response = mockMvc.perform(multipart("/api/profile/image").file(profileImage())
                                .with(authenticatedUser(testUser)))
                        .andExpect(status().isOk()).andReturn().getResponse();
                uploadedKey.set(objectMapper.readValue(response.getContentAsString(), UserProfileDto.class).getProfileImageKey());
                transaction.setRollbackOnly();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
        assertThat(userRepository.findById(testUser.getId()).orElseThrow().getProfileImageKey()).isEqualTo(previousKey);
        ArgumentCaptor<DeleteObjectRequest> deletion = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(deletion.capture());
        assertThat(deletion.getValue().key()).isEqualTo("users/" + uploadedKey.get()).isNotEqualTo("users/" + previousKey);
        verifyNoInteractions(sqsPublisher);
    }

    @Test
    void rejectsEmptyAndDisguisedImagesBeforeStorage() throws Exception {
        mockMvc.perform(multipart("/api/profile/image")
                        .file(new MockMultipartFile("image", "photo.png", "image/png", new byte[0]))
                        .with(authenticatedUser(testUser))).andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/profile/image")
                        .file(new MockMultipartFile("image", "photo.png", "image/png", "not an image".getBytes()))
                        .with(authenticatedUser(testUser))).andExpect(status().isUnsupportedMediaType());
        verifyNoInteractions(s3Client, sqsPublisher);
    }

    @Test
    void rejectsOversizedUnsupportedAndMismatchedImageTypes() throws Exception {
        mockMvc.perform(multipart("/api/profile/image")
                        .file(new MockMultipartFile("image", "photo.png", "image/png", new byte[10 * 1024 * 1024 + 1]))
                        .with(authenticatedUser(testUser))).andExpect(status().isPayloadTooLarge());
        mockMvc.perform(multipart("/api/profile/image")
                        .file(new MockMultipartFile("image", "photo.svg", "image/svg+xml", "<svg/>".getBytes()))
                        .with(authenticatedUser(testUser))).andExpect(status().isUnsupportedMediaType());
        mockMvc.perform(multipart("/api/profile/image")
                        .file(new MockMultipartFile("image", "photo.png", "image/jpeg", profileImage().getBytes()))
                        .with(authenticatedUser(testUser))).andExpect(status().isUnsupportedMediaType());
        verifyNoInteractions(s3Client, sqsPublisher);
    }

    @Test
    void requiresAuthenticationForProfileImage() throws Exception {
        mockMvc.perform(multipart("/api/profile/image").file(profileImage()))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(s3Client, sqsPublisher);
    }

    @Test
    void profileUpdateCannotSetImageKeyWithoutUploading() throws Exception {
        mockMvc.perform(put("/api/profile").with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileImageKey\":\"users/someone-else/profile/image.png\"}"))
                .andExpect(status().isOk());
        assertThat(userRepository.findById(testUser.getId()).orElseThrow().getProfileImageKey()).isNull();
        verifyNoInteractions(s3Client, sqsPublisher);
    }

    @Test
    public void getProfileShouldReturnUserProfile() throws Exception {
        testUser.setAge(25);
        testUser.setGender("MALE");
        testUser.setCity("Paris");
        userRepository.save(testUser);

        mockMvc.perform(get("/api/profile")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.city").value("Paris"));
    }

    @Test
    public void updatePreferencesShouldReturnOk() throws Exception {
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setCategoryIds(List.of(testCategory.getId()));
        dto.setGender("MALE");

        mockMvc.perform(post("/api/profile/preferences")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/profile/preferences")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.categoryIds[0]").value(testCategory.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value(testCategory.getName()));
    }

    @Test
    public void updateProfileShouldUpdateOnlyProvidedFields() throws Exception {
        testUser.setFirstName("Original");
        testUser.setLastName("User");
        testUser.setAge(30);
        userRepository.save(testUser);

        UserProfileDto updateDto = UserProfileDto.builder()
                .firstName("Updated")
                .build();

        mockMvc.perform(put("/api/profile")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    public void getProfileUnauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void sequentialUpdatesViaControllerShouldWorkCorrectly() throws Exception {
        // 1. Update Profile City
        UserProfileDto cityDto = UserProfileDto.builder()
                .city("Casablanca")
                .build();

        mockMvc.perform(put("/api/profile")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Casablanca"));

        // 2. Update Profile Gender
        UserProfileDto genderDto = UserProfileDto.builder()
                .gender("MALE")
                .build();

        mockMvc.perform(put("/api/profile")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.city").value("Casablanca")); // Still there

        // 3. Update Preferences
        UserPreferencesDto prefDto = new UserPreferencesDto();
        prefDto.setCategoryIds(List.of(testCategory.getId()));
        prefDto.setGender("FEMALE");

        mockMvc.perform(post("/api/profile/preferences")
                        .with(authenticatedUser(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prefDto)))
                .andExpect(status().isOk());

        // 4. Verify Preferences
        mockMvc.perform(get("/api/profile/preferences")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender").value("FEMALE"))
                .andExpect(jsonPath("$.categoryIds[0]").value(testCategory.getId().toString()));
    }
}
