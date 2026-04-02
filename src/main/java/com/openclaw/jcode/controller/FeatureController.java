package com.openclaw.jcode.controller;

import com.openclaw.jcode.services.ProgressiveLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 渐进式加载控制器
 * 
 * 提供以下端点：
 * - GET /api/features - 列出所有特性
 * - PUT /api/features/{name} - 启用/禁用特性
 * - GET /api/startup/metrics - 获取启动指标
 * - POST /api/startup/preload - 预加载组件
 */
@RestController
@RequestMapping("/api/features")
public class FeatureController {
    
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);
    
    private final ProgressiveLoader progressiveLoader;
    
    public FeatureController(ProgressiveLoader progressiveLoader) {
        this.progressiveLoader = progressiveLoader;
    }
    
    /**
     * 列出所有特性
     */
    @GetMapping
    public List<Map<String, Object>> listFeatures() {
        logger.info("列出所有特性");
        return progressiveLoader.listFeatures();
    }
    
    /**
     * 启用/禁用特性
     */
    @PutMapping("/{name}")
    public Map<String, Object> toggleFeature(
            @PathVariable String name,
            @RequestBody Map<String, Boolean> body) {
        
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return Map.of(
                "success", false,
                "error", "enabled 参数不能为空"
            );
        }
        
        logger.info("切换特性：{} -> {}", name, enabled);
        progressiveLoader.setFeatureEnabled(name, enabled);
        
        return Map.of(
            "success", true,
            "feature", name,
            "enabled", enabled
        );
    }
    
    /**
     * 获取启动指标
     */
    @GetMapping("/startup/metrics")
    public Map<String, Object> getStartupMetrics() {
        logger.info("获取启动指标");
        return progressiveLoader.getStartupMetrics();
    }
    
    /**
     * 预加载组件
     */
    @PostMapping("/startup/preload")
    public Map<String, Object> preloadComponents(@RequestBody List<String> components) {
        logger.info("预加载组件：{}", components);
        progressiveLoader.preloadComponents(components);
        
        return Map.of(
            "success", true,
            "components", components
        );
    }
    
    /**
     * 获取当前启动阶段
     */
    @GetMapping("/startup/phase")
    public Map<String, Object> getCurrentPhase() {
        return Map.of(
            "success", true,
            "phase", progressiveLoader.getCurrentPhase().name()
        );
    }
}
