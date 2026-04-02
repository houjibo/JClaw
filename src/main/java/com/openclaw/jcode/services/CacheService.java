package com.openclaw.jcode.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 * 
 * 功能：
 * - 本地缓存
 * - 自动过期
 * - 统计信息
 */
@Service
public class CacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    
    /**
     * 工具结果缓存
     */
    private final Cache<String, Object> toolResultCache;
    
    /**
     * 文件内容缓存
     */
    private final Cache<String, String> fileContentCache;
    
    /**
     * API 响应缓存
     */
    private final Cache<String, Object> apiResponseCache;
    
    public CacheService() {
        // 工具结果缓存：最大 1000 项，10 分钟过期
        this.toolResultCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        // 文件内容缓存：最大 500 项，5 分钟过期
        this.fileContentCache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        // API 响应缓存：最大 2000 项，15 分钟过期
        this.apiResponseCache = Caffeine.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        logger.info("缓存服务已初始化");
    }
    
    /**
     * 获取工具结果缓存
     */
    public Object getToolResult(String key) {
        return toolResultCache.getIfPresent(key);
    }
    
    /**
     * 设置工具结果缓存
     */
    public void putToolResult(String key, Object value) {
        toolResultCache.put(key, value);
        logger.debug("缓存工具结果：{}", key);
    }
    
    /**
     * 获取文件内容缓存
     */
    public String getFileContent(String key) {
        return fileContentCache.getIfPresent(key);
    }
    
    /**
     * 设置文件内容缓存
     */
    public void putFileContent(String key, String value) {
        fileContentCache.put(key, value);
        logger.debug("缓存文件内容：{}", key);
    }
    
    /**
     * 获取 API 响应缓存
     */
    public Object getApiResponse(String key) {
        return apiResponseCache.getIfPresent(key);
    }
    
    /**
     * 设置 API 响应缓存
     */
    public void putApiResponse(String key, Object value) {
        apiResponseCache.put(key, value);
        logger.debug("缓存 API 响应：{}", key);
    }
    
    /**
     * 使缓存失效
     */
    public void invalidate(String key) {
        toolResultCache.invalidate(key);
        fileContentCache.invalidate(key);
        apiResponseCache.invalidate(key);
        logger.debug("缓存失效：{}", key);
    }
    
    /**
     * 清空所有缓存
     */
    public void invalidateAll() {
        toolResultCache.invalidateAll();
        fileContentCache.invalidateAll();
        apiResponseCache.invalidateAll();
        logger.info("清空所有缓存");
    }
    
    /**
     * 获取缓存统计
     */
    public Map<String, Object> getStats() {
        return Map.of(
            "toolResultCache", Map.of(
                "size", toolResultCache.estimatedSize(),
                "hits", toolResultCache.stats().hitCount(),
                "misses", toolResultCache.stats().missCount(),
                "hitRate", toolResultCache.stats().hitRate()
            ),
            "fileContentCache", Map.of(
                "size", fileContentCache.estimatedSize(),
                "hits", fileContentCache.stats().hitCount(),
                "misses", fileContentCache.stats().missCount(),
                "hitRate", fileContentCache.stats().hitRate()
            ),
            "apiResponseCache", Map.of(
                "size", apiResponseCache.estimatedSize(),
                "hits", apiResponseCache.stats().hitCount(),
                "misses", apiResponseCache.stats().missCount(),
                "hitRate", apiResponseCache.stats().hitRate()
            )
        );
    }
}
