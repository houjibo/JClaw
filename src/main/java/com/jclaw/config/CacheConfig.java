package com.jclaw.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine 本地缓存配置（一级缓存）
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 用户缓存：最大 1000 条，过期时间 10 分钟
        cacheManager.registerCustomCache("users", 
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build());
        
        // 意图缓存：最大 5000 条，过期时间 30 分钟
        cacheManager.registerCustomCache("intents", 
            Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
        
        // 记忆缓存：最大 2000 条，过期时间 1 小时
        cacheManager.registerCustomCache("memories", 
            Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
        
        // 代码单元缓存：最大 10000 条，过期时间 1 小时
        cacheManager.registerCustomCache("codeUnits", 
            Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());
        
        // 调用链缓存：最大 5000 条，过期时间 30 分钟
        cacheManager.registerCustomCache("callChains", 
            Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
        
        // 子系统缓存：最大 500 条，过期时间 2 小时
        cacheManager.registerCustomCache("subsystems", 
            Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(2, TimeUnit.HOURS)
                .build());
        
        return cacheManager;
    }

    /**
     * Redis 分布式缓存配置（二级缓存）
     * 当前主要使用 Caffeine 本地缓存，Redis 作为可选扩展
     */
    // TODO: 当需要 Redis 分布式缓存时启用此配置
    // @Bean
    // public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    //     RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
    //         .entryTtl(Duration.ofMinutes(30));
    //     
    //     return RedisCacheManager.builder(connectionFactory)
    //         .cacheDefaults(config)
    //         .build();
    // }
}
