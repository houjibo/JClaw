package com.openclaw.jcode.controller;

import com.openclaw.jcode.services.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * 缓存控制器测试
 */
@DisplayName("缓存控制器测试")
class CacheControllerTest {
    
    private MockMvc mockMvc;
    private CacheService cacheService;
    
    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
        mockMvc = MockMvcBuilders.standaloneSetup(new CacheController(cacheService)).build();
    }
    
    @Test
    @DisplayName("测试获取缓存统计")
    void testGetStats() throws Exception {
        mockMvc.perform(get("/api/cache/stats")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.toolResultCache").exists())
            .andExpect(jsonPath("$.fileContentCache").exists())
            .andDo(print());
    }
    
    @Test
    @DisplayName("测试清空所有缓存")
    void testInvalidateAll() throws Exception {
        mockMvc.perform(delete("/api/cache")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("已清空所有缓存"))
            .andDo(print());
    }
    
    @Test
    @DisplayName("测试删除指定缓存")
    void testInvalidate() throws Exception {
        mockMvc.perform(delete("/api/cache/test-key")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.key").value("test-key"))
            .andExpect(jsonPath("$.message").value("缓存已删除"))
            .andDo(print());
    }
}
