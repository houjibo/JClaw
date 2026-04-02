package com.jclaw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheService 单元测试
 */
@DisplayName("缓存服务测试")
class CacheServiceTest {
    
    private CacheService cacheService;
    
    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }
    
    @Test
    @DisplayName("测试工具结果缓存")
    void testToolResultCache() {
        // 测试缓存写入
        cacheService.putToolResult("test-key", "test-value");
        
        // 测试缓存读取
        Object value = cacheService.getToolResult("test-key");
        assertEquals("test-value", value);
        
        // 测试不存在的 key
        Object nullValue = cacheService.getToolResult("non-exist");
        assertNull(nullValue);
    }
    
    @Test
    @DisplayName("测试文件内容缓存")
    void testFileContentCache() {
        // 测试缓存写入
        cacheService.putFileContent("/path/to/file", "file content");
        
        // 测试缓存读取
        String content = cacheService.getFileContent("/path/to/file");
        assertEquals("file content", content);
    }
    
    @Test
    @DisplayName("测试 API 响应缓存")
    void testApiResponseCache() {
        // 测试缓存写入
        Map<String, Object> responseData = Map.of("status", "success", "data", 123);
        cacheService.putApiResponse("api-key", responseData);
        
        // 测试缓存读取
        Object cached = cacheService.getApiResponse("api-key");
        assertEquals(responseData, cached);
    }
    
    @Test
    @DisplayName("测试缓存失效")
    void testInvalidate() {
        // 写入缓存
        cacheService.putToolResult("key1", "value1");
        cacheService.putFileContent("key2", "value2");
        cacheService.putApiResponse("key3", "value3");
        
        // 使缓存失效
        cacheService.invalidate("key1");
        
        // 验证失效
        assertNull(cacheService.getToolResult("key1"));
        assertNotNull(cacheService.getFileContent("key2"));
        assertNotNull(cacheService.getApiResponse("key3"));
    }
    
    @Test
    @DisplayName("测试清空所有缓存")
    void testInvalidateAll() {
        // 写入多个缓存
        cacheService.putToolResult("key1", "value1");
        cacheService.putFileContent("key2", "value2");
        cacheService.putApiResponse("key3", "value3");
        
        // 清空所有缓存
        cacheService.invalidateAll();
        
        // 验证全部清空
        assertNull(cacheService.getToolResult("key1"));
        assertNull(cacheService.getFileContent("key2"));
        assertNull(cacheService.getApiResponse("key3"));
    }
    
    @Test
    @DisplayName("测试缓存统计")
    void testGetStats() {
        // 写入一些数据
        cacheService.putToolResult("key1", "value1");
        cacheService.getToolResult("key1"); // hit
        cacheService.getToolResult("non-exist"); // miss
        
        // 获取统计
        Map<String, Object> stats = cacheService.getStats();
        
        // 验证统计信息
        assertTrue(stats.containsKey("toolResultCache"));
        assertTrue(stats.containsKey("fileContentCache"));
        assertTrue(stats.containsKey("apiResponseCache"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> toolStats = (Map<String, Object>) stats.get("toolResultCache");
        assertTrue((Long) toolStats.get("hits") >= 1);
        assertTrue((Long) toolStats.get("misses") >= 1);
    }
    
    @Test
    @DisplayName("测试缓存覆盖")
    void testCacheOverwrite() {
        // 第一次写入
        cacheService.putToolResult("key", "value1");
        assertEquals("value1", cacheService.getToolResult("key"));
        
        // 覆盖写入
        cacheService.putToolResult("key", "value2");
        assertEquals("value2", cacheService.getToolResult("key"));
    }
}
