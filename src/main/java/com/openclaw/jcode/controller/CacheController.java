package com.openclaw.jcode.controller;

import com.openclaw.jcode.services.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 缓存控制器
 * 
 * 提供以下端点：
 * - GET /api/cache/stats - 缓存统计
 * - DELETE /api/cache - 清空缓存
 * - DELETE /api/cache/{key} - 删除指定缓存
 */
@RestController
@RequestMapping("/api/cache")
@Tag(name = "缓存管理", description = "缓存服务管理")
public class CacheController {
    
    private final CacheService cacheService;
    
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    /**
     * 获取缓存统计
     */
    @GetMapping("/stats")
    @Operation(summary = "缓存统计", description = "获取所有缓存的统计信息")
    public Map<String, Object> getStats() {
        return cacheService.getStats();
    }
    
    /**
     * 清空所有缓存
     */
    @DeleteMapping
    @Operation(summary = "清空缓存", description = "清空所有缓存数据")
    public Map<String, Object> invalidateAll() {
        cacheService.invalidateAll();
        return Map.of(
            "success", true,
            "message", "已清空所有缓存"
        );
    }
    
    /**
     * 删除指定缓存
     */
    @DeleteMapping("/{key}")
    @Operation(summary = "删除缓存", description = "删除指定 key 的缓存")
    public Map<String, Object> invalidate(@PathVariable String key) {
        cacheService.invalidate(key);
        return Map.of(
            "success", true,
            "key", key,
            "message", "缓存已删除"
        );
    }
}
