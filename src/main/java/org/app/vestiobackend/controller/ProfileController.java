package org.app.vestiobackend.controller;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.OnboardingStatusDto;
import org.app.vestiobackend.dto.UserProfileDto;
import org.app.vestiobackend.dto.UserPreferencesDto;
import org.app.vestiobackend.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user profiles.
 * Provides endpoints for onboarding status, retrieving current user profile, and updating preferences.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Endpoint to retrieve the onboarding status of the current user.
     *
     * @return A response entity containing the onboarding status DTO.
     */
    @GetMapping("/onboarding-status")
    public ResponseEntity<OnboardingStatusDto> getOnboardingStatus() {
        return ResponseEntity.ok(profileService.getOnboardingStatus());
    }

    /**
     * Endpoint to retrieve the profile of the currently authenticated user.
     *
     * @return A response entity containing the user profile DTO.
     */
    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile() {
        return ResponseEntity.ok(profileService.getCurrentUserProfile());
    }

    /**
     * Endpoint to update the profile of the currently authenticated user.
     *
     * @param dto The DTO containing the updated profile information.
     * @return A response entity containing the updated user profile DTO.
     */
    @PutMapping
    public ResponseEntity<UserProfileDto> updateProfile(@RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(profileService.updateProfile(dto));
    }

    /**
     * Endpoint to update user preferences.
     *
     * @param dto The DTO containing the new preferences.
     * @return A response entity with a success status.
     */
    @PostMapping("/preferences")
    public ResponseEntity<Void> updatePreferences(@RequestBody UserPreferencesDto dto) {
        profileService.updatePreferences(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to retrieve user preferences.
     *
     * @return A response entity containing the user preferences DTO.
     */
    @GetMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> getPreferences() {
        return ResponseEntity.ok(profileService.getUserPreferences());
    }
}
