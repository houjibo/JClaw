package com.jclaw.controller;

import com.jclaw.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 命令控制器 - 提供命令执行的 REST API
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@RestController
@RequestMapping("/api/commands")
public class CommandController {
    
    @Autowired
    private CommandRegistry commandRegistry;
    
    /**
     * 列出所有可用命令
     */
    @GetMapping
    public Map<String, Object> listCommands(
            @RequestParam(required = false) String category) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (category != null && !category.isBlank()) {
            try {
                Command.CommandCategory cat = Command.CommandCategory.valueOf(category.toUpperCase());
                result.put("commands", commandRegistry.listCommandsByCategory(cat));
            } catch (IllegalArgumentException e) {
                result.put("error", "无效的类别：" + category);
                result.put("total", 0);
                result.put("stats", commandRegistry.getStats());
                return result;
            }
        } else {
            result.put("commands", commandRegistry.listCommands());
        }
        
        List<?> commands = (List<?>) result.get("commands");
        result.put("total", commands != null ? commands.size() : 0);
        result.put("stats", commandRegistry.getStats());
        
        return result;
    }
    
    /**
     * 获取命令详情
     */
    @GetMapping("/{name}")
    public Map<String, Object> getCommand(@PathVariable String name) {
        Command cmd = commandRegistry.getCommand(name);
        
        if (cmd == null) {
            return Map.of("error", "命令不存在：" + name);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("name", cmd.getName());
        result.put("description", cmd.getDescription());
        result.put("aliases", cmd.getAliases());
        result.put("category", cmd.getCategory().name());
        result.put("requiresConfirmation", cmd.isRequiresConfirmation());
        result.put("help", cmd.getHelp());
        
        return result;
    }
    
    /**
     * 执行命令
     */
    @PostMapping("/{name}/execute")
    public Map<String, Object> executeCommand(
            @PathVariable String name,
            @RequestBody(required = false) Map<String, Object> params) {
        
        Command cmd = commandRegistry.getCommand(name);
        
        if (cmd == null) {
            return Map.of(
                "success", false,
                "error", "命令不存在：" + name
            );
        }
        
        // 检查是否需要确认
        if (cmd.isRequiresConfirmation()) {
            return Map.of(
                "success", false,
                "requiresConfirmation", true,
                "message", "此命令需要确认"
            );
        }
        
        try {
            // 构建命令参数字符串
            String args = "";
            if (params != null && params.containsKey("args")) {
                args = (String) params.get("args");
            }
            
            // 创建命令上下文（简化版）
            com.jclaw.core.ToolContext toolContext = com.jclaw.core.ToolContext.defaultContext();
            CommandContext context = new CommandContext(
                toolContext,
                "user",
                "session-" + System.currentTimeMillis(),
                true
            );
            
            // 执行命令
            CommandResult result = cmd.execute(args, context);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getType() == CommandResult.ResultType.SUCCESS);
            response.put("type", result.getType().name());
            response.put("message", result.getMessage());
            response.put("data", result.getData());
            response.put("displayText", result.getDisplayText());
            
            return response;
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", "命令执行失败：" + e.getMessage()
            );
        }
    }
    
    /**
     * 获取命令自动完成建议
     */
    @GetMapping("/autocomplete")
    public Set<String> autocomplete(@RequestParam String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return Collections.emptySet();
        }
        
        Set<String> suggestions = new TreeSet<>();
        for (String name : commandRegistry.getCommandNames()) {
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(name);
            }
        }
        return suggestions;
    }
    
    /**
     * 启用/禁用命令
     */
    @PostMapping("/{name}/toggle")
    public Map<String, Object> toggleCommand(
            @PathVariable String name,
            @RequestParam boolean enabled) {
        
        String resolvedName = commandRegistry.resolveCommandName(name);
        if (resolvedName == null) {
            return Map.of("error", "命令不存在：" + name);
        }
        
        commandRegistry.setCommandEnabled(resolvedName, enabled);
        
        return Map.of(
            "success", true,
            "name", resolvedName,
            "enabled", enabled
        );
    }
}
