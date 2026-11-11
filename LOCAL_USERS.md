# Local test users

The `local` Spring profile seeds an in-memory H2 database and syncs these test accounts to the dev Cognito pool. The initializer saves each account’s Cognito `sub` on its local user row. New accounts get the default password `Password123!`. Existing confirmed accounts keep their current password.

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

The local profile imports the Cognito settings from the `DEV/COGNITO/*` Secrets Manager entries using your AWS credentials. The seed sync also requires `cognito-idp:AdminCreateUser`, `cognito-idp:AdminGetUser`, and `cognito-idp:AdminSetUserPassword` on the dev user pool.

Use the email address to sign in. Cognito issues the access token with its internal username, and the API uses the token’s `sub` to find the local user.
