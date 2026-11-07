package org.app.vestiobackend.controller;

import tools.jackson.databind.ObjectMapper;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;
import org.app.vestiobackend.repository.UserPreferenceRepository;
import org.app.vestiobackend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserPreferenceRepository userPreferenceRepository;

    @Autowired
    protected org.app.vestiobackend.repository.ListingRepository listingRepository;

    @Autowired
    protected org.app.vestiobackend.repository.CityRepository cityRepository;

    @Autowired
    protected org.app.vestiobackend.repository.CategoryRepository categoryRepository;

    @Autowired
    protected org.app.vestiobackend.repository.SizeRepository sizeRepository;

    @Autowired
    protected org.app.vestiobackend.repository.SizeGroupRepository sizeGroupRepository;

    @MockitoBean
    protected CognitoIdentityProviderClient cognitoClient;

    protected final String TEST_EMAIL = "test@example.com";

    protected User testUser;
    protected City testCity;
    protected Category testCategory;
    protected org.app.vestiobackend.entity.Size testSize;
    protected org.app.vestiobackend.entity.SizeGroup testSizeGroup;

    @BeforeEach
    public void setup() {
        listingRepository.deleteAll();
        userPreferenceRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        cityRepository.deleteAll();
        sizeRepository.deleteAll();
        sizeGroupRepository.deleteAll();
        
        initializeDefaultData();
    }

    private void initializeDefaultData() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email(TEST_EMAIL)
                .firstName("Test")
                .lastName("User")
                .build();
        userRepository.save(testUser);

        testCity = City.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .name("Casablanca")
                .build();
        cityRepository.save(testCity);

        testSizeGroup = org.app.vestiobackend.entity.SizeGroup.builder()
                .id(UUID.randomUUID())
                .name("Clothing")
                .build();
        sizeGroupRepository.save(testSizeGroup);

        testSize = org.app.vestiobackend.entity.Size.builder()
                .id(UUID.randomUUID())
                .name("M")
                .sizeGroup(testSizeGroup)
                .build();
        sizeRepository.save(testSize);

        testCategory = Category.builder()
                .id(UUID.fromString("660e8400-e29b-41d4-a716-446655440000"))
                .name("Tops")
                .description("T-shirts, blouses, shirts")
                .sizeGroup(testSizeGroup)
                .build();
        categoryRepository.save(testCategory);
    }

    protected Listing createTestListing(String title, User user, City city, Category category) {
        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description("Default description")
                .price(new BigDecimal("100.00"))
                .quantity(1)
                .condition(Condition.GOOD)
                .contactTypes(List.of(ContactType.MEETUP))
                .status(ListingStatus.AVAILABLE)
                .user(user)
                .cities(List.of(city))
                .category(category)
                .colors(List.of("Black"))
                .build();
        return listingRepository.save(listing);
    }

    @AfterEach
    public void cleanUp() {
        listingRepository.deleteAll();
        userPreferenceRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        cityRepository.deleteAll();
        sizeRepository.deleteAll();
        sizeGroupRepository.deleteAll();
    }

    protected RequestPostProcessor authenticatedUser(String email) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.claim("email", email));
    }
}
