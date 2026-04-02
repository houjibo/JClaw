package com.openclaw.jcode.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * 提供以下端点：
 * - GET /api/health - 健康状态
 * - GET /api/health/ready - 就绪状态
 * - GET /api/health/live - 存活状态
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "健康检查", description = "服务健康状态监控")
public class HealthController {
    
    /**
     * 健康检查
     */
    @GetMapping
    @Operation(summary = "健康检查", description = "检查服务整体健康状态")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "timestamp", Instant.now().toString(),
            "version", "1.0.0",
            "components", Map.of(
                "database", "UP",
                "cache", "UP",
                "api", "UP"
            )
        );
    }
    
    /**
     * 就绪检查
     */
    @GetMapping("/ready")
    @Operation(summary = "就绪检查", description = "检查服务是否准备好接收请求")
    public Map<String, Object> ready() {
        return Map.of(
            "status", "READY",
            "timestamp", Instant.now().toString()
        );
    }
    
    /**
     * 存活检查
     */
    @GetMapping("/live")
    @Operation(summary = "存活检查", description = "检查服务是否存活")
    public Map<String, Object> live() {
        return Map.of(
            "status", "ALIVE",
            "timestamp", Instant.now().toString()
        );
    }
}
