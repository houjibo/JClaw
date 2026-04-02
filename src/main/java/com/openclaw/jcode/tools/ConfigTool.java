package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 配置管理工具
 */
@Component
public class ConfigTool extends Tool {
    
    private static final Map<String, Object> CONFIG = new HashMap<>();
    static {
        CONFIG.put("workspace", System.getProperty("user.home") + "/.openclaw/workspace");
        CONFIG.put("maxReadSize", 10485760);
        CONFIG.put("allowWrite", true);
        CONFIG.put("allowExec", true);
        CONFIG.put("port", 8081);
    }
    
    public ConfigTool() {
        this.name = "config";
        this.description = "查看和修改配置";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = (String) params.getOrDefault("action", "list");
        String key = (String) params.get("key");
        Object value = params.get("value");
        
        return switch (action) {
            case "list" -> listConfig();
            case "get" -> getConfig(key);
            case "set" -> setConfig(key, value);
            default -> ToolResult.error("未知操作：" + action);
        };
    }
    
    private ToolResult listConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 配置列表 ===\n\n");
        for (Map.Entry<String, Object> entry : CONFIG.entrySet()) {
            sb.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
        }
        return ToolResult.success("配置列表", sb.toString());
    }
    
    private ToolResult getConfig(String key) {
        if (key == null) return ToolResult.error("key 参数不能为空");
        Object value = CONFIG.get(key);
        if (value == null) return ToolResult.error("配置不存在：" + key);
        return ToolResult.success("获取成功", key + ": " + value);
    }
    
    private ToolResult setConfig(String key, Object value) {
        if (key == null) return ToolResult.error("key 参数不能为空");
        CONFIG.put(key, value);
        return ToolResult.success("配置已更新", key + ": " + value);
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return true; }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 action - 操作：list, get, set (可选，默认 list)
                 key    - 配置键 (get/set 需要)
                 value  - 配置值 (set 需要)
               示例:
                 config
                 config action="get" key="port"
                 config action="set" key="port" value=8082
               """.formatted(name, description);
    }
}
