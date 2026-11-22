package org.app.vestiobackend.service;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileImageKeyMigrationTest {
    @Test
    void existingImageKeysBecomeRelativeWithoutChangingRelativeOrNullValues() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:profile-key-migration;DB_CLOSE_DELAY=-1")) {
            try (var statement = connection.createStatement()) {
                statement.execute("CREATE TABLE users (id INT PRIMARY KEY, profile_image_key VARCHAR(512))");
                statement.execute("INSERT INTO users VALUES (1, 'users/owner/profile/image.png'), (2, 'owner/profile/relative.jpg'), (3, NULL)");
            }
            try (var liquibase = new Liquibase("db/changelog/changesets/014-relativize-user-profile-image-key.yaml",
                    new ClassLoaderResourceAccessor(), new JdbcConnection(connection))) {
                liquibase.update(new Contexts());
                try (var statement = connection.createStatement();
                     var rows = statement.executeQuery("SELECT profile_image_key FROM users ORDER BY id")) {
                    assertThat(rows.next()).isTrue();
                    assertThat(rows.getString(1)).isEqualTo("owner/profile/image.png");
                    assertThat(rows.next()).isTrue();
                    assertThat(rows.getString(1)).isEqualTo("owner/profile/relative.jpg");
                    assertThat(rows.next()).isTrue();
                    assertThat(rows.getString(1)).isNull();
                }
            }
        }
    }
}
