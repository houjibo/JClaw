package com.jclaw.controller;

import com.jclaw.services.McpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP 服务控制器
 * 
 * 提供以下端点：
 * - GET /api/mcp/servers - 列出所有 MCP 服务器
 * - POST /api/mcp/servers - 注册 MCP 服务器
 * - GET /api/mcp/servers/{name}/tools - 列出工具
 * - GET /api/mcp/servers/{name}/resources - 列出资源
 * - GET /api/mcp/servers/{name}/prompts - 列出提示词
 * - POST /api/mcp/servers/{name}/tools/{toolName}/call - 调用工具
 * - GET /api/mcp/servers/{name}/resources/read - 读取资源
 * - POST /api/mcp/servers/{name}/prompts/{promptName}/get - 获取提示词
 */
@RestController
@RequestMapping("/api/mcp")
public class McpController {
    
    private static final Logger logger = LoggerFactory.getLogger(McpController.class);
    
    private final McpService mcpService;
    
    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }
    
    /**
     * 列出所有 MCP 服务器
     */
    @GetMapping("/servers")
    public List<Map<String, Object>> listServers() {
        logger.info("列出 MCP 服务器");
        return mcpService.listServers();
    }
    
    /**
     * 注册 MCP 服务器
     */
    @PostMapping("/servers")
    public Map<String, Object> registerServer(@RequestBody Map<String, String> config) {
        String name = config.get("name");
        String url = config.get("url");
        
        if (name == null || url == null) {
            return Map.of(
                "success", false,
                "error", "name 和 url 不能为空"
            );
        }
        
        logger.info("注册 MCP 服务器：{} -> {}", name, url);
        mcpService.registerServer(name, url);
        
        return Map.of(
            "success", true,
            "name", name,
            "url", url
        );
    }
    
    /**
     * 列出指定服务器的工具
     */
    @GetMapping("/servers/{serverName}/tools")
    public List<Map<String, Object>> listTools(@PathVariable String serverName) {
        logger.info("列出 MCP 工具：{}", serverName);
        return mcpService.listTools(serverName);
    }
    
    /**
     * 列出指定服务器的资源
     */
    @GetMapping("/servers/{serverName}/resources")
    public List<Map<String, Object>> listResources(@PathVariable String serverName) {
        logger.info("列出 MCP 资源：{}", serverName);
        return mcpService.listResources(serverName);
    }
    
    /**
     * 列出指定服务器的提示词
     */
    @GetMapping("/servers/{serverName}/prompts")
    public List<Map<String, Object>> listPrompts(@PathVariable String serverName) {
        logger.info("列出 MCP 提示词：{}", serverName);
        return mcpService.listPrompts(serverName);
    }
    
    /**
     * 调用 MCP 工具
     */
    @PostMapping("/servers/{serverName}/tools/{toolName}/call")
    public Map<String, Object> callTool(
            @PathVariable String serverName,
            @PathVariable String toolName,
            @RequestBody(required = false) Map<String, Object> args) {
        
        logger.info("调用 MCP 工具：{}.{}", serverName, toolName);
        
        try {
            Map<String, Object> result = mcpService.callTool(serverName, toolName, args != null ? args : new java.util.HashMap<>());
            return Map.of(
                "success", true,
                "server", serverName,
                "tool", toolName,
                "result", result
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
    
    /**
     * 读取 MCP 资源
     */
    @GetMapping("/servers/{serverName}/resources/read")
    public Map<String, Object> readResource(
            @PathVariable String serverName,
            @RequestParam String uri) {
        
        logger.info("读取 MCP 资源：{} -> {}", serverName, uri);
        
        try {
            String content = mcpService.readResource(serverName, uri);
            return Map.of(
                "success", true,
                "server", serverName,
                "uri", uri,
                "content", content
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
    
    /**
     * 获取 MCP 提示词
     */
    @PostMapping("/servers/{serverName}/prompts/{promptName}/get")
    public Map<String, Object> getPrompt(
            @PathVariable String serverName,
            @PathVariable String promptName,
            @RequestBody(required = false) Map<String, Object> args) {
        
        logger.info("获取 MCP 提示词：{}.{}", serverName, promptName);
        
        try {
            Map<String, Object> result = mcpService.getPrompt(serverName, promptName, args != null ? args : new java.util.HashMap<>());
            return Map.of(
                "success", true,
                "server", serverName,
                "prompt", promptName,
                "result", result
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
    
    /**
     * 启用/禁用 MCP 服务器
     */
    @PostMapping("/servers/{serverName}/toggle")
    public Map<String, Object> toggleServer(
            @PathVariable String serverName,
            @RequestParam boolean enabled) {
        
        logger.info("切换 MCP 服务器状态：{} -> {}", serverName, enabled);
        mcpService.setServerEnabled(serverName, enabled);
        
        return Map.of(
            "success", true,
            "server", serverName,
            "enabled", enabled
        );
    }
    
    /**
     * 移除 MCP 服务器
     */
    @DeleteMapping("/servers/{serverName}")
    public Map<String, Object> removeServer(@PathVariable String serverName) {
        logger.info("移除 MCP 服务器：{}", serverName);
        mcpService.removeServer(serverName);
        
        return Map.of(
            "success", true,
            "server", serverName
        );
    }
}
