package org.app.vestiobackend.service;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.OnboardingStatusDto;
import org.app.vestiobackend.dto.UserProfileDto;
import org.app.vestiobackend.dto.UserPreferencesDto;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.UserPreference;
import org.app.vestiobackend.http.errors.ErrorCode;
import org.app.vestiobackend.http.errors.exception.ResourceNotFoundException;
import org.app.vestiobackend.mapper.CategoryMapper;
import org.app.vestiobackend.mapper.UserMapper;
import org.app.vestiobackend.repository.CategoryRepository;
import org.app.vestiobackend.repository.UserPreferenceRepository;
import org.app.vestiobackend.repository.UserRepository;
import org.app.vestiobackend.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing user profiles and preferences.
 * Handles onboarding status, retrieval of user profile data, and updating user preferences.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final CategoryRepository categoryRepository;
    private final UserContext userContext;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieves the onboarding status of the currently authenticated user.
     * The status includes whether the account is created, additional data (age, gender, city) is added,
     * preferences are selected, and if the user has published anything.
     *
     * @return An {@link OnboardingStatusDto} containing the completion status of various onboarding steps.
     * @throws ResourceNotFoundException if the user is not found in the local database.
     */
    public OnboardingStatusDto getOnboardingStatus() {
        String email = userContext.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "User not found in local database"));

        boolean additionalDataAdded = user.getAge() != null || user.getGender() != null || user.getCity() != null;
        boolean preferencesAdded = !userPreferenceRepository.findByUser(user).isEmpty();

        return OnboardingStatusDto.builder()
                .accountCreated(true)
                .additionalDataAdded(additionalDataAdded)
                .preferencesAdded(preferencesAdded)
                .publishedSomething(false) // Placeholder
                .build();
    }

    /**
     * Retrieves the profile data of the currently authenticated user.
     *
     * @return A {@link UserProfileDto} containing user profile information.
     */
    public UserProfileDto getCurrentUserProfile() {
        User user = userContext.getCurrentUser();
        return userMapper.toDto(user);
    }

    /**
     * Updates the preferences (favorite categories) for the currently authenticated user.
     * This implementation replaces all existing preferences with the new set provided.
     *
     * @param dto The DTO containing the list of category IDs to set as preferences.
     * @throws ResourceNotFoundException if any of the specified categories are not found.
     */
    @Transactional
    public void updatePreferences(UserPreferencesDto dto) {
        User user = userContext.getCurrentUser();
        
        // Simple strategy: delete existing and add new
        userPreferenceRepository.deleteByUser(user);

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            dto.getCategoryIds().forEach(categoryId -> {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ERR_RESOURCE_NOT_FOUND, "Category not found: " + categoryId));
                UserPreference preference = UserPreference.builder()
                        .id(UUID.randomUUID())
                        .user(user)
                        .category(category)
                        .gender(dto.getGender())
                        .build();
                userPreferenceRepository.save(preference);
            });
        } else if (dto.getGender() != null) {
            UserPreference preference = UserPreference.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .category(null)
                    .gender(dto.getGender())
                    .build();
            userPreferenceRepository.save(preference);
        }
    }

    /**
     * Retrieves the preferences (favorite categories) for the currently authenticated user.
     *
     * @return A {@link UserPreferencesDto} containing the list of category IDs.
     */
    public UserPreferencesDto getUserPreferences() {
        User user = userContext.getCurrentUser();
        List<UserPreference> prefs = userPreferenceRepository.findByUser(user);
        
        UserPreferencesDto dto = new UserPreferencesDto();
        if (!prefs.isEmpty()) {
            UserPreference first = prefs.get(0);
            dto.setGender(first.getGender());
            
            List<Category> categories = prefs.stream()
                    .map(UserPreference::getCategory)
                    .filter(Objects::nonNull)
                    .toList();
            
            dto.setCategoryIds(categories.stream().map(Category::getId).toList());
            dto.setCategories(categories.stream().map(categoryMapper::toDto).toList());
        }
        return dto;
    }

    /**
     * Updates the profile information of the currently authenticated user.
     *
     * @param dto The DTO containing the updated profile information.
     * @return The updated user profile DTO.
     */
    @Transactional
    public UserProfileDto updateProfile(UserProfileDto dto) {
        User user = userContext.getCurrentUser();
        userMapper.updateUserFromDto(dto, user);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}
