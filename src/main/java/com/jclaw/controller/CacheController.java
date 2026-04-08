package com.jclaw.controller;

import com.jclaw.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存监控 API
 */
@RestController
@RequestMapping("/api/cache")
@Slf4j
public class CacheController {

    @Autowired
    private CacheService cacheService;

    /**
     * 获取缓存统计
     */
    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> result = new HashMap<>();
        
        String[] cacheNames = {"users", "intents", "memories", "codeUnits", "subsystems", "callChains"};
        Map<String, Object> stats = new HashMap<>();
        
        for (String cacheName : cacheNames) {
            CacheService.CacheStats cacheStats = cacheService.getStats(cacheName);
            if (cacheStats != null) {
                stats.put(cacheName, cacheStats);
            }
        }
        
        result.put("success", true);
        result.put("data", stats);
        return result;
    }

    /**
     * 清空指定缓存
     */
    @PostMapping("/clear/{cacheName}")
    public Map<String, Object> clearCache(@PathVariable String cacheName) {
        log.info("清空缓存：{}", cacheName);
        cacheService.clear(cacheName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缓存已清空：" + cacheName);
        return result;
    }

    /**
     * 清空所有缓存
     */
    @PostMapping("/clear/all")
    public Map<String, Object> clearAllCaches() {
        log.info("清空所有缓存");
        
        String[] cacheNames = {"users", "intents", "memories", "codeUnits", "subsystems", "callChains"};
        for (String cacheName : cacheNames) {
            cacheService.clear(cacheName);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "所有缓存已清空");
        return result;
    }
}
