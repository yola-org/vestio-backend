package org.app.vestiobackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SizeControllerTest extends AbstractControllerTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void getAllSizesShouldReturnSizeGroups() throws Exception {
        mockMvc.perform(get("/api/sizes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Verify cache
        Cache sizesCache = cacheManager.getCache("sizes");
        assertNotNull(sizesCache);
        assertNotNull(sizesCache.get(SimpleKey.EMPTY));
    }
}
