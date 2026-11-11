package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.UserProfileDto;
import org.app.vestiobackend.dto.UserPreferencesDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.UserPreference;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerTest extends AbstractControllerTest {

    @Test
    public void getOnboardingStatusShouldReturnCorrectStatus() throws Exception {
        testUser.setAge(25);
        userRepository.save(testUser);
        
        userPreferenceRepository.save(UserPreference.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .category(testCategory)
                .build());

        mockMvc.perform(get("/api/profile/onboarding-status")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountCreated").value(true))
                .andExpect(jsonPath("$.additionalDataAdded").value(true))
                .andExpect(jsonPath("$.preferencesAdded").value(true))
                .andExpect(jsonPath("$.publishedSomething").value(false));
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
    public void getOnboardingStatusUnauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/profile/onboarding-status"))
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

        // 5. Verify Onboarding Status
        mockMvc.perform(get("/api/profile/onboarding-status")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountCreated").value(true))
                .andExpect(jsonPath("$.additionalDataAdded").value(true))
                .andExpect(jsonPath("$.preferencesAdded").value(true));
    }
}
