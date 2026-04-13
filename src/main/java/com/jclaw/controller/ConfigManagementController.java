package com.jclaw.controller;

import com.jclaw.config.ConfigHotReloadListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 配置管理 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigManagementController {
    
    private final ConfigHotReloadListener configHotReloadListener;
    
    /**
     * 触发配置重载
     */
    @PostMapping("/reload")
    public ResponseEntity<Map<String, String>> reloadConfig() {
        log.info("手动触发配置重载");
        configHotReloadListener.reloadConfig();
        return ResponseEntity.ok(Map.of(
            "status", "warning",
            "message", "配置热重载功能尚未完全实现，请重启应用"
        ));
    }
    
    /**
     * 获取配置信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getConfigInfo() {
        return ResponseEntity.ok(Map.of(
            "hotReloadEnabled", false,
            "configFiles", new String[] {"application.yml", "application.properties"},
            "note", "配置修改后需要重启应用"
        ));
    }
}
