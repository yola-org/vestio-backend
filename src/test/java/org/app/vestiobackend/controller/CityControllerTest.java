package org.app.vestiobackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CityControllerTest extends AbstractControllerTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void getCitiesShouldReturnAllCities() throws Exception {
        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name == 'Casablanca')]").exists());

        // Verify cache
        Cache citiesCache = cacheManager.getCache("cities");
        assertNotNull(citiesCache);
        assertNotNull(citiesCache.get(SimpleKey.EMPTY));
    }
}
