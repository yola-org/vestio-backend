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

The default (non-local) profile loads its Cognito configuration from AWS Secrets Manager at startup (`vestio/cognito-client-secret` for `prod`, `vestio-dev/cognito-client-secret` otherwise), through the default AWS credentials chain (instance profile on EC2; `AWS_PROFILE` locally). The secret holds the full Cognito JSON config. No production Cognito credentials are stored in the YAML files or in the environment. Database credentials still come from the datasource environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`). Local Cognito test sync (seeded users) additionally requires AWS credentials with `cognito-idp:AdminCreateUser` and `cognito-idp:AdminSetUserPassword` on the `vestio-dev-users` pool.