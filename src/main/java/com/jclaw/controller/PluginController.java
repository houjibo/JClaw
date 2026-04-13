package com.jclaw.controller;

import com.jclaw.plugin.PluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 插件管理 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/plugins")
@RequiredArgsConstructor
public class PluginController {
    
    private final PluginManager pluginManager;
    
    /**
     * 列出所有插件
     */
    @GetMapping
    public ResponseEntity<List<PluginManager.Plugin>> listPlugins() {
        return ResponseEntity.ok(pluginManager.listPlugins());
    }
    
    /**
     * 获取插件详情
     */
    @GetMapping("/{name}")
    public ResponseEntity<PluginManager.Plugin> getPlugin(@PathVariable String name) {
        PluginManager.Plugin plugin = pluginManager.getPlugin(name);
        if (plugin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plugin);
    }
    
    /**
     * 注册插件
     */
    @PostMapping
    public ResponseEntity<PluginManager.Plugin> registerPlugin(@RequestBody RegisterRequest request) {
        PluginManager.Plugin plugin = pluginManager.registerPlugin(
            request.getName(),
            request.getVersion(),
            request.getDescription()
        );
        return ResponseEntity.ok(plugin);
    }
    
    /**
     * 卸载插件
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> unregisterPlugin(@PathVariable String name) {
        boolean success = pluginManager.unregisterPlugin(name);
        return success
            ? ResponseEntity.ok(Map.of("status", "unregistered"))
            : ResponseEntity.badRequest().body(Map.of("error", "插件不存在"));
    }
    
    /**
     * 启用插件
     */
    @PostMapping("/{name}/enable")
    public ResponseEntity<Map<String, String>> enablePlugin(@PathVariable String name) {
        boolean success = pluginManager.enablePlugin(name);
        return success
            ? ResponseEntity.ok(Map.of("status", "enabled"))
            : ResponseEntity.badRequest().body(Map.of("error", "插件不存在"));
    }
    
    /**
     * 禁用插件
     */
    @PostMapping("/{name}/disable")
    public ResponseEntity<Map<String, String>> disablePlugin(@PathVariable String name) {
        boolean success = pluginManager.disablePlugin(name);
        return success
            ? ResponseEntity.ok(Map.of("status", "disabled"))
            : ResponseEntity.badRequest().body(Map.of("error", "插件不存在"));
    }
    
    public static class RegisterRequest {
        private String name;
        private String version;
        private String description;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
