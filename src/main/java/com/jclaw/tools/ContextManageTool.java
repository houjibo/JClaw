package com.jclaw.tools;

import com.jclaw.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context Manage 工具 - 上下文管理
 * 
 * 功能：
 * - 设置上下文变量
 * - 获取上下文变量
 * - 删除上下文变量
 * - 列出所有上下文
 * - 清空上下文
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Component
public class ContextManageTool extends Tool {
    
    private static final Map<String, Object> CONTEXT = new ConcurrentHashMap<>();
    
    public ContextManageTool() {
        this.name = "context_manage";
        this.description = "上下文管理，支持设置/获取/删除上下文变量";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = (String) params.get("action");
        
        if (action == null || action.isBlank()) {
            return ToolResult.error("action 参数不能为空 (set/get/delete/list/clear)");
        }
        
        return switch (action.toLowerCase()) {
            case "set" -> setContext(params);
            case "get" -> getContext(params);
            case "delete" -> deleteContext(params);
            case "list" -> listContext();
            case "clear" -> clearContext();
            default -> ToolResult.error("不支持的操作：" + action);
        };
    }
    
    private ToolResult setContext(Map<String, Object> params) {
        String key = (String) params.get("key");
        Object value = params.get("value");
        
        if (key == null || key.isBlank()) {
            return ToolResult.error("key 参数不能为空");
        }
        
        CONTEXT.put(key, value);
        log.info("设置上下文：{} = {}", key, value);
        
        return ToolResult.success(String.format("已设置上下文：%s", key));
    }
    
    private ToolResult getContext(Map<String, Object> params) {
        String key = (String) params.get("key");
        
        if (key == null || key.isBlank()) {
            return ToolResult.error("key 参数不能为空");
        }
        
        Object value = CONTEXT.get(key);
        if (value == null) {
            return ToolResult.error(String.format("上下文变量不存在：%s", key));
        }
        
        log.info("获取上下文：{} = {}", key, value);
        
        ToolResult result = ToolResult.success(String.format("获取上下文：%s", key));
        result.setData(Map.of("key", key, "value", value));
        return result;
    }
    
    private ToolResult deleteContext(Map<String, Object> params) {
        String key = (String) params.get("key");
        
        if (key == null || key.isBlank()) {
            return ToolResult.error("key 参数不能为空");
        }
        
        Object removed = CONTEXT.remove(key);
        if (removed == null) {
            return ToolResult.error(String.format("上下文变量不存在：%s", key));
        }
        
        log.info("删除上下文：{} = {}", key, removed);
        
        return ToolResult.success(String.format("已删除上下文：%s", key));
    }
    
    private ToolResult listContext() {
        int size = CONTEXT.size();
        log.info("列出上下文，共 {} 个变量", size);
        
        ToolResult result = ToolResult.success(String.format("上下文变量数：%d", size));
        result.setData(Map.of("count", size, "keys", new ArrayList<>(CONTEXT.keySet())));
        return result;
    }
    
    private ToolResult clearContext() {
        int size = CONTEXT.size();
        CONTEXT.clear();
        log.info("清空上下文，共清除 {} 个变量", size);
        
        return ToolResult.success(String.format("已清空上下文，清除 %d 个变量", size));
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("action");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 action - 操作类型：set/get/delete/list/clear (必填)
                 key - 变量键名 (set/get/delete 时必填)
                 value - 变量值 (set 时必填)
               示例:
                 context_manage action=set key=user value=alice
                 context_manage action=get key=user
                 context_manage action=list
                 context_manage action=clear
               """.formatted(name, description);
    }
}
