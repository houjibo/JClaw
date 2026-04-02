package com.jclaw.controller;

import com.jclaw.config.HotReloadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 配置热重载控制器
 * 
 * 提供以下端点：
 * - GET /api/config - 获取所有配置
 * - GET /api/config/{key} - 获取指定配置
 * - PUT /api/config/{key} - 更新配置
 * - POST /api/config/reload - 重新加载配置
 * - GET /api/config/history - 列出历史版本
 * - POST /api/config/rollback - 回滚配置
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
    
    private final HotReloadConfig hotReloadConfig;
    
    public ConfigController(HotReloadConfig hotReloadConfig) {
        this.hotReloadConfig = hotReloadConfig;
    }
    
    /**
     * 获取所有配置
     */
    @GetMapping
    public Map<String, Object> getAllConfig() {
        logger.info("获取所有配置");
        return hotReloadConfig.getAllConfig();
    }
    
    /**
     * 获取指定配置
     */
    @GetMapping("/{key}")
    public Map<String, Object> getConfig(@PathVariable String key) {
        logger.info("获取配置：{}", key);
        Object value = hotReloadConfig.get(key, null);
        
        if (value == null) {
            return Map.of(
                "success", false,
                "error", "配置不存在：" + key
            );
        }
        
        return Map.of(
            "success", true,
            "key", key,
            "value", value
        );
    }
    
    /**
     * 更新配置
     */
    @PutMapping("/{key}")
    public Map<String, Object> updateConfig(
            @PathVariable String key,
            @RequestBody Map<String, Object> body) {
        
        Object value = body.get("value");
        if (value == null) {
            return Map.of(
                "success", false,
                "error", "value 不能为空"
            );
        }
        
        logger.info("更新配置：{} = {}", key, value);
        hotReloadConfig.set(key, value);
        
        return Map.of(
            "success", true,
            "key", key,
            "value", value
        );
    }
    
    /**
     * 重新加载配置
     */
    @PostMapping("/reload")
    public Map<String, Object> reloadConfig() {
        logger.info("手动重新加载配置");
        hotReloadConfig.reloadConfig();
        
        return Map.of(
            "success", true,
            "message", "配置已重新加载"
        );
    }
    
    /**
     * 列出历史版本
     */
    @GetMapping("/history")
    public Map<String, Object> listHistory() {
        logger.info("列出配置历史");
        List<String> history = hotReloadConfig.listHistory();
        
        return Map.of(
            "success", true,
            "count", history.size(),
            "versions", history
        );
    }
    
    /**
     * 回滚配置
     */
    @PostMapping("/rollback")
    public Map<String, Object> rollback(@RequestBody Map<String, String> body) {
        String timestamp = body.get("timestamp");
        if (timestamp == null) {
            return Map.of(
                "success", false,
                "error", "timestamp 不能为空"
            );
        }
        
        logger.info("回滚配置到：{}", timestamp);
        boolean success = hotReloadConfig.rollback(timestamp);
        
        if (success) {
            return Map.of(
                "success", true,
                "message", "配置已回滚到：" + timestamp
            );
        } else {
            return Map.of(
                "success", false,
                "error", "回滚失败"
            );
        }
    }
}
