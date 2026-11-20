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

For AppSync publishing, only the `local` profile imports Parameter Store values
from `/DEV/` in `ca-central-1` (or `AWS_REGION`). The local YAML enables
Parameter Store and imports the path, exposing `APPSYNC_EVENTS_HTTP_ENDPOINT` and
`APPSYNC_EVENTS_REGION` as Spring properties using your
default AWS credentials or `AWS_PROFILE`. Your local identity needs
`ssm:GetParametersByPath` for that path and `appsync:EventPublish` for the dev
`chat` and `notifications` namespaces. Dev/prod continue to receive
`APPSYNC_EVENTS_HTTP_ENDPOINT` and `APPSYNC_EVENTS_REGION` as environment variables.
`EventsConfig` injects the resolved endpoint into the singleton publisher.
Pass the user's ID to `dispatchEvent` and handle its returned
`CompletableFuture` to observe publication failures.

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

### Notification delivery

Listing creation saves a `listing-created` notification in the listing transaction.
After commit, the backend sends it through the shared AppSync publisher to
`/notifications/{userId}`. Lambda saves and sends `listing-published` once all
current image renditions exist and the listing becomes `AVAILABLE`.

Both applications use their own notification repository and the same common
entity, enum, event envelope and publisher. Rollbacks never send events. There is
no outbox or automatic live-delivery replay. A failed AppSync send is logged and
the saved notification remains available through the inbox endpoints. Lambda
waits for the publish attempt before completing its invocation.

Deploy backend migration `016-simplify-notifications` before deploying Lambda.
It drops the old outbox, renames `params` to `payload`, adds nullable `listing_id`,
and migrates the old pending/ready types and listing correlations. Historical
changesets remain intact so existing databases can upgrade.

Notification and event IDs use the same generated UUID. `user_id` references
the Cognito subject UUID stored directly in `users.id`; AppSync routes using that same ID.

### Notification inbox API

All notification endpoints require authentication. The user ID is resolved from
the current session; callers cannot select another user.

| Method | Endpoint | Response |
| --- | --- | --- |
| GET | `/api/notifications?page=0&size=20&unreadOnly=false` | `{items, page, size, totalElements, totalPages, unreadCount}` |
| GET | `/api/notifications/unread-count` | `{unreadCount}` |
| PUT | `/api/notifications/{id}/read` | Updated notification; 404 for missing or another user's ID |
| PUT | `/api/notifications/read-all` | 204; marks the current user's unread notifications read |

History sorts newest first, with UUID as the tie breaker. `page` is zero based;
`size` accepts 1–100. `unreadCount` always counts all unread notifications, including
those outside the selected page. Read operations are idempotent. Notifications
contain `id`, `userId`, `type`, `payload`, optional `listingId`, `readAt`, and `createdAt`, matching
the notification payload in AppSync events. Database timestamps use the existing
local date-time format. AppSync continues publishing new notifications after commit.

### User identity

`users.id` is the UUID from the Cognito `sub` claim. Signup and local account
provisioning use this ID directly; JWT requests look up users by primary key.
There is no separate Cognito-subject field or login-time identity linking.
Listings, offers, favorites, preferences, and notifications reference this same ID.

Fresh schemas do not create `cognito_sub`. Changeset `012-use-cognito-user-ids`
upgrades existing PostgreSQL databases: it atomically remaps user IDs and all
referencing foreign keys, preserves their constraints, then removes the legacy
column. It halts before changing data if an existing user has a missing, invalid,
or duplicate Cognito UUID subject. Such accounts must be linked to their actual
Cognito subjects before upgrading. No database reset is required.

To run the PostgreSQL migration tests against a disposable database, set
`USER_ID_MIGRATION_JDBC_URL` (and optionally `USER_ID_MIGRATION_DB_USER` and
`USER_ID_MIGRATION_DB_PASSWORD`) and run `./gradlew test --tests '*UserIdentityMigrationTest'`.
The tests create and remove their own isolated schemas.

Changeset `013-rename-notification-user-id` renames the legacy notification owner
column to `user_id`, preserving its data, indexes, and foreign key. Notification
JSON uses `userId` in both the HTTP API and AppSync payload. Publisher channels
are `/<namespace>/<userId>`; clients read this ID from the JWT `sub` claim.

### Shared notification contract

Notifications contain a user ID, `NotificationType`, arbitrary JSON `payload`,
optional `listingId`, ID and read/creation timestamps. JSON notification types
use `listing-created` and `listing-published`; database enum values use uppercase
names. Generic notifications do not need a listing.

The shared `Event` envelope is `{id, type, payload, occurredAt}`. Its `EventType`
is `notification` or `chat`; only notification payloads are implemented now.
For notification events, `payload` is the saved notification and the event ID
matches its ID. The canonical fixture is shared by common, backend and Lambda.

### Shared events library

Notifications, enums, realtime event payloads and the AppSync publisher use
`com.vestio:vestio-common:0.4.0` from GitHub Packages. Spring repositories and
transaction listeners remain in this application. Shared JPA entities are included
in the application's entity scan.

For local builds, set the following in `~/.gradle/gradle.properties` (never commit credentials):

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_TOKEN_WITH_READ_PACKAGES
```

Alternatively, set `GITHUB_ACTOR` and `PACKAGES_READ_TOKEN` (or `GITHUB_TOKEN`).
CI passes `PACKAGES_READ_TOKEN`, falling back to `INFRA_GITHUB_TOKEN`. Configure
a token with `read:packages` and access to the `yola-org/vestio-common` package.
The reusable Gradle workflows in `shared-workflows` must include package authentication.

### Listing visibility

Public `GET /api/listings`, `/api/listings/search`, and `/api/listings/{id}`
return only `AVAILABLE` listings. A non-available detail ID returns 404. Favorite
results also exclude listings that are no longer available.

Authenticated `GET /api/listings/mine` returns the current user's listings across
all statuses. `GET /api/listings/pending` returns their `AWAITING_ASSETS` and
`PENDING` listings. Both accept `page` (zero based) and `size` (1–100, default 12)
and sort newest first. They never return another user's listings.

### Offer notifications

Creating an offer saves one `offer-sent` notification for the listing owner in
the same transaction. Its shared `OfferSentPayload` contains `offerId`, numeric
`amount`, `senderId`, `senderName` and `listingTitle`. `listingId` is a notification
field. Names and prices come from persisted backend entities; clients cannot
choose the recipient or sender name. Rejected and duplicate offers do not notify.
The shared AppSync publisher sends the notification after commit; live-send
failures leave the offer and inbox entry saved. Rolled-back offers never publish.

Merge the common feature first and publish `vestio-common:0.4.0` before merging
this backend branch. Its local checks were run against the matching common source.
