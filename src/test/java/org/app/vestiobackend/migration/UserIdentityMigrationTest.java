package org.app.vestiobackend.migration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "USER_ID_MIGRATION_JDBC_URL", matches = ".+")
class UserIdentityMigrationTest {
    private static final String CURRENT = "db/changelog/db.changelog-master.yaml";
    private static final String LEGACY = "db/changelog/legacy-user-schema.yaml";
    private static final String FIRST = "00000000-0000-4000-8000-000000000001";
    private static final String SECOND = "00000000-0000-4000-8000-000000000002";

    @Test
    void freshSchemaHasNoSeparateCognitoSubject() throws Exception {
        inSchema((connection, schema) -> {
            migrate(connection, schema, CURRENT);
            assertThat(columnExists(connection, schema)).isFalse();
            String userId = UUID.randomUUID().toString();
            String notificationId = UUID.randomUUID().toString();
            execute(connection, "INSERT INTO users(id,email,first_name,last_name) VALUES ('" + userId + "','generic@example.com','Generic','User')");
            execute(connection, "INSERT INTO notifications(id,user_id,type,payload) VALUES ('" + notificationId + "','" + userId + "','OFFER_CREATED','{}')");
            assertThat(scalar(connection, "SELECT listing_id::text FROM notifications")).isNull();
            assertThat(scalar(connection, "SELECT count(*)::text FROM information_schema.tables WHERE table_schema='" + schema + "' AND table_name='notification_outbox'")).isEqualTo("0");
        });
    }

    @Test
    void upgradesNotificationPayloadTypesAndRemovesOutbox() throws Exception {
        inSchema((connection, schema) -> {
            migrate(connection, schema, "db/changelog/pre-notification-event-model.yaml");
            String userId = UUID.randomUUID().toString();
            String listingId = UUID.randomUUID().toString();
            execute(connection, "INSERT INTO users(id,email,first_name,last_name) VALUES ('" + userId + "','upgrade@example.com','Upgrade','User')");
            execute(connection, "INSERT INTO notifications(id,user_id,type,params) VALUES (gen_random_uuid(),'" + userId + "','LISTING_PENDING','{\"listingId\":\"" + listingId + "\",\"listingTitle\":\"Shirt\"}'), (gen_random_uuid(),'" + userId + "','LISTING_READY','{}')");
            migrate(connection, schema, CURRENT);
            migrate(connection, schema, CURRENT);
            assertThat(scalar(connection, "SELECT listing_id::text FROM notifications WHERE type='LISTING_CREATED'")).isEqualTo(listingId);
            assertThat(scalar(connection, "SELECT payload->>'listingTitle' FROM notifications WHERE type='LISTING_CREATED'")).isEqualTo("Shirt");
            assertThat(scalar(connection, "SELECT count(*)::text FROM notifications WHERE jsonb_exists(payload, 'listingId')")).isEqualTo("0");
            assertThat(scalar(connection, "SELECT count(*)::text FROM notifications WHERE type='LISTING_PUBLISHED'")).isEqualTo("1");
            assertThat(scalar(connection, "SELECT count(*)::text FROM information_schema.tables WHERE table_schema='" + schema + "' AND table_name='notification_outbox'")).isEqualTo("0");
        });
    }

    @Test
    void upgradesExistingUsersAndEveryOwnershipReferenceEvenWhenIdsSwap() throws Exception {
        inSchema((connection, schema) -> {
            migrate(connection, schema, LEGACY);
            String category = UUID.randomUUID().toString();
            String listing = UUID.randomUUID().toString();
            execute(connection, "INSERT INTO users(id,email,first_name,last_name,cognito_sub) VALUES ('" + FIRST + "','one@example.com','One','User','" + SECOND + "'), ('" + SECOND + "','two@example.com','Two','User','" + FIRST + "')");
            execute(connection, "INSERT INTO categories(id,name) VALUES ('" + category + "','Shirts')");
            execute(connection, "INSERT INTO listings(id,title,price,condition,status,quantity,user_id,category_id,created_at,updated_at) VALUES ('" + listing + "','Shirt',10,'GOOD','AVAILABLE',1,'" + FIRST + "','" + category + "',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
            execute(connection, "INSERT INTO user_preferences(id,user_id,category_id) VALUES (gen_random_uuid(),'" + FIRST + "','" + category + "')");
            for (String userId : new String[]{FIRST, SECOND}) {
                execute(connection, "INSERT INTO favorites(id,user_id,listing_id) VALUES (gen_random_uuid(),'" + userId + "','" + listing + "')");
                execute(connection, "INSERT INTO offers(id,user_id,listing_id,price,delivery_types,message) VALUES (gen_random_uuid(),'" + userId + "','" + listing + "',9,ARRAY['MEETUP'],'Offer')");
            }
            execute(connection, "INSERT INTO notifications(id,recipient_id,type,params) VALUES (gen_random_uuid(),'" + FIRST + "','LISTING_CREATED','{}')");
            String constraints = scalar(connection, "SELECT string_agg(conname || ':' || pg_get_constraintdef(oid), ';' ORDER BY conname) FROM pg_constraint WHERE contype='f' AND confrelid='users'::regclass AND conname <> 'fk_notification_outbox_user'");

            migrate(connection, schema, CURRENT);
            migrate(connection, schema, CURRENT); // Safe on subsequent startups.

            assertThat(columnExists(connection, schema)).isFalse();
            assertThat(scalar(connection, "SELECT id::text FROM users WHERE email='one@example.com'")).isEqualTo(SECOND);
            assertThat(scalar(connection, "SELECT id::text FROM users WHERE email='two@example.com'")).isEqualTo(FIRST);
            for (String table : new String[]{"listings", "user_preferences"}) {
                assertThat(scalar(connection, "SELECT user_id::text FROM " + table)).isEqualTo(SECOND);
            }
            assertThat(scalar(connection, "SELECT user_id::text FROM notifications")).isEqualTo(SECOND);
            for (String table : new String[]{"favorites", "offers"}) {
                assertThat(scalar(connection, "SELECT count(*)::text FROM " + table)).isEqualTo("2");
            }
            assertThat(scalar(connection, "SELECT string_agg(conname || ':' || pg_get_constraintdef(oid), ';' ORDER BY conname) FROM pg_constraint WHERE contype='f' AND confrelid='users'::regclass AND conname <> 'fk_notification_outbox_user'")).isEqualTo(constraints.replace("FOREIGN KEY (recipient_id)", "FOREIGN KEY (user_id)"));
        });
    }

    @Test
    void refusesUnlinkedUsersWithoutChangingTheirIdsOrSchema() throws Exception {
        inSchema((connection, schema) -> {
            migrate(connection, schema, LEGACY);
            execute(connection, "INSERT INTO users(id,email,first_name,last_name) VALUES ('" + FIRST + "','unlinked@example.com','Unlinked','User')");
            assertThatThrownBy(() -> migrate(connection, schema, CURRENT))
                    .hasStackTraceContaining("every existing user must have a valid Cognito UUID subject");
            assertThat(columnExists(connection, schema)).isTrue();
            assertThat(scalar(connection, "SELECT id::text FROM users")).isEqualTo(FIRST);
        });
    }

    private void migrate(Connection connection, String schema, String changelog) throws Exception {
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        database.setDefaultSchemaName(schema);
        database.setLiquibaseSchemaName(schema);
        new Liquibase(changelog, new ClassLoaderResourceAccessor(), database).update(new Contexts(), new LabelExpression());
    }

    private boolean columnExists(Connection connection, String schema) throws Exception {
        return "1".equals(scalar(connection, "SELECT count(*)::text FROM information_schema.columns WHERE table_schema='" + schema + "' AND table_name='users' AND column_name='cognito_sub'"));
    }

    private void inSchema(Scenario scenario) throws Exception {
        try (var connection = DriverManager.getConnection(System.getenv("USER_ID_MIGRATION_JDBC_URL"),
                System.getenv().getOrDefault("USER_ID_MIGRATION_DB_USER", System.getProperty("user.name")),
                System.getenv().getOrDefault("USER_ID_MIGRATION_DB_PASSWORD", ""))) {
            String schema = "identity_test_" + UUID.randomUUID().toString().replace("-", "");
            execute(connection, "CREATE SCHEMA " + schema);
            connection.setSchema(schema);
            try {
                scenario.run(connection, schema);
            } finally {
                if (!connection.getAutoCommit()) connection.rollback();
                connection.setAutoCommit(true);
                connection.setSchema("public");
                execute(connection, "DROP SCHEMA " + schema + " CASCADE");
            }
        }
    }

    private void execute(Connection connection, String sql) throws Exception {
        try (var statement = connection.createStatement()) { statement.execute(sql); }
    }

    private String scalar(Connection connection, String sql) throws Exception {
        try (var statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            assertThat(result.next()).isTrue();
            return result.getString(1);
        }
    }

    @FunctionalInterface
    private interface Scenario { void run(Connection connection, String schema) throws Exception; }
}
