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

The local profile imports `DEV/PRIMARY_JDBC_URL` and the `DEV/COGNITO/*` secrets using the AWS default credentials chain. The PostgreSQL URL includes the database username and password. Dev and prod deployments receive their environment's values from the EC2 launch template.

### AWS assets and events

S3 assets use the `S3_ASSETS_BUCKET` environment variable. Objects are stored below the `users/` and `listings/` prefixes. SQS queue URLs are supplied through `SQS_USER_EVENTS_QUEUE_URL` and `SQS_LISTING_EVENTS_QUEUE_URL`. All AWS clients use the standard AWS credentials provider chain and `AWS_REGION` (defaulting to `ca-central-1`).

### API Documentation

Once the application is running, you can access the Swagger UI at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Local Development Profile

The local profile connects to dev PostgreSQL and Cognito. Liquibase applies pending schema migrations before Hibernate validates the schema. Startup provisions the default dev accounts. See [Local Users](./LOCAL_USERS.md) for sign-in details.

## Documentation Guides

- **[Code Guide](./CODE_GUIDE.md)**: Overview of the project architecture, entities, and coding patterns.
- **[Testing Guide](./TESTING_GUIDE.md)**: Strategy and instructions for writing and running tests.
- **[Local Users](./LOCAL_USERS.md)**: Dev accounts and Cognito configuration for local development.

## Testing

Run the test suite using Gradle:

```bash
./gradlew test
```

Tests use an in-memory H2 database and a dedicated `test` profile.

Run `./gradlew check` to run the tests, SpotBugs analysis, and Spotless Java
formatting checks. Use `./gradlew spotlessApply` to trim trailing whitespace
and add a final newline to Java files. SpotBugs reports findings without failing
the build. JaCoCo runs with the test suite and has no minimum coverage threshold.

- JaCoCo [HTML](build/reports/jacoco/test/html/index.html) and
  [XML](build/reports/jacoco/test/jacocoTestReport.xml) reports
- SpotBugs [application](build/reports/spotbugs/main.html) and
  [test](build/reports/spotbugs/test.html) reports

GitHub Actions and GitLab CI both run `check`. GitLab CI uploads the reports
with the test job.

## GitHub Actions

`.github/workflows/ci.yml` follows the image optimizer pipeline and calls
`yola-org/shared-workflows` for version calculation, Java 25 tests, packaging,
and Docker publishing. Pull requests to `main` run verification; pushes to
`main` also publish `vestio/backend` to ECR and create the release tag after
publishing succeeds. Manual runs perform verification only.

Configure repository secrets `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, and
`INFRA_GITHUB_TOKEN` (release tagging), plus repository variables `AWS_REGION`
and `ECR_REGISTRY`, as described in the shared workflows setup guide. The
shared workflows repository must allow this repository to call its workflows.

Packaging uses `bootJar` so the Docker build receives only the executable
Spring Boot JAR. The existing GitLab pipeline remains available separately.
There is no automatic infrastructure bump: `aws-infra` currently has no backend
deployment or `backend_image_tag` in its `dev` environment.
