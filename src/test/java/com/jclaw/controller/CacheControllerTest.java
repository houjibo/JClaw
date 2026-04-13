package com.jclaw.controller;

import com.jclaw.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 缓存控制器测试
 */
@DisplayName("缓存控制器测试")
class CacheControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private CacheService cacheService;
    
    @InjectMocks
    private CacheController cacheController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cacheController).build();
    }
    
    @Test
    @DisplayName("测试获取缓存统计")
    void testGetCacheStats() throws Exception {
        // Arrange
        CacheService.CacheStats mockStats = new CacheService.CacheStats();
        mockStats.cacheName = "users";
        mockStats.available = true;
        mockStats.size = 100L;
        when(cacheService.getStats(anyString())).thenReturn(mockStats);
        
        // Act & Assert
        mockMvc.perform(get("/api/cache/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").exists())
            .andDo(print());
        
        verify(cacheService, atLeastOnce()).getStats(anyString());
    }
    
    @Test
    @DisplayName("测试清空指定缓存")
    void testClearCache() throws Exception {
        // Arrange
        doNothing().when(cacheService).clear("users");
        
        // Act & Assert
        mockMvc.perform(post("/api/cache/clear/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("缓存已清空：users"))
            .andDo(print());
        
        verify(cacheService, times(1)).clear("users");
    }
    
    @Test
    @DisplayName("测试清空所有缓存")
    void testClearAllCaches() throws Exception {
        // Arrange
        doNothing().when(cacheService).clear(anyString());
        
        // Act & Assert
        mockMvc.perform(post("/api/cache/clear/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("所有缓存已清空"))
            .andDo(print());
        
        verify(cacheService, atLeast(6)).clear(anyString());
    }
}
