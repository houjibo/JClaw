package com.openclaw.jcode.controller;

import com.openclaw.jcode.core.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 工具 REST API 控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ToolController {
    
    private final ToolRegistry toolRegistry;
    
    public ToolController(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }
    
    /**
     * 列出所有可用工具
     */
    @GetMapping("/tools")
    public Map<String, Object> listTools() {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        for (Tool tool : toolRegistry.listTools()) {
            Map<String, Object> toolInfo = new HashMap<>();
            toolInfo.put("name", tool.getName());
            toolInfo.put("description", tool.getDescription());
            toolInfo.put("category", tool.getCategory().name());
            toolInfo.put("requiresConfirmation", tool.isRequiresConfirmation());
            tools.add(toolInfo);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", tools.size());
        response.put("tools", tools);
        
        return response;
    }
    
    /**
     * 获取单个工具详情
     */
    @GetMapping("/tools/{toolName}")
    public Map<String, Object> getTool(@PathVariable String toolName) {
        return toolRegistry.getTool(toolName)
                .map(tool -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("tool", Map.of(
                            "name", tool.getName(),
                            "description", tool.getDescription(),
                            "category", tool.getCategory().name(),
                            "requiresConfirmation", tool.isRequiresConfirmation(),
                            "help", tool.getHelp()
                    ));
                    return response;
                })
                .orElse(Map.of("success", false, "error", "工具不存在：" + toolName));
    }
    
    /**
     * 执行工具
     */
    @PostMapping("/tools/{toolName}/execute")
    public Map<String, Object> executeTool(
            @PathVariable String toolName,
            @RequestBody(required = false) Map<String, Object> params) {
        
        System.out.println("[API] 执行工具：" + toolName + " 参数：" + params);
        
        ToolContext context = ToolContext.defaultContext();
        ToolResult result = toolRegistry.execute(toolName, params != null ? params : new HashMap<>(), context);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        response.put("durationMs", result.getDurationMs());
        
        if (result.isSuccess()) {
            response.put("output", result.getOutput());
            response.put("data", result.getData());
        } else {
            response.put("error", result.getError());
        }
        
        return response;
    }
}
