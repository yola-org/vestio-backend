package org.app.vestiobackend.service;

import java.util.Map;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogSeedTest {
    @Test
    void seedCanBeReplayedAgainstExistingSchemaAndPopulatedCatalog() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:catalog-seed;DB_CLOSE_DELAY=-1");
             var schema = new Liquibase("db/changelog/db.changelog-master.yaml",
                     new ClassLoaderResourceAccessor(), new JdbcConnection(connection))) {
            schema.update(new Contexts());
            try (var statement = connection.createStatement()) {
                statement.execute("INSERT INTO size_groups VALUES ('b77d5d2e-085c-53a0-a81b-83523baf001d', 'Shoes (Adult)')");
                statement.execute("INSERT INTO sizes VALUES ('c601df5f-ff49-5d36-832a-dab375481b43', '40', 'b77d5d2e-085c-53a0-a81b-83523baf001d')");
            }
            // Simulate an already imported detailed category with a listing and overlapping preferences.
            try (var statement = connection.createStatement()) {
                statement.execute("INSERT INTO size_groups VALUES ('439daea4-0ef8-5395-bf7a-8f9941c816b3', 'Clothing (Adult)')");
                statement.execute("INSERT INTO sizes VALUES ('df4148ba-6e17-5124-96ff-83c0b0548233', 'M', '439daea4-0ef8-5395-bf7a-8f9941c816b3')");
                statement.execute("INSERT INTO categories (id, name) VALUES ('65aeaf07-a302-5175-be2d-950d4b5142b3', 'Femme')");
                statement.execute("INSERT INTO categories (id, name, parent_id, size_group_id) VALUES ('d103cc35-670f-5520-b0b8-846cb4950a02', 'Vestes', '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3')");
                statement.execute("INSERT INTO categories (id, name, parent_id, size_group_id) VALUES ('75e6051c-6a34-586c-bfb6-65e5166a9644', 'Jackets et blousons', '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3')");
                statement.execute("INSERT INTO users (id, first_name, last_name, email) VALUES ('00000000-0000-0000-0000-000000000001', 'Test', 'User', 'seed@test.local')");
                statement.execute("INSERT INTO listings (id, title, price, condition, status, quantity, user_id, category_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000002', 'My jacket', 10, 'GOOD', 'AVAILABLE', 1, '00000000-0000-0000-0000-000000000001', '75e6051c-6a34-586c-bfb6-65e5166a9644', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
                statement.execute("INSERT INTO listing_sizes VALUES ('00000000-0000-0000-0000-000000000002', 'df4148ba-6e17-5124-96ff-83c0b0548233')");
                statement.execute("INSERT INTO user_preferences (id, user_id, category_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '75e6051c-6a34-586c-bfb6-65e5166a9644')");
                statement.execute("INSERT INTO user_preferences (id, user_id, category_id) VALUES ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'd103cc35-670f-5520-b0b8-846cb4950a02')");
            }
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/initial_data.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/initial_data.sql"));
            try (var statement = connection.createStatement()) {
                try (var rows = statement.executeQuery("SELECT name FROM sizes WHERE id = 'c601df5f-ff49-5d36-832a-dab375481b43'")) {
                    assertThat(rows.next()).isTrue();
                    assertThat(rows.getString(1)).isEqualTo("EU 40");
                }
                try (var rows = statement.executeQuery("SELECT COUNT(DISTINCT parent_id) FROM categories WHERE name = 'Vestes, jackets et manteaux'")) {
                    rows.next();
                    assertThat(rows.getInt(1)).isEqualTo(2);
                }
                try (var rows = statement.executeQuery("SELECT name, parent_id FROM categories GROUP BY name, parent_id HAVING COUNT(*) > 1")) {
                    assertThat(rows.next()).as("No duplicate item types within a department").isFalse();
                }
                try (var rows = statement.executeQuery("SELECT COUNT(*) FROM categories c LEFT JOIN size_groups g ON c.size_group_id = g.id WHERE c.parent_id IS NOT NULL AND g.id IS NULL")) {
                    rows.next();
                    assertThat(rows.getInt(1)).as("Every item type has a valid size group").isZero();
                }
                try (var rows = statement.executeQuery("SELECT category_id FROM listings WHERE id = '00000000-0000-0000-0000-000000000002'")) {
                    rows.next();
                    assertThat(rows.getString(1)).isEqualTo("d103cc35-670f-5520-b0b8-846cb4950a02");
                }
                try (var rows = statement.executeQuery("SELECT COUNT(*) FROM user_preferences WHERE user_id = '00000000-0000-0000-0000-000000000001' AND category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02'")) {
                    rows.next();
                    assertThat(rows.getInt(1)).isEqualTo(1);
                }
                try (var rows = statement.executeQuery("SELECT COUNT(*) FROM listing_sizes WHERE listing_id = '00000000-0000-0000-0000-000000000002' AND size_id = 'df4148ba-6e17-5124-96ff-83c0b0548233'")) {
                    rows.next();
                    assertThat(rows.getInt(1)).isEqualTo(1);
                }
                for (var entry : Map.of("categories", 69, "sizes", 180, "size_groups", 10, "cities", 12).entrySet()) {
                    try (var rows = statement.executeQuery("SELECT COUNT(*) FROM " + entry.getKey())) {
                        rows.next();
                        assertThat(rows.getInt(1)).as(entry.getKey()).isEqualTo(entry.getValue());
                    }
                }
            }
        }
    }
}
