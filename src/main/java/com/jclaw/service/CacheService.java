package com.jclaw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * 缓存服务
 * 提供统一的缓存操作接口
 */
@Service
@Slf4j
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 从缓存获取数据，如果不存在则从加载器加载
     */
    public <T> T get(String cacheName, Object key, Callable<T> loader) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在：{}", cacheName);
            try {
                return loader.call();
            } catch (Exception e) {
                throw new RuntimeException("加载数据失败", e);
            }
        }
        
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null && wrapper.get() != null) {
            log.debug("缓存命中：{} - {}", cacheName, key);
            return (T) wrapper.get();
        }
        
        // 缓存未命中，从加载器加载
        try {
            log.debug("缓存未命中，从数据库加载：{} - {}", cacheName, key);
            T value = loader.call();
            if (value != null) {
                cache.put(key, value);
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("加载数据失败", e);
        }
    }

    /**
     * 放入缓存
     */
    public void put(String cacheName, Object key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
            log.debug("缓存已更新：{} - {}", cacheName, key);
        }
    }

    /**
     * 从缓存删除
     */
    public void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("缓存已清除：{} - {}", cacheName, key);
        }
    }

    /**
     * 清空整个缓存
     */
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("缓存已清空：{}", cacheName);
        }
    }

    /**
     * 获取缓存统计信息
     */
    public CacheStats getStats(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        
        // 注意：Caffeine 原生统计需要额外配置
        // 这里返回基本信息
        CacheStats stats = new CacheStats();
        stats.cacheName = cacheName;
        stats.available = true;
        return stats;
    }

    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        public String cacheName;
        public boolean available;
        public Long size;
        public Long hitCount;
        public Long missCount;
        public Double hitRate;
        
        @Override
        public String toString() {
            return String.format("CacheStats{cacheName='%s', available=%s, size=%d}", 
                cacheName, available, size != null ? size : 0);
        }
    }
}
