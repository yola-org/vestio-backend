# Vestio Backend

Vestio Backend is a Spring Boot application providing the RESTful API for the Vestio platform. It handles user authentication, profile management, and item listings.

## Account confirmation and welcome email (Resend)

Successful signups publish a verification-PIN event. After the database transaction
commits, `EmailService` sends a six-digit confirmation PIN using Resend. After
confirmation, it sends a personalized French welcome email using the Resend
Java SDK. Both emails use the shared HTML layout at
`src/main/resources/templates/email/layout.html`, with body fragments in
`welcome.html` and `verification.html` in the same directory. `EmailTemplate`
renders HTML, subject, and plain text; dynamic values are escaped and inserted
in one pass. Update the layout once to change the branding for every email. It
uses Vestio’s cream, coral, lime, and black palette. A plain-text version is
included. The backend confirms the Cognito account only after validating its own PIN.

Configuration is identical across environments:

| Setting | Purpose | Default |
| --- | --- | --- |
| `RESEND_API_KEY` | Resend credential, supplied through secrets/environment | Empty (delivery skipped with a warning) |
| `EMAIL_FROM` | Sender on your verified Resend domain | `Vestio <onboarding@resend.dev>` (development only) |
| `EMAIL_SITE_URL` | Website destination in welcome emails; absolute HTTPS URL | `https://vestio.ma` |
| `EMAIL_ENABLED` | Enable confirmation and welcome email delivery | `true` |

