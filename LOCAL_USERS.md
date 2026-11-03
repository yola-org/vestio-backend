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

## Production environment

The default (non-local) profile expects database and Cognito settings from the environment. Configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `AWS_REGION`, `COGNITO_ISSUER_URI`, `COGNITO_JWK_SET_URI`, `COGNITO_CLIENT_ID`, `COGNITO_CLIENT_SECRET`, and `COGNITO_USER_POOL_ID` for the deployment environment. No production database credentials or Cognito identifiers/secrets are stored in the application YAML files. Keep secrets in environment-specific secret storage and never commit them.