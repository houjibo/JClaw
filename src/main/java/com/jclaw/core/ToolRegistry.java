package com.jclaw.core;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册表 - 管理所有可用工具
 */
@Component
public class ToolRegistry {
    
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    
    public void register(Tool tool) {
        tools.put(tool.getName(), tool);
        System.out.println("[ToolRegistry] 注册工具：" + tool.getName() + " - " + tool.getDescription());
    }
    
    public void registerAll(Collection<Tool> toolList) {
        for (Tool tool : toolList) {
            register(tool);
        }
    }
    
    public Optional<Tool> getTool(String name) {
        return Optional.ofNullable(tools.get(name));
    }
    
    public List<Tool> listTools() {
        return new ArrayList<>(tools.values());
    }
    
    public List<Tool> listToolsByCategory(ToolCategory category) {
        return tools.values().stream()
                .filter(t -> t.getCategory() == category)
                .toList();
    }
    
    public ToolResult execute(String toolName, Map<String, Object> params, ToolContext context) {
        return getTool(toolName)
                .map(tool -> {
                    long start = System.currentTimeMillis();
                    try {
                        if (!tool.validate(params)) {
                            return ToolResult.error("参数验证失败");
                        }
                        ToolResult result = tool.execute(params, context);
                        result.setDurationMs(System.currentTimeMillis() - start);
                        return result;
                    } catch (Exception e) {
                        System.err.println("[ToolRegistry] 工具执行失败：" + toolName + " - " + e.getMessage());
                        return ToolResult.error(e.getMessage());
                    }
                })
                .orElse(ToolResult.error("未找到工具：" + toolName));
    }
    
    public int size() {
        return tools.size();
    }
}
