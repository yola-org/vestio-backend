package org.app.vestiobackend.service;

import org.app.vestiobackend.dto.OnboardingStatusDto;
import org.app.vestiobackend.dto.UserProfileDto;
import org.app.vestiobackend.dto.UserPreferencesDto;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.UserPreference;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
public class ProfileServiceTestIntegrationTest extends AbstractServiceTest {

    @Autowired
    private ProfileService profileService;

    @Test
    public void getOnboardingStatusShouldReturnCorrectStatus() {
        // Given
        testUser.setAge(25);
        userRepository.save(testUser);

        userPreferenceRepository.save(UserPreference.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .category(testCategory)
                .build());

        // When
        OnboardingStatusDto status = profileService.getOnboardingStatus();

        // Then
        assertTrue(status.isAccountCreated());
        assertTrue(status.isAdditionalDataAdded());
        assertTrue(status.isPreferencesAdded());
        assertFalse(status.isPublishedSomething());
    }

    @Test
    public void getOnboardingStatusWithNoAdditionalDataShouldReturnFalse() {
        // When
        OnboardingStatusDto status = profileService.getOnboardingStatus();

        // Then
        assertTrue(status.isAccountCreated());
        assertFalse(status.isAdditionalDataAdded());
        assertFalse(status.isPreferencesAdded());
    }

    @Test
    public void updatePreferencesShouldSaveNewPreferences() {
        // Given
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setCategoryIds(List.of(testCategory.getId()));
        dto.setGender("FEMALE");

        // When
        profileService.updatePreferences(dto);

        // Then
        User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        List<UserPreference> preferences = userPreferenceRepository.findByUser(user);
        assertEquals(1, preferences.size());
        UserPreference pref = preferences.get(0);
        assertEquals(testCategory.getId(), pref.getCategory().getId());
        assertEquals("FEMALE", pref.getGender());
    }

    @Test
    public void getUserPreferencesShouldReturnAllFields() {
        // Given
        UserPreferencesDto saveDto = new UserPreferencesDto();
        saveDto.setCategoryIds(List.of(testCategory.getId()));
        saveDto.setGender("MALE");
        profileService.updatePreferences(saveDto);

        // When
        UserPreferencesDto result = profileService.getUserPreferences();

        // Then
        assertEquals("MALE", result.getGender());
        assertEquals(1, result.getCategoryIds().size());
        assertEquals(testCategory.getId(), result.getCategoryIds().get(0));
        assertEquals(1, result.getCategories().size());
        assertEquals(testCategory.getName(), result.getCategories().get(0).getName());
    }

    @Test
    public void updatePreferencesShouldReplaceExistingPreferences() {
        // Given
        userPreferenceRepository.save(UserPreference.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .category(testCategory)
                .build());

        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setCategoryIds(List.of(testCategory.getId()));

        // When
        profileService.updatePreferences(dto);

        // Then
        List<UserPreference> preferences = userPreferenceRepository.findByUser(testUser);
        assertEquals(1, preferences.size());
        assertEquals(testCategory.getId(), preferences.get(0).getCategory().getId());
    }

    @Test
    public void getOnboardingStatusWhenUserNotFoundShouldThrowException() {
        // Given
        userRepository.deleteAll(); // Remove the default testUser
        SecurityContextHolder.clearContext();
        setupSecurityContext("notfound@example.com");

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> profileService.getOnboardingStatus());
    }

    @Test
    public void sequentialUpdatesShouldWorkCorrectly() {
        // 1. Update Profile City
        UserProfileDto profileDto = UserProfileDto.builder()
                .city("Casablanca")
                .build();
        profileService.updateProfile(profileDto);

        User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertEquals("Casablanca", user.getCity());

        // 2. Update Profile Gender
        profileDto = UserProfileDto.builder()
                .gender("MALE")
                .build();
        profileService.updateProfile(profileDto);

        user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertEquals("MALE", user.getGender());
        assertEquals("Casablanca", user.getCity()); // Ensure Casablanca was not overwritten by null

        // 3. Update Preferences
        UserPreferencesDto prefDto = new UserPreferencesDto();
        prefDto.setCategoryIds(List.of(testCategory.getId()));
        prefDto.setGender("FEMALE");
        profileService.updatePreferences(prefDto);

        UserPreferencesDto resultPrefs = profileService.getUserPreferences();
        assertEquals("FEMALE", resultPrefs.getGender());
        assertEquals(1, resultPrefs.getCategoryIds().size());

        // 4. Verify Onboarding Status
        OnboardingStatusDto status = profileService.getOnboardingStatus();
        assertTrue(status.isAccountCreated());
        assertTrue(status.isAdditionalDataAdded());
        assertTrue(status.isPreferencesAdded());

        // 5. Update Profile Age
        profileDto = UserProfileDto.builder()
                .age(30)
                .build();
        profileService.updateProfile(profileDto);

        user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertEquals(30, user.getAge());
        assertEquals("MALE", user.getGender());
        assertEquals("Casablanca", user.getCity());
    }
}
