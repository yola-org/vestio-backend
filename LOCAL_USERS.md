# Local test users

The `local` Spring profile provisions `admin@example.com`, `admin2@example.com`, `admin3@example.com`, `admin4@example.com`, and `admin5@example.com` in dev Cognito and PostgreSQL. New accounts use `!Password123`. Existing confirmed accounts keep their passwords. Sign in with the full email address.

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

The EC2 launch template reads the environment's Secrets Manager entries and injects these variables.

The local profile imports the Cognito settings from the `DEV/COGNITO/*` Secrets Manager entries using your AWS credentials. Provisioning requires `cognito-idp:AdminGetUser`, `cognito-idp:AdminCreateUser`, and `cognito-idp:AdminSetUserPassword` on the dev pool.

Use the email address to sign in. Cognito issues the access token with its internal username, and the API uses the token’s `sub` to find the local user.
