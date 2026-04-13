package com.jclaw.plugin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件管理服务
 */
@Slf4j
@Service
public class PluginManager {
    
    // 内存存储插件
    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();
    
    /**
     * 注册插件
     */
    public Plugin registerPlugin(String name, String version, String description) {
        Plugin plugin = new Plugin();
        plugin.setId(name);
        plugin.setName(name);
        plugin.setVersion(version);
        plugin.setDescription(description);
        plugin.setStatus("active");
        plugin.setRegisteredAt(LocalDateTime.now());
        
        plugins.put(name, plugin);
        
        log.info("注册插件：{} v{}", name, version);
        return plugin;
    }
    
    /**
     * 卸载插件
     */
    public boolean unregisterPlugin(String name) {
        Plugin plugin = plugins.remove(name);
        if (plugin != null) {
            log.info("卸载插件：{}", name);
            return true;
        }
        return false;
    }
    
    /**
     * 获取插件
     */
    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }
    
    /**
     * 列出所有插件
     */
    public List<Plugin> listPlugins() {
        return new ArrayList<>(plugins.values());
    }
    
    /**
     * 启用插件
     */
    public boolean enablePlugin(String name) {
        Plugin plugin = plugins.get(name);
        if (plugin != null) {
            plugin.setStatus("active");
            log.info("启用插件：{}", name);
            return true;
        }
        return false;
    }
    
    /**
     * 禁用插件
     */
    public boolean disablePlugin(String name) {
        Plugin plugin = plugins.get(name);
        if (plugin != null) {
            plugin.setStatus("disabled");
            log.info("禁用插件：{}", name);
            return true;
        }
        return false;
    }
    
    /**
     * 检查插件是否可用
     */
    public boolean isPluginAvailable(String name) {
        Plugin plugin = plugins.get(name);
        return plugin != null && "active".equals(plugin.getStatus());
    }
    
    /**
     * 插件信息
     */
    @Data
    @NoArgsConstructor
    public static class Plugin {
        private String id;
        private String name;
        private String version;
        private String description;
        private String status; // active, disabled, error
        private LocalDateTime registeredAt;
        private Map<String, Object> config = new HashMap<>();
    }
}