The local profile already imports `DEV/RESEND_API_KEY` from AWS Secrets Manager.
For deployed environments, inject the matching secret as `RESEND_API_KEY` and set
`EMAIL_FROM` to an address on your verified sending domain. Creating the secret
alone does not inject it into the running application. The Resend onboarding
sender can only deliver to the account owner's address; production signups need
a verified sender. See [Resend’s Java setup](https://resend.com/docs/send-with-java).
Set `EMAIL_ENABLED=false` in environments where signup must not send real mail;
tests disable it and exercise delivery with a mocked Resend client.

Delivery uses per-PIN confirmation and per-account welcome idempotency keys and a five-second request timeout.
Failures log the account ID and exception type without credentials or email content,
and do not fail signup. Sending is synchronous after commit and may add up to the
request timeout to signup latency. This is best-effort delivery: there is no durable
queue or automatic replay after a process crash or provider failure.

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

All tests live under `src/test/java` and run with `test`, including controller tests, service integration tests, and named business flows. They use the shared H2 test configuration. See [TESTING_GUIDE.md](./TESTING_GUIDE.md) for naming and examples.

Run `./gradlew check` to run the tests, SpotBugs analysis, and Spotless Java
formatting checks. Use `./gradlew spotlessApply` to trim trailing whitespace
and add a final newline to Java files. SpotBugs reports findings without failing
the build. JaCoCo runs with the test suite and has no minimum coverage threshold.

- JaCoCo [HTML](build/reports/jacoco/test/html/index.html) and
  [XML](build/reports/jacoco/test/jacocoTestReport.xml) reports
- SpotBugs [application](build/reports/spotbugs/main.html) and
  [test](build/reports/spotbugs/test.html) reports

GitHub Actions runs all tests and the verification tasks.

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
The backend service saves and flushes the notification, then directly calls the
shared AppSync publisher and waits for completion on `/notifications/{userId}`.
Lambda uses the same direct-send flow for `listing-published` after image processing.
There are no internal notification events, dispatchers, listeners or outbox.

An AppSync failure propagates and rolls back the current notification and
listing/offer transaction. The send occurs before commit, so a later database
commit failure can leave a live event whose transaction did not commit.

The initial backend changelogs create the current notification schema directly,
including `user_id`, JSON `payload`, nullable `listing_id`, and UTC timestamps.
Initialize the backend database before starting Lambda.

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
the notification payload in AppSync events. Database timestamps use `TIMESTAMP WITH TIME ZONE`
and serialize as ISO 8601 UTC values ending in `Z`. The service sends notifications directly after saving and flushing them.

### User identity

`users.id` is the UUID from the Cognito `sub` claim. Signup and local account
provisioning use this ID directly; JWT requests look up users by primary key.
There is no separate Cognito-subject field or login-time identity linking.
Listings, offers, favorites, preferences, and notifications reference this same ID.

The initial schema uses Cognito subjects directly as `users.id` and references
them through `user_id`. Notification JSON uses `userId` in both the HTTP API and
AppSync payload. Publisher channels are `/<namespace>/<userId>`; clients read
this ID from the JWT `sub` claim.

### Shared notification contract

Notifications contain a user ID, `NotificationType`, arbitrary JSON `payload`,
optional `listingId`, ID and read/creation timestamps. JSON notification types
use `listing-created` and `listing-published`; database enum values use uppercase
names. Generic notifications do not need a listing.

The shared `Event` envelope is `{id, type, payload, occurredAt}`. Its `EventType`
is `notification` or `chat`. Chat events use the concrete `ChatEvent.Payload` message contract.
For notification events, `payload` is the saved notification and the event ID
matches its ID. The canonical fixture is shared by common, backend and Lambda.

### Shared events library

Notifications, enums, realtime event payloads and the AppSync publisher use
`com.vestio:vestio-common:0.7.0` from GitHub Packages. Spring repositories and
notification services remain in this application. Shared JPA entities are included
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

### Listing search filters

`GET /api/listings/search` accepts multiple `colors`, `cityIds`, `sizeIds`, `conditions` (item states),
and `contactTypes`, using repeated parameters or comma-separated values. For example:
`/api/listings/search?colors=Blue,Red&conditions=GOOD,VERY_GOOD&contactTypes=DELIVERY&contactTypes=PARCEL&sizeIds=<uuid1>,<uuid2>`.
A listing must match at least one value within each selected filter, and all
selected filters must match. Color matching ignores case and surrounding whitespace.
Empty lists impose no restriction. Results remain
unique when multiple colors, cities, sizes or contact types match, including across pages.

### Offer notifications

Creating an offer saves one `offer-sent` notification for the listing owner in
the same transaction. Its shared `OfferSentPayload` contains `offerId`, numeric
`amount`, `senderId`, `senderName` and `listingTitle`. `listingId` is a notification
field. Names and prices come from persisted backend entities; clients cannot
choose the recipient or sender name. Rejected and duplicate offers do not notify.
The service invokes the shared AppSync publisher directly after save/flush.
A failed send rolls back both the offer and its notification, allowing a retry.

Merge the common feature first and publish `vestio-common:0.7.0` before merging
this backend branch. Its local checks were run against the matching common source.

### Typed notification payloads

`NotificationService.notifyUser` requires `NotificationPayload`, never an arbitrary
map. Shared `OfferSentPayload` and `ListingNotificationPayload` implement this
interface. Its `toMap(ObjectMapper)` uses the application's injected Jackson mapper,
so payload classes have no manual field-to-map implementations. The persisted
notification and frontend JSON shapes stay unchanged.

## UTC timestamps

All persisted audit timestamps and notification/event timestamps use `Instant`. APIs and AppSync events emit ISO 8601 UTC timestamps ending in `Z`; the frontend converts them to the browser timezone for display. Calendar dates must remain date-only. Hibernate JDBC and container JVM defaults use UTC.

Release `vestio-common:0.7.0` before building these consumers. The initial Liquibase changelogs create all audit timestamp columns as `TIMESTAMP WITH TIME ZONE` from the start; no legacy timezone conversion or migration setting is required. Initialize the backend database before starting the image optimizer Lambda.

## Chats and messages

`Chat` links an offer and its listing to the owner and buyer. Each chat requires a unique `offer_id`; a composite foreign key enforces that the offer belongs to the same listing and buyer. Each listing/buyer pair has one chat; the owner must match the listing's seller and must differ from the buyer. `Message` links a chat to its sender and nonblank text content. System messages use `system = true`, a `systemType` enum and JSON `params`; sender and content may be omitted. The initial type is `OFFER_ACCEPTED` (`offer-accepted` in JSON), requiring a positive numeric `amount`, a three-letter uppercase `currency`, and string `ownerName`, `buyerName` and `listingTitle`. Regular messages require a participant sender and text, with no system metadata. JPA validates these rules and that any supplied sender is one of the chat's two participants. Both entities use generated UUIDs and UTC `Instant` creation timestamps. Changelog 011 creates chats and their ownership constraints; changelog 012 creates messages and an index for chronological chat history. Deleting an offer, listing or participant deletes the related chats and messages. The offer/listing foreign keys use `ON DELETE CASCADE` and `ON UPDATE CASCADE`, so changing referenced IDs updates chat references. Listing collections and offer listing references cascade too, allowing listing ID changes without broken references. Cascades apply to foreign-key IDs, not snapshots in message JSON or edits to unrelated parent fields. The listing-ID cascade regression runs on PostgreSQL because H2 rejects the overlapping composite cascade paths.

### Accept an offer

`PUT /api/listings/{listingId}/offers/{offerId}/accept` requires authentication as the listing owner and has no request body. It returns the accepted `OfferDto` (HTTP 200). Acceptance creates or reuses the listing/buyer chat, saves an `offer-accepted` system message with no sender or rendered text, and sends an `offer-accepted` notification to the buyer through the existing notification publisher. The notification and system message share `offerId`, `chatId`, numeric `amount`, `currency`, `ownerName`, `buyerName` and `listingTitle`. All names and offer details come from persisted records. `app.currency` defaults to `MAD` and can be overridden with `APP_CURRENCY`.

The listing is locked during acceptance so repeated or concurrent requests do not duplicate the chat, system message or notification. Requests from the buyer or another user receive HTTP 401; mismatched listing/offer IDs receive HTTP 404. Notification publication failures roll back the offer status and all new database records, allowing a retry. Notification publication uses the existing send-before-commit flow: an event can still be delivered before a later database commit failure. There is no outbox. Publish `vestio-common:0.7.0` before building this backend update.

`PUT /api/listings/{listingId}/offers/{offerId}/refuse` lets the listing owner mark a pending offer `REFUSED`; it returns the updated `OfferDto` (HTTP 200). Repeating the same decision is safe. Accepted and refused offers cannot switch to the other decision, or be edited/cancelled by the buyer (`ERR_OFFER_NOT_PENDING`). Both decisions take the listing lock, so concurrent Accept/Refuse requests produce one final outcome. The frontend shows Accept and Refuse only for pending received offers and displays the resulting status after refreshing.

### Chat API

All `/api/chats` endpoints require authentication. Only the owner and buyer can access their chat; missing chats and nonparticipant requests return HTTP 404. Chat creation remains part of offer acceptance. Responses use DTOs with participant IDs and display names, without user emails or JPA entity graphs.

| Method | Endpoint | Response |
| --- | --- | --- |
| GET | `/api/chats?page=0&size=20` | Paginated chats for the authenticated user |
| GET | `/api/chats/{id}` | Chat, listing, offer and participant details |
| GET | `/api/chats/{id}/messages?page=0&size=20` | Paginated regular and system messages |
| POST | `/api/chats/{id}/messages` | New regular message (HTTP 201) |

Pages contain `items`, `page`, `size`, `totalElements` and `totalPages`. Chat DTOs include a nullable `listingCoverAssetKey` (the first image in the listing’s ordered asset keys), retrieved alongside the summary without loading the image collection per chat. Image-less listings return null; replacing or reordering listing images updates the cover on the next read. They also include an embedded `lastMessage` (`MessageDto`, or null when empty), including system messages. Chats sort by the latest message timestamp descending, falling back to chat creation time when empty, then by chat ID descending. Message history sorts by message `createdAt` and then message ID descending. Equal message timestamps select the greatest message ID as the preview. The database query retrieves each chat and its preview together before pagination. Page numbers start at zero; size is 1–100. Send requests contain only `{ "content": "Hello" }`, with nonblank text up to 4,000 characters. Unknown fields, including sender and system metadata, are ignored. The service derives the sender from authentication, verifies membership and locks the chat while saving. User messages always have `system: false`, `systemType: null` and `params: null`. All timestamps are ISO 8601 UTC. Regular messages and offer-accepted system messages publish a typed `ChatEvent` to `/chat/{ownerId}` and `/chat/{buyerId}` after the database transaction commits. Event ID equals message ID; the payload has the same fields as `MessageDto`, including the wire value of `systemType`. Rolled-back messages never publish. Chat transport failures are logged without failing an already committed send, and each participant is attempted independently. There is no durable chat delivery queue; clients reload persisted history on reconnect or focus.

### Profile images

Authenticated `POST /api/profile/image` accepts one multipart `image` (JPEG, PNG,
or WebP, up to the configured 10 MB image limit). The backend checks the file
extension, MIME type and signature, then writes the bytes directly to the
existing assets bucket under `users/{userId}/profile/{randomUuid}.{extension}`.
It stores and returns `profileImageKey` as `{userId}/profile/{randomUuid}.{extension}`
without the `users/` prefix. The frontend adds the asset prefix when building URLs. No SQS message or optimizer
job is generated. The user ID comes from authentication; normal profile updates
cannot assign image keys.

A committed replacement removes the previous image. A rolled-back update removes
the newly uploaded image and retains the previous one. Cleanup failures are
logged without failing an already committed update. The storage bucket policy
must allow public reads of these specific profile-image keys for avatar display.
The users domain changelog includes the profile-image column; new image keys are stored
in relative form and require no legacy data rewrite. For release, deploy the prepared storage policy before enabling public avatar URLs.


### Database changelog organization

`src/main/resources/db/changelog/db.changelog-master.yaml` includes domain schemas in dependency order:
catalog → users and preferences → listings, collections, favorites and search → offers → messaging → notifications.
Domain files live under `db/changelog/domains/`. Listing search remains a separate PostgreSQL-only changeset
inside the listings domain. Future changes to an executed schema should use new changesets.


### Listing read responses

`GET /api/listings`, `/api/listings/search`, `/api/listings/favorites`, `/api/listings/pending`,
and `/api/listings/mine` return our `PaginatedResult<ListingSummaryDto>` DTO using
`{items, page, size, totalElements, totalPages}`. All accept zero-based `page`
(default 0) and `size` (default 12, range 1–100). `/mine` also accepts an optional
`status`, applied before pagination, so the profile can request only its owner's
available listings. `/favorites` accepts an optional comma-separated `listingIds`
filter (up to 100 UUIDs) for checking saved state only for visible cards.

`GET /api/offers/sent` and `/api/offers/received` use the same page envelope and
parameters, scoped to the authenticated user. Offers sort by `createdAt` descending,
then ID descending. Empty and out-of-range pages still report the filtered total;
the frontend returns to the last available page after removals.
Summaries include card fields, seller display fields, category name, a description preview capped
at 160 characters, colors, city/size names, and one nullable `coverAssetKey` (asset position zero).
They omit the full description, asset list, contact types, quantity, and category/city/size IDs.
`GET /api/listings/{id}` and listing mutation responses continue returning the full `ListingDto`.

Summary queries use scalar DTO projections and bounded scalar collection queries instead of loading
listing entities. A nonempty page uses five SQL queries including the filtered
count, excluding optional category-tree expansion. Authenticated lists also resolve
the current user. Collection enrichment is limited to the requested page.

### Email confirmation API and schema

`POST /api/auth/signup` creates an unverified user and emails a PIN after commit.
`POST /api/auth/confirm-email` accepts `{ "email": "...", "pin": "123456" }`.
`POST /api/auth/resend-confirmation` accepts `{ "email": "..." }`.
PINs expire after 10 minutes, allow five guesses, and can be resent after 60 seconds.
Only the latest PIN is accepted. PINs are stored as keyed hashes and all rows for
that user are deleted on activation. Confirmation also confirms the Cognito account
and marks its email verified; login is blocked until verification completes.
The profile's `emailVerified` field is read-only, and profile updates cannot change
the login email. Delivery failures are logged without PINs; users can request a resend.

The original `db/changelog/domains/users.yaml` was rewritten, as requested, with
`users.email_verified` and `pins`. This is a fresh-schema baseline,
not an upgrade migration: databases that already applied `create-users-domain`
need a separate migration (or an intentional reset) before this version runs.
Cognito automatic verification and email recovery are disabled; all application
email is sent by the backend through Resend.
Deploy the matching Cognito configuration and scoped IAM permissions from aws-infra
before enabling this flow. Configure `RESEND_API_KEY`, `EMAIL_FROM`, and `EMAIL_ENABLED`
for actual mail delivery. Terraform changes are prepared only, not applied.

### Password reset and shared PIN purposes

`POST /api/auth/forgot-password` accepts `{ "email": "..." }`. Unknown addresses
return HTTP 400 with `ERR_EMAIL_NOT_FOUND` and "Your email wasn't found." The web
request/reset forms translate this code and display it inline. Existing unverified
or throttled accounts retain the generic success response. A verified account
receives a reset PIN through Resend after the request commits.
`POST /api/auth/reset-password` accepts `{ "email": "...", "pin": "123456",
"newPassword": "..." }`. Passwords require 12–256 characters, uppercase,
lowercase, a digit, and a symbol, with no whitespace; Cognito also enforces its
configured policy and any password history.

The original users changelog now defines one `pins` table. `purpose` is stored as
`ACCOUNT_ACTIVATION` or `PASSWORD_RESET`, and the lookup index covers user,
purpose, and creation time. Both flows use keyed PIN hashes, 10-minute expiry,
and a five-attempt limit. Only the latest PIN for the requested purpose works.
A successful operation deletes that user's PINs for its own purpose. Password
reset requests for existing accounts are silently throttled for one minute. PIN and password validation values are redacted from errors.

Reset and password-changed emails use the common HTML layout with plain-text
fallbacks. The backend calls Cognito `AdminSetUserPassword` with a permanent
password and then `AdminUserGlobalSignOut`; it never invokes Cognito's email
recovery API. `users.password_changed_at` also fences old JWT sessions on protected
HTTP endpoints, using `auth_time` (or `iat` when absent) so refreshed old sessions
cannot regain access. New sessions created in the same timestamp second may need
to sign in again in the next second because Cognito timestamps have second precision.
External real-time services must independently enforce token expiry/revocation.

As requested, this updates the existing schema baseline rather than adding an
upgrade migration. An existing database needs an intentional migration or reset
for the table rename, purpose column, and password-change timestamp. Apply the
matching scoped IAM permissions in aws-infra before deploying the feature.
