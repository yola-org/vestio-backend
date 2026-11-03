# Vestio Backend - Testing Guide

This document explains the testing strategy and provides guidance on writing tests for the Vestio Backend.

## Testing Strategy

The project uses a combination of:
- **Integration Tests**: Using `@SpringBootTest` to test services and controllers with a real ApplicationContext and H2 database.
- **Unit Tests**: For isolated logic where applicable.

## Base Test Classes

To simplify test writing and ensure consistency, use the following base classes:

### `AbstractServiceTest`
Located at `src/test/java/org/app/vestiobackend/service/AbstractServiceTest.java`.
- **Purpose**: Base for service layer integration tests.
- **Features**:
    - Automatically cleans the database (`@AfterEach` and `@BeforeEach`).
    - Mocks `CognitoIdentityProviderClient`.
    - Provides helper methods like `createTestUser`, `createTestListing`.
    - `setupSecurityContext(email)`: Mocks the Spring Security context with a JWT containing the given email.
- **Configuration**: Uses `@ActiveProfiles("test")`.

### `AbstractControllerTest`
Located at `src/test/java/org/app/vestiobackend/controller/AbstractControllerTest.java`.
- **Purpose**: Base for controller layer integration tests using `MockMvc`.
- **Features**:
    - `authenticatedUser(email)`: `RequestPostProcessor` to simulate an authenticated user in `mockMvc` calls.
    - Example usage: `mockMvc.perform(get("/api/listings").with(authenticatedUser("test@example.com")))`
    - Pre-populates default data (test user, city, category).

## How to Write New Tests

### Service Tests
1. Extend `AbstractServiceTest`.
2. Inject your service using `@Autowired`.
3. Use `testUser`, `testCity`, `testCategory` for relationships.

```java
public class MyServiceTest extends AbstractServiceTest {
    @Autowired
    private MyService myService;

    @Test
    void testSomething() {
        // testUser is already setup in SecurityContext
        myService.doSomething();
    }
}
```

### Controller Tests
1. Extend `AbstractControllerTest`.
2. Use `mockMvc` to perform requests.
3. Use `authenticatedUser()` to simulate login.

```java
public class MyControllerTest extends AbstractControllerTest {
    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(post("/api/resource")
                .with(authenticatedUser(TEST_EMAIL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }
}
```

## Mocking Cognito
The `CognitoIdentityProviderClient` is mocked in base classes. If you need to verify Cognito interactions, use Mockito on the `cognitoClient` field.

## Database
Tests use an in-memory H2 database configured in `src/test/resources/application.yaml`. Liquibase migrations are applied automatically during test startup.

## Local Profile
The `local` profile is designed for local development and manual testing. It provides a more persistent and pre-populated environment compared to the `test` profile.

### Key Features of the `local` Profile:
- **File-based Database**: Unlike the in-memory database used in tests, the `local` profile uses a file-based H2 database located at `./data/vestiodb`. This ensures that data persists across application restarts.
- **Cognito Sync**:
    - Automatically syncs all local test users to the configured AWS Cognito User Pool.
    - If a user from `local_test_data.sql` does not exist in Cognito, it is created with the default password `Password123!`.
    - This ensures that local accounts are fully functional with real Cognito authentication.
- **Data Initialization**:
    - `DatabaseInitializer`: Automatically loads core reference data (Cities and Categories) from `src/main/resources/sql/initial_data.sql`.
    - `LocalDataInitializer`: Populates the database with sample users and listings from `src/main/resources/sql/local_test_data.sql` if the database is empty.
- **Liquibase**: Automatically updates the database schema to the latest version.

To run the application with the local profile:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```
