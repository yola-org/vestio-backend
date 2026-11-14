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
The `local` profile uses dev PostgreSQL and Cognito. Liquibase applies pending schema migrations at startup, then the application provisions the default dev accounts. Startup skips SQL seeding.

To run the application with the local profile:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```
