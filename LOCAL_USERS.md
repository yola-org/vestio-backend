# Local test users

The `local` Spring profile uses a file-based H2 database and syncs these users to AWS Cognito. When the application starts with the `local` profile, it ensures these users exist in Cognito with the default password `Password123!`.

| Email | Name | Test data |
| --- | --- | --- |
| `local@example.com` | Local User | Multiple listings |
| `seller1@example.com` | Amina Sellers | One listing |
| `seller2@example.com` | Youssef Buyers | No listings |
| `expert@example.com` | Sara Expert | Multiple listings |
| `casual@example.com` | Mehdi Casual | Mixed listings |

Run the backend with the `local` profile (the Gradle `bootRun` task activates it by default) to use these seeded accounts. The database is recreated when the application stops. Do not use these test credentials outside local development.

## Cognito configuration

Deployed environments (prod and dev) read unprefixed environment variables:

| Variable | Purpose |
| --- | --- |
| `COGNITO_REGION` | AWS region of the Cognito user pool |
| `COGNITO_CLIENT_ID` | Cognito app client ID |
| `COGNITO_CLIENT_SECRET` | Cognito app client secret |
| `COGNITO_USER_POOL_ID` | Cognito user pool ID |
| `COGNITO_ISSUER_URI` | OAuth2 JWT issuer URI |
| `COGNITO_JWK_SET_URI` | OAuth2 JWK set URI |

The EC2 launch template fetches the `PROD_*` / `DEV_*` Secrets Manager entries, strips the prefix, and injects these variables.

Local development uses the same unprefixed names. `LocalCognitoSecretsEnvironmentPostProcessor` (active only for the `local` profile) resolves any missing value from the matching `DEV_*` secret with your default AWS credentials:

`COGNITO_CLIENT_ID` ← `DEV_COGNITO_CLIENT_ID`, `COGNITO_CLIENT_SECRET` ← `DEV_COGNITO_CLIENT_SECRET`, `COGNITO_USER_POOL_ID` ← `DEV_COGNITO_USER_POOL_ID`, `COGNITO_REGION` ← `DEV_COGNITO_REGION`, `COGNITO_ISSUER_URI` ← `DEV_COGNITO_ISSUER_URI`, `COGNITO_JWK_SET_URI` ← `DEV_COGNITO_JWK_SET_URI`.

Anything you export yourself takes precedence, and the fetch is skipped when all six are provided.

Database credentials still come from the datasource environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`). Local Cognito test sync (seeded users) additionally requires AWS credentials with `cognito-idp:AdminCreateUser` and `cognito-idp:AdminSetUserPassword` on the `vestio-dev-users` pool.