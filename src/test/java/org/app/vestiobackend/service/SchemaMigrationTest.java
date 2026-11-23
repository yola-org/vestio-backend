package org.app.vestiobackend.service;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaMigrationTest {
    @Test
    void freshSchemaEnforcesCollectionKeysAndListingChecks() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:schema-constraints;DB_CLOSE_DELAY=-1");
             Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml",
                     new ClassLoaderResourceAccessor(), new JdbcConnection(connection))) {
            liquibase.update(new Contexts());
            try (Statement statement = connection.createStatement()) {
                String user = "00000000-0000-0000-0000-000000000001";
                String category = "00000000-0000-0000-0000-000000000002";
                String listing = "00000000-0000-0000-0000-000000000003";
                String city = "00000000-0000-0000-0000-000000000004";
                String group = "00000000-0000-0000-0000-000000000005";
                String size = "00000000-0000-0000-0000-000000000006";
                statement.execute("INSERT INTO users (id, first_name, last_name, email) VALUES ('" + user + "', 'Test', 'User', 'schema@test.local')");
                statement.execute("INSERT INTO categories (id, name) VALUES ('" + category + "', 'Tops')");
                statement.execute("INSERT INTO cities (id, name) VALUES ('" + city + "', 'Test City')");
                statement.execute("INSERT INTO size_groups (id, name) VALUES ('" + group + "', 'Clothing')");
                statement.execute("INSERT INTO sizes (id, name, size_group_id) VALUES ('" + size + "', 'M', '" + group + "')");
                statement.execute("INSERT INTO listings (id, title, price, condition, status, quantity, user_id, category_id, created_at, updated_at) VALUES ('"
                        + listing + "', 'Test', 10, 'GOOD', 'AVAILABLE', 1, '" + user + "', '" + category + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
                for (String insert : List.of(
                        "INSERT INTO listing_cities VALUES ('" + listing + "', '" + city + "')",
                        "INSERT INTO listing_sizes VALUES ('" + listing + "', '" + size + "')",
                        "INSERT INTO listing_colors VALUES ('" + listing + "', 'Black')",
                        "INSERT INTO listing_contact_types VALUES ('" + listing + "', 'MEETUP')")) {
                    statement.execute(insert);
                    assertThrows(SQLException.class, () -> statement.execute(insert));
                }
                assertThrows(SQLException.class, () -> statement.execute("UPDATE listings SET price = 0"));
                assertThrows(SQLException.class, () -> statement.execute("UPDATE listings SET quantity = 0"));
                try (ResultSet columns = connection.getMetaData().getColumns(null, null, "USER_PREFERENCES", "USER_ID")) {
                    assertThat(columns.next()).isTrue();
                    assertThat(columns.getString("IS_NULLABLE")).isEqualTo("NO");
                }
            }
            // Every domain must also roll back in reverse dependency order.
            int appliedChanges;
            try (Statement statement = connection.createStatement();
                 ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM DATABASECHANGELOG")) {
                rows.next();
                appliedChanges = rows.getInt(1);
            }
            liquibase.rollback(appliedChanges, "");
            try (ResultSet tables = connection.getMetaData().getTables(null, null, "LISTINGS", null)) {
                assertThat(tables.next()).isFalse();
            }
        }
    }
}
