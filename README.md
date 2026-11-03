# Vestio Backend

Vestio Backend is a Spring Boot application providing the RESTful API for the Vestio platform. It handles user authentication, profile management, and item listings.

## Technologies

- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Security & OAuth2 Resource Server**
- **AWS Cognito** (Authentication)
- **H2 / PostgreSQL** (Database)
- **Liquibase** (Database Migrations)
- **MapStruct** (Object Mapping)
- **Swagger/OpenAPI** (API Documentation)

## Getting Started

### Prerequisites

- JDK 17
- Gradle (included via `./gradlew`)

### Running the Application

To run the application locally with the default `local` profile:

```bash
./gradlew bootRun
```

The `local` profile is configured in `src/main/resources/application-local.yaml` and is activated by default in the `bootRun` task.

### API Documentation

Once the application is running, you can access the Swagger UI at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Local Development Profile

The `local` profile is designed for easy development and manual testing.

### Key Features:
- **File-based Database**: Uses H2 database located at `./data/vestiodb`.
- **Cognito Sync**: Automatically ensures local test users exist in the configured AWS Cognito User Pool with the default password `Password123!`.
- **Mock Authentication**: Supports a mock token for easier API testing. See [CODE_GUIDE.md](./CODE_GUIDE.md#security--authentication) for details.
- **Seeded Data**: Automatically populates the database with categories, cities, and test users/listings.

### Local Test Users
Several test accounts are available for local development. See [LOCAL_USERS.md](./LOCAL_USERS.md) for the list of accounts and their credentials.

## Documentation Guides

- **[Code Guide](./CODE_GUIDE.md)**: Overview of the project architecture, entities, and coding patterns.
- **[Testing Guide](./TESTING_GUIDE.md)**: Strategy and instructions for writing and running tests.
- **[Local Users](./LOCAL_USERS.md)**: Details about seeded test data and Cognito integration for local development.

## Testing

Run the test suite using Gradle:

```bash
./gradlew test
```

Tests use an in-memory H2 database and a dedicated `test` profile.
