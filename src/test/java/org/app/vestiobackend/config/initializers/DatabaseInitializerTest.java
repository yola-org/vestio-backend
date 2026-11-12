package org.app.vestiobackend.config.initializers;

import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.repository.CategoryRepository;
import org.app.vestiobackend.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

class DatabaseInitializerTest {

    private static final UUID CASABLANCA_ID = UUID.fromString("13464db4-c346-5288-b2ae-97df62bc79ca");
    private static final UUID FEMME_ID = UUID.fromString("65aeaf07-a302-5175-be2d-950d4b5142b3");

    private final CityRepository cities = mock(CityRepository.class);
    private final CategoryRepository categories = mock(CategoryRepository.class);
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final DatabaseInitializer initializer = new DatabaseInitializer(cities, categories, jdbc);

    @Test
    void loadsAllCatalogTypesAndSkipsExistingCitiesAndCategories() throws Exception {
        when(cities.findById(CASABLANCA_ID)).thenReturn(Optional.of(City.builder().name("Casablanca").build()));
        when(categories.findById(FEMME_ID)).thenReturn(Optional.of(Category.builder().name("Femme").build()));

        initializer.run();

        ArgumentCaptor<String> statements = ArgumentCaptor.forClass(String.class);
        verify(jdbc, atLeastOnce()).execute(statements.capture());
        assertThat(statements.getAllValues())
                .anyMatch(sql -> sql.startsWith("INSERT INTO size_groups "))
                .anyMatch(sql -> sql.startsWith("INSERT INTO sizes "))
                .anyMatch(sql -> sql.startsWith("INSERT INTO cities ") && sql.contains("'Rabat'"))
                .anyMatch(sql -> sql.startsWith("INSERT INTO categories ") && sql.contains("'Robes'"))
                .noneMatch(sql -> sql.startsWith("INSERT INTO cities ") && sql.contains("'Casablanca'"))
                .noneMatch(sql -> sql.startsWith("INSERT INTO categories ") && sql.contains("'Femme'"));
    }

    @Test
    void rejectsAnExistingCityIdWithADifferentName() {
        when(cities.findById(CASABLANCA_ID)).thenReturn(Optional.of(City.builder().name("Different name").build()));

        assertThatThrownBy(initializer::run)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("City with ID " + CASABLANCA_ID)
                .hasMessageContaining("Different name vs Casablanca");
    }

    @Test
    void rejectsAnExistingCityNameWithADifferentId() {
        City conflict = City.builder().id(UUID.randomUUID()).name("Casablanca").build();
        when(cities.findByName("Casablanca")).thenReturn(Optional.of(conflict));

        assertThatThrownBy(initializer::run)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("City with name Casablanca")
                .hasMessageContaining(conflict.getId().toString());
    }

    @Test
    void rejectsAnExistingCategoryIdWithADifferentName() {
        when(categories.findById(FEMME_ID)).thenReturn(Optional.of(Category.builder().name("Different name").build()));

        assertThatThrownBy(initializer::run)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category with ID " + FEMME_ID)
                .hasMessageContaining("Different name vs Femme");
    }
}
