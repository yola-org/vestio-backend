# Vestio Backend - Testing Guide

## Organization and naming

All tests live under `src/test/java/org/app/vestiobackend` and run through Gradle's standard `test` task. Controller and service integration tests are organized by package, with a separate package for named business flows.

| Type | Package | Naming | Scope |
| --- | --- | --- | --- |
| Controller tests | `controller` | `<ControllerName>Test` | HTTP contracts, validation, authentication, and error mapping |
| Service integration tests | `service` | `<ServiceName>IT` | Services with the database and transaction boundaries |
| Business flows | `flow` | `<FlowName>FlowIT` | Scenarios spanning services or endpoints |
| Isolated unit tests | Subject's package | `<SubjectName>Test` | Mock-only service tests and other isolated logic |

Use the actual production class name: `AuthControllerTest` tests `AuthController`, and `AuthServiceIT` tests `AuthService`. Group related endpoint cases inside that controller's class using `@Nested`, such as `ListingControllerTest.Search`. Repository, migration, security, and application-context integration checks also use the `IT` suffix in their respective packages.

A flow gets its own class named for the behavior, such as `SignupFlowIT`, `AccountActivationFlowIT`, `PasswordResetFlowIT`, `OfferAcceptanceFlowIT`, or `ChatMessagingFlowIT`. Put these in the `flow` package. Tests for one service belong in its service integration class; scenarios spanning services or endpoints belong in the corresponding flow class.

Mock-only service tests such as `S3AssetsServiceTest` use the `Test` suffix. Controller tests still use a real Spring context and H2; the organization describes what they verify rather than requiring every test to be isolated.

## Running tests

```bash
# All controller, unit, service integration, and flow tests
./gradlew test

# All tests, coverage, SpotBugs, and formatting checks
./gradlew check

# A single controller, service, or flow
./gradlew test --tests '*AuthControllerTest'
./gradlew test --tests '*AuthServiceIT'
./gradlew test --tests '*PasswordResetFlowIT'
```

JUnit XML and HTML reports are under `build/test-results/test` and `build/reports/tests/test`. JaCoCo runs after the test task and produces coverage under `build/reports/jacoco/test`.

CI runs the standard test task, JaCoCo coverage, Spotless, and SpotBugs for production and test code.

## Base classes and fixtures

### Controller tests

Extend `AbstractControllerTest` from `src/test/java/org/app/vestiobackend/controller`. It provides `mockMvc`, `objectMapper`, database cleanup, a default user and catalog data, and mocked Cognito and event publishing clients.

Use `authenticatedUser(testUser)` to authenticate a request with the user's UUID:

```java
class MyControllerTest extends AbstractControllerTest {
    @Test
    void returnsTheCurrentProfile() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .with(authenticatedUser(testUser)))
                .andExpect(status().isOk());
    }
}
```

`AbstractOfferControllerTest` adds shared offer fixtures for `OfferControllerTest`, `UserOfferControllerTest`, and the HTTP offer lifecycle flow.

### Service integration tests and flows

Extend `AbstractServiceIT` from `src/test/java/org/app/vestiobackend/service`. It uses `@SpringBootTest` and the `test` profile, cleans the database before and after each test, and supplies `createTestUser`, `createTestListing`, and catalog fixtures. `setupSecurityContext(testUser)` or `setupSecurityContext(userId)` authenticates service calls with the user UUID.

```java
class MyServiceIT extends AbstractServiceIT {
    @Autowired
    private MyService service;

    @Test
    void performsTheOperation() {
        service.doSomething();
    }
}
```

Use transactions where appropriate for the behavior. Tests verifying commit, rollback, post-commit publishing, or persisted failed PIN attempts must allow the service's actual transaction boundaries to execute rather than wrapping the scenario in a test transaction.

## Database and external systems

Tests use H2 configuration in `src/test/resources/application.yaml`. Liquibase applies the real migrations during application startup. Cognito and external event publishing are mocked by the base classes; tests needing different responses can stub their inherited clients.

## Local application

The `local` profile uses dev PostgreSQL and Cognito. Liquibase applies pending migrations, then the application provisions default dev accounts. Startup skips SQL seeding.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```
