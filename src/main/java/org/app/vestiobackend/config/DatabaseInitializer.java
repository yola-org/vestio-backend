package org.app.vestiobackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.repository.CategoryRepository;
import org.app.vestiobackend.repository.CityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final JdbcTemplate jdbcTemplate;

    private int citiesInserted = 0;
    private int categoriesInserted = 0;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("[INITIALIZATION] Starting DatabaseInitializer (Order 1) - loading initial_data.sql");
        loadAndCheckSqlData();
        log.info("[INITIALIZATION] DatabaseInitializer completed. Cities inserted: {}, Categories inserted: {}", citiesInserted, categoriesInserted);
    }

    private void loadAndCheckSqlData() throws Exception {
        citiesInserted = 0;
        categoriesInserted = 0;
        ClassPathResource resource = new ClassPathResource("sql/initial_data.sql");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }

                if (line.startsWith("INSERT INTO cities")) {
                    processCityInsert(line);
                } else if (line.startsWith("INSERT INTO size_groups")) {
                    processSizeGroupInsert(line);
                } else if (line.startsWith("INSERT INTO sizes")) {
                    processSizeInsert(line);
                } else if (line.startsWith("INSERT INTO categories")) {
                    processCategoryInsert(line);
                }
            }
        }
    }

    private void processSizeGroupInsert(String sql) {
        Pattern pattern = Pattern.compile("VALUES\\s*\\(\\s*'(.*?)'\\s*,\\s*'(.*?)'\\s*\\)");
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            UUID id = UUID.fromString(matcher.group(1));
            jdbcTemplate.execute(sql);
        }
    }

    private void processSizeInsert(String sql) {
        // (id, name, size_group_id)
        Pattern pattern = Pattern.compile("VALUES\\s*\\(\\s*'(.*?)'\\s*,\\s*'(.*?)'\\s*,\\s*'(.*?)'\\s*\\)");
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            jdbcTemplate.execute(sql);
        }
    }

    private void processCityInsert(String sql) {
        Pattern pattern = Pattern.compile("VALUES\\s*\\(\\s*'(.*?)'\\s*,\\s*'(.*?)'\\s*\\)");
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            UUID id = UUID.fromString(matcher.group(1));
            String name = matcher.group(2);

            Optional<City> existingById = cityRepository.findById(id);
            Optional<City> existingByName = cityRepository.findByName(name);

            if (existingById.isPresent()) {
                if (!existingById.get().getName().equals(name)) {
                    throw new RuntimeException("Database conflict: City with ID " + id + " already exists with different name: " + existingById.get().getName() + " vs " + name);
                }
            } else if (existingByName.isPresent()) {
                throw new RuntimeException("Database conflict: City with name " + name + " already exists with different ID: " + existingByName.get().getId() + " vs " + id);
            } else {
                jdbcTemplate.execute(sql);
                citiesInserted++;
            }
        }
    }

    private void processCategoryInsert(String sql) {
        // Updated regex to handle more columns (id, name, image, parent_id, size_group_id) and handle NULL values
        Pattern pattern = Pattern.compile("INSERT INTO categories \\(id, name, image, parent_id, size_group_id\\) VALUES \\s*\\(\\s*'(.*?)'\\s*,\\s*'(.*?)'\\s*,\\s*(NULL|'.*?')\\s*,\\s*(NULL|'.*?')\\s*,\\s*(NULL|'.*?')\\s*\\)");
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            UUID id = UUID.fromString(matcher.group(1));
            String name = matcher.group(2);
            String imageRaw = matcher.group(3);
            String parentIdRaw = matcher.group(4);
            String sizeGroupIdRaw = matcher.group(5);

            String image = null;
            if (!"NULL".equals(imageRaw)) {
                image = imageRaw.substring(1, imageRaw.length() - 1);
            }

            UUID parentId = null;
            if (!"NULL".equals(parentIdRaw)) {
                parentId = UUID.fromString(parentIdRaw.substring(1, parentIdRaw.length() - 1));
            }

            UUID sizeGroupId = null;
            if (!"NULL".equals(sizeGroupIdRaw)) {
                sizeGroupId = UUID.fromString(sizeGroupIdRaw.substring(1, sizeGroupIdRaw.length() - 1));
            }

            Optional<Category> existingById = categoryRepository.findById(id);

            if (existingById.isPresent()) {
                Category cat = existingById.get();
                if (!cat.getName().equals(name)) {
                    throw new RuntimeException("Database conflict: Category with ID " + id + " already exists with different name: " + cat.getName() + " vs " + name);
                }
            } else {
                jdbcTemplate.execute(sql);
                categoriesInserted++;
            }
        }
    }
}
