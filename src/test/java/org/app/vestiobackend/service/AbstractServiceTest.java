package org.app.vestiobackend.service;

import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Size;
import org.app.vestiobackend.entity.SizeGroup;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;
import org.app.vestiobackend.repository.CategoryRepository;
import org.app.vestiobackend.repository.CityRepository;
import org.app.vestiobackend.repository.ListingRepository;
import org.app.vestiobackend.repository.SizeGroupRepository;
import org.app.vestiobackend.repository.SizeRepository;
import org.app.vestiobackend.repository.UserPreferenceRepository;
import org.app.vestiobackend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractServiceTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserPreferenceRepository userPreferenceRepository;

    @Autowired
    protected ListingRepository listingRepository;

    @Autowired
    protected CityRepository cityRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected SizeRepository sizeRepository;

    @Autowired
    protected SizeGroupRepository sizeGroupRepository;

    @MockitoBean
    protected CognitoIdentityProviderClient cognitoClient;

    protected final String TEST_EMAIL = "test@example.com";

    protected User testUser;
    protected City testCity;
    protected Category testCategory;
    protected Size testSize;
    protected SizeGroup testSizeGroup;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
        listingRepository.deleteAll();
        userPreferenceRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        cityRepository.deleteAll();
        sizeRepository.deleteAll();
        sizeGroupRepository.deleteAll();

        initializeDefaultData();
        setupSecurityContext(testUser);
    }

    private void initializeDefaultData() {
        testUser = createTestUser(TEST_EMAIL);

        testCity = City.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .name("Casablanca")
                .build();
        cityRepository.save(testCity);

        testSizeGroup = SizeGroup.builder()
                .id(UUID.randomUUID())
                .name("Clothing")
                .build();
        sizeGroupRepository.save(testSizeGroup);

        testSize = Size.builder()
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

    protected void setupSecurityContext(User user) {
        setupSecurityContext(user.getCognitoSub());
    }

    protected void setupSecurityContext(String sub) {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .subject(sub)
                .claim("username", sub)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void tearDown() {
        listingRepository.deleteAll();
        userPreferenceRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        cityRepository.deleteAll();
        sizeRepository.deleteAll();
        sizeGroupRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    protected User createTestUser(String email) {
        User user = User.builder()
                .id(UUID.randomUUID())
                .cognitoSub(UUID.randomUUID().toString())
                .email(email)
                .firstName("Test")
                .lastName("User")
                .build();
        return userRepository.save(user);
    }
}
