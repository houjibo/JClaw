package com.jclaw.controller;

import com.jclaw.config.JClawProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 配置 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    
    private final JClawProperties jclawProperties;
    
    /**
     * 获取当前配置
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = Map.of(
            "ai", Map.of(
                "model", jclawProperties.getAi().getModel(),
                "zhipuConfigured", jclawProperties.getAi().getZhipu().getApiKey() != null && 
                    !jclawProperties.getAi().getZhipu().getApiKey().isEmpty() &&
                    !jclawProperties.getAi().getZhipu().getApiKey().equals("your-api-key-here")
            ),
            "memory", Map.of(
                "enabled", jclawProperties.getMemory().isEnabled(),
                "path", jclawProperties.getMemory().getPath(),
                "dailyLogEnabled", jclawProperties.getMemory().isDailyLogEnabled()
            ),
            "channels", Map.of(
                "enabled", jclawProperties.getChannels().isEnabled(),
                "feishuEnabled", jclawProperties.getChannels().getFeishu().isEnabled()
            ),
            "skills", Map.of(
                "enabled", jclawProperties.getSkills().isEnabled()
            )
        );
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * 配置状态检查
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> configStatus() {
        boolean aiConfigured = jclawProperties.getAi().getZhipu().getApiKey() != null && 
            !jclawProperties.getAi().getZhipu().getApiKey().isEmpty() &&
            !jclawProperties.getAi().getZhipu().getApiKey().equals("your-api-key-here");
        
        boolean feishuConfigured = jclawProperties.getChannels().getFeishu().getAppId() != null && 
            !jclawProperties.getChannels().getFeishu().getAppId().isEmpty();
        
        Map<String, Object> status = Map.of(
            "ready", aiConfigured,
            "checks", Map.of(
                "aiConfigured", aiConfigured,
                "feishuConfigured", feishuConfigured,
                "memoryEnabled", jclawProperties.getMemory().isEnabled(),
                "skillsEnabled", jclawProperties.getSkills().isEnabled()
            ),
            "warnings", aiConfigured ? new String[0] : new String[]{"智谱 AI API Key 未配置"}
        );
        
        return ResponseEntity.ok(status);
    }
}
