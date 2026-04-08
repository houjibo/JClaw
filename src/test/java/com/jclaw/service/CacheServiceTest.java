package com.jclaw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * CacheService 单元测试
 * IM platform service 层测试
 */
@DisplayName("CacheService 单元测试")
class CacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheService = new CacheService();
        // 使用反射注入 cacheManager 因为是 @Autowired
        try {
            java.lang.reflect.Field field = CacheService.class.getDeclaredField("cacheManager");
            field.setAccessible(true);
            field.set(cacheService, cacheManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("测试构造函数初始化")
    void testConstructor() {
        assertNotNull(cacheService);
    }

    @Test
    @DisplayName("测试获取缓存 - 缓存命中")
    void testGet_CacheHit() throws Exception {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";
        String expectedValue = "testValue";
        final boolean[] loaderCalled = {false};

        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        when(cache.get(key)).thenReturn(() -> expectedValue);

        // 使用真实 lambda，不 mock Callable
        Callable<String> loader = () -> {
            loaderCalled[0] = true;
            return "loadedValue";
        };

        // Act
        String result = cacheService.get(cacheName, key, loader);

        // Assert
        assertEquals(expectedValue, result);
        verify(cacheManager, times(1)).getCache(cacheName);
        verify(cache, times(1)).get(key);
        // 缓存命中不会调用 loader
        assertFalse(loaderCalled[0]);
    }

    @Test
    @DisplayName("测试获取缓存 - 缓存未命中，从加载器加载")
    void testGet_CacheMiss() throws Exception {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";
        String loadedValue = "loadedValue";
        final int[] callCount = {0};

        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        when(cache.get(key)).thenReturn(null);

        // 使用真实 lambda，不 mock Callable
        Callable<String> loader = () -> {
            callCount[0]++;
            return loadedValue;
        };

        // Act
        String result = cacheService.get(cacheName, key, loader);

        // Assert
        assertEquals(loadedValue, result);
        verify(cache, times(1)).put(key, loadedValue);
        assertEquals(1, callCount[0]);
    }

    @Test
    @DisplayName("测试获取缓存 - 缓存不存在")
    void testGet_CacheNotFound() throws Exception {
        // Arrange
        String cacheName = "nonExistentCache";
        String key = "testKey";
        String loadedValue = "loadedValue";

        when(cacheManager.getCache(cacheName)).thenReturn(null);

        Callable<String> loader = () -> loadedValue;

        // Act
        String result = cacheService.get(cacheName, key, loader);

        // Assert
        assertEquals(loadedValue, result);
        // 缓存不存在时不会尝试 put
        verify(cache, never()).put(any(), any());
    }

    @Test
    @DisplayName("测试获取缓存 - 加载器抛出异常")
    void testGet_LoaderThrowsException() throws Exception {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        when(cache.get(key)).thenReturn(null);

        // 使用真实 lambda 抛出异常，不 mock Callable
        Callable<String> loader = () -> {
            throw new RuntimeException("加载失败");
        };

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cacheService.get(cacheName, key, loader));
        assertTrue(exception.getMessage().contains("加载数据失败"));
    }

    @Test
    @DisplayName("测试放入缓存 - 缓存存在")
    void testPut_CacheExists() {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";
        String value = "testValue";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);

        // Act
        cacheService.put(cacheName, key, value);

        // Assert
        verify(cache, times(1)).put(key, value);
    }

    @Test
    @DisplayName("测试放入缓存 - 缓存不存在")
    void testPut_CacheNotFound() {
        // Arrange
        String cacheName = "nonExistentCache";
        String key = "testKey";
        String value = "testValue";

        when(cacheManager.getCache(cacheName)).thenReturn(null);

        // Act - 不应该抛出异常
        assertDoesNotThrow(() -> cacheService.put(cacheName, key, value));

        // Assert - 缓存不存在时静默处理
        verify(cache, never()).put(any(), any());
    }

    @Test
    @DisplayName("测试删除缓存 - 缓存存在")
    void testEvict_CacheExists() {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);

        // Act
        cacheService.evict(cacheName, key);

        // Assert
        verify(cache, times(1)).evict(key);
    }

    @Test
    @DisplayName("测试删除缓存 - 缓存不存在")
    void testEvict_CacheNotFound() {
        // Arrange
        String cacheName = "nonExistentCache";
        String key = "testKey";

        when(cacheManager.getCache(cacheName)).thenReturn(null);

        // Act - 不应该抛出异常
        assertDoesNotThrow(() -> cacheService.evict(cacheName, key));

        // Assert - 缓存不存在时静默处理
        verify(cache, never()).evict(any());
    }

    @Test
    @DisplayName("测试清空缓存 - 缓存存在")
    void testClear_CacheExists() {
        // Arrange
        String cacheName = "testCache";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);

        // Act
        cacheService.clear(cacheName);

        // Assert
        verify(cache, times(1)).clear();
    }

    @Test
    @DisplayName("测试清空缓存 - 缓存不存在")
    void testClear_CacheNotFound() {
        // Arrange
        String cacheName = "nonExistentCache";

        when(cacheManager.getCache(cacheName)).thenReturn(null);

        // Act - 不应该抛出异常
        assertDoesNotThrow(() -> cacheService.clear(cacheName));

        // Assert - 缓存不存在时静默处理
        verify(cache, never()).clear();
    }

    @Test
    @DisplayName("测试获取缓存统计 - 缓存存在")
    void testGetStats_CacheExists() {
        // Arrange
        String cacheName = "testCache";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);

        // Act
        CacheService.CacheStats stats = cacheService.getStats(cacheName);

        // Assert
        assertNotNull(stats);
        assertEquals(cacheName, stats.cacheName);
        assertTrue(stats.available);
    }

    @Test
    @DisplayName("测试获取缓存统计 - 缓存不存在")
    void testGetStats_CacheNotFound() {
        // Arrange
        String cacheName = "nonExistentCache";

        when(cacheManager.getCache(cacheName)).thenReturn(null);

        // Act
        CacheService.CacheStats stats = cacheService.getStats(cacheName);

        // Assert
        assertNull(stats);
    }

    @Test
    @DisplayName("测试 CacheStats toString")
    void testCacheStatsToString() {
        // Arrange
        CacheService.CacheStats stats = new CacheService.CacheStats();
        stats.cacheName = "testCache";
        stats.available = true;
        stats.size = 100L;

        // Act
        String str = stats.toString();

        // Assert
        assertTrue(str.contains("testCache"));
        assertTrue(str.contains("100"));
    }

    @Test
    @DisplayName("测试获取缓存 - 值为 null")
    void testGet_NullValue() throws Exception {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        when(cache.get(key)).thenReturn(() -> null);

        Callable<String> loader = () -> "loadedValue";

        // Act
        String result = cacheService.get(cacheName, key, loader);

        // Assert
        assertEquals("loadedValue", result);
        verify(cache, times(1)).put(key, "loadedValue");
    }

    @Test
    @DisplayName("测试加载值为 null")
    void testGet_LoadedValueNull() throws Exception {
        // Arrange
        String cacheName = "testCache";
        String key = "testKey";

        when(cacheManager.getCache(cacheName)).thenReturn(cache);
        when(cache.get(key)).thenReturn(null);

        Callable<String> loader = () -> null;

        // Act
        String result = cacheService.get(cacheName, key, loader);

        // Assert
        assertNull(result);
        // 加载值为 null 不会放入缓存
        verify(cache, never()).put(any(), any());
    }
}
