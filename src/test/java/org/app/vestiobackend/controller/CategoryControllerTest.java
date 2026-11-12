package org.app.vestiobackend.controller;

import org.app.vestiobackend.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerTest extends AbstractControllerTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void getCategoriesShouldReturnHierarchicalCategories() throws Exception {
        // Setup hierarchical data
        Category parent = Category.builder()
                .id(UUID.randomUUID())
                .name("Femme")
                .build();
        parent = categoryRepository.save(parent);

        Category child = Category.builder()
                .id(UUID.randomUUID())
                .name("Robes")
                .parent(parent)
                .sizeGroup(testSizeGroup)
                .build();
        categoryRepository.save(child);
        categoryRepository.flush();

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name == 'Femme')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Femme')].subCategories").isNotEmpty())
                .andExpect(jsonPath("$[?(@.name == 'Femme')].subCategories[0].sizeGroup.name").value("Clothing"));

        // Verify cache
        Cache categoriesCache = cacheManager.getCache("categories");
        assertNotNull(categoriesCache);
        assertNotNull(categoriesCache.get(SimpleKey.EMPTY));
    }
}
