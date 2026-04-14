package com.jclaw.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.jclaw.mcp.McpProtocol.*;

/**
 * MCP 服务 - 深度集成 Model Context Protocol
 * 
 * 功能：
 * - MCP 服务器管理
 * - 远程工具调用
 * - 资源管理
 * - 提示词 (Prompts) 支持
 */
@Service
public class McpService {
    
    private static final Logger logger = LoggerFactory.getLogger(McpService.class);
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    /**
     * MCP 服务器连接池
     */
    private final ConcurrentHashMap<String, McpServer> servers = new ConcurrentHashMap<>();
    
    /**
     * MCP 服务器配置
     */
    public static class McpServer {
        public String name;
        public String url;
        public boolean enabled;
        public List<Map<String, Object>> tools;
        public List<Map<String, Object>> resources;
        public List<Map<String, Object>> prompts;
        public long lastConnected;
        
        public McpServer(String name, String url) {
            this.name = name;
            this.url = url;
            this.enabled = true;
            this.tools = new ArrayList<>();
            this.resources = new ArrayList<>();
            this.prompts = new ArrayList<>();
            this.lastConnected = System.currentTimeMillis();
        }
    }
    
    public McpService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 注册 MCP 服务器
     */
    public void registerServer(String name, String url) {
        logger.info("注册 MCP 服务器：{} -> {}", name, url);
        McpServer server = new McpServer(name, url);
        servers.put(name, server);
        
        // 自动发现工具、资源、提示词
        discoverCapabilities(server);
    }
    
    /**
     * 发现 MCP 服务器能力
     */
    private void discoverCapabilities(McpServer server) {
        try {
            // 获取工具列表
            List<Map<String, Object>> tools = callMethod(server.url, "tools/list", new HashMap<>());
            if (tools != null && tools.get(0) instanceof Map) {
                Map<?, ?> result = (Map<?, ?>) ((Map<?, ?>) tools.get(0)).get("result");
                if (result != null && result.containsKey("tools")) {
                    server.tools = (List<Map<String, Object>>) result.get("tools");
                    logger.info("发现 {} 个工具：{}", server.name, server.tools.size());
                }
            }
            
            // 获取资源列表
            List<Map<String, Object>> resources = callMethod(server.url, "resources/list", new HashMap<>());
            if (resources != null && resources.get(0) instanceof Map) {
                Map<?, ?> result = (Map<?, ?>) ((Map<?, ?>) resources.get(0)).get("result");
                if (result != null && result.containsKey("resources")) {
                    server.resources = (List<Map<String, Object>>) result.get("resources");
                    logger.info("发现 {} 个资源：{}", server.name, server.resources.size());
                }
            }
            
            // 获取提示词列表
            List<Map<String, Object>> prompts = callMethod(server.url, "prompts/list", new HashMap<>());
            if (prompts != null && prompts.get(0) instanceof Map) {
                Map<?, ?> result = (Map<?, ?>) ((Map<?, ?>) prompts.get(0)).get("result");
                if (result != null && result.containsKey("prompts")) {
                    server.prompts = (List<Map<String, Object>>) result.get("prompts");
                    logger.info("发现 {} 个提示词：{}", server.name, server.prompts.size());
                }
            }
            
            server.lastConnected = System.currentTimeMillis();
            
        } catch (Exception e) {
            logger.error("发现 MCP 服务器能力失败：{}", server.name, e);
        }
    }
    
    /**
     * 调用远程 MCP 工具
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> callTool(String serverName, String toolName, Map<String, Object> args) {
        McpServer server = servers.get(serverName);
        if (server == null) {
            throw new RuntimeException("MCP 服务器不存在：" + serverName);
        }
        
        if (!server.enabled) {
            throw new RuntimeException("MCP 服务器已禁用：" + serverName);
        }
        
        logger.info("调用 MCP 工具：{}.{}", serverName, toolName);
        
        Map<String, Object> params = new HashMap<>();
        params.put("name", toolName);
        params.put("arguments", args);
        
        List<Map<String, Object>> result = callMethod(server.url, "tools/call", params);
        
        if (result != null && !result.isEmpty()) {
            Map<?, ?> response = (Map<?, ?>) result.get(0);
            if (response.containsKey("result")) {
                return (Map<String, Object>) response.get("result");
            } else if (response.containsKey("error")) {
                throw new RuntimeException("MCP 工具调用失败：" + response.get("error"));
            }
        }
        
        return new HashMap<>();
    }
    
    /**
     * 读取 MCP 资源
     */
    @SuppressWarnings("unchecked")
    public String readResource(String serverName, String resourceUri) {
        McpServer server = servers.get(serverName);
        if (server == null) {
            throw new RuntimeException("MCP 服务器不存在：" + serverName);
        }
        
        logger.info("读取 MCP 资源：{} -> {}", serverName, resourceUri);
        
        Map<String, Object> params = new HashMap<>();
        params.put("uri", resourceUri);
        
        List<Map<String, Object>> result = callMethod(server.url, "resources/read", params);
        
        if (result != null && !result.isEmpty()) {
            Map<?, ?> response = (Map<?, ?>) result.get(0);
            if (response.containsKey("result")) {
                Map<?, ?> resResult = (Map<?, ?>) ((Map<?, ?>) response.get("result")).get("contents");
                if (resResult != null && resResult instanceof List) {
                    List<?> contents = (List<?>) resResult;
                    if (!contents.isEmpty()) {
                        return (String) ((Map<?, ?>) contents.get(0)).get("text");
                    }
                }
            }
        }
        
        return "";
    }
    
    /**
     * 获取提示词
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPrompt(String serverName, String promptName, Map<String, Object> args) {
        McpServer server = servers.get(serverName);
        if (server == null) {
            throw new RuntimeException("MCP 服务器不存在：" + serverName);
        }
        
        logger.info("获取 MCP 提示词：{}.{}", serverName, promptName);
        
        Map<String, Object> params = new HashMap<>();
        params.put("name", promptName);
        if (args != null) {
            params.put("arguments", args);
        }
        
        List<Map<String, Object>> result = callMethod(server.url, "prompts/get", params);
        
        if (result != null && !result.isEmpty()) {
            Map<?, ?> response = (Map<?, ?>) result.get(0);
            if (response.containsKey("result")) {
                return (Map<String, Object>) response.get("result");
            }
        }
        
        return new HashMap<>();
    }
    
    /**
     * 列出所有已注册的 MCP 服务器
     */
    public List<Map<String, Object>> listServers() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (McpServer server : servers.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("name", server.name);
            info.put("url", server.url);
            info.put("enabled", server.enabled);
            info.put("toolsCount", server.tools.size());
            info.put("resourcesCount", server.resources.size());
            info.put("promptsCount", server.prompts.size());
            info.put("lastConnected", server.lastConnected);
            list.add(info);
        }
        return list;
    }
    
    /**
     * 列出所有工具（不区分服务器）
     */
    public List<Tool> listTools() {
        List<Tool> allTools = new ArrayList<>();
        for (McpServer server : servers.values()) {
            if (server.enabled && server.tools != null) {
                for (Map<String, Object> toolMap : server.tools) {
                    Tool tool = new Tool();
                    tool.setName((String) toolMap.get("name"));
                    tool.setDescription((String) toolMap.get("description"));
                    allTools.add(tool);
                }
            }
        }
        return allTools;
    }
    
    /**
     * 列出所有资源（不区分服务器）
     */
    public List<Resource> listResources() {
        List<Resource> allResources = new ArrayList<>();
        for (McpServer server : servers.values()) {
            if (server.enabled && server.resources != null) {
                for (Map<String, Object> resMap : server.resources) {
                    Resource resource = new Resource();
                    resource.setUri((String) resMap.get("uri"));
                    resource.setName((String) resMap.get("name"));
                    resource.setDescription((String) resMap.get("description"));
                    resource.setMimeType((String) resMap.get("mimeType"));
                    allResources.add(resource);
                }
            }
        }
        return allResources;
    }
    
    /**
     * 列出所有提示词（不区分服务器）
     */
    public List<Prompt> listPrompts() {
        List<Prompt> allPrompts = new ArrayList<>();
        for (McpServer server : servers.values()) {
            if (server.enabled && server.prompts != null) {
                for (Map<String, Object> promptMap : server.prompts) {
                    Prompt prompt = new Prompt();
                    prompt.setName((String) promptMap.get("name"));
                    prompt.setDescription((String) promptMap.get("description"));
                    allPrompts.add(prompt);
                }
            }
        }
        return allPrompts;
    }
    
    /**
     * 调用工具
     */
    public Response callTool(String toolName, Map<String, Object> args) {
        // 遍历所有服务器查找工具
        for (McpServer server : servers.values()) {
            if (server.enabled && server.tools != null) {
                for (Map<String, Object> toolMap : server.tools) {
                    String name = (String) toolMap.get("name");
                    if (name != null && name.equals(toolName)) {
                        try {
                            Map<String, Object> result = callTool(server.name, toolName, args);
                            Response response = new Response();
                            response.setResult((Map<String, Object>) result);
                            return response;
                        } catch (Exception e) {
                            Response response = new Response();
                            Response.Error error = new Response.Error();
                            error.setCode(-1);
                            error.setMessage(e.getMessage());
                            response.setError(error);
                            return response;
                        }
                    }
                }
            }
        }
        
        // 工具未找到
        Response response = new Response();
        Response.Error error = new Response.Error();
        error.setCode(-32601);
        error.setMessage("工具未找到：" + toolName);
        response.setError(error);
        return response;
    }
    
    /**
     * 读取资源
     */
    public String readResource(String uri) {
        logger.info("读取 MCP 资源：{}", uri);
        
        // 支持本地文件资源
        if (uri.startsWith("file://")) {
            try {
                String filePath = uri.substring(7); // 移除 file:// 前缀
                java.nio.file.Path path = java.nio.file.Paths.get(filePath);
                if (java.nio.file.Files.exists(path)) {
                    String content = java.nio.file.Files.readString(path);
                    logger.info("本地文件读取成功：{} bytes", content.length());
                    return content;
                } else {
                    logger.warn("本地文件不存在：{}", filePath);
                    return "";
                }
            } catch (Exception e) {
                logger.error("读取本地文件失败：{}", uri, e);
                return "";
            }
        }
        
        // 遍历所有服务器查找远程资源
        for (McpServer server : servers.values()) {
            if (server.enabled && server.resources != null) {
                for (Map<String, Object> resMap : server.resources) {
                    String resUri = (String) resMap.get("uri");
                    if (resUri != null && resUri.equals(uri)) {
                        return readResource(server.name, uri);
                    }
                }
            }
        }
        
        logger.warn("资源未找到：{}", uri);
        return "";
    }
    
    /**
     * 启用/禁用服务器
     */
    public void setServerEnabled(String serverName, boolean enabled) {
        McpServer server = servers.get(serverName);
        if (server != null) {
            server.enabled = enabled;
            logger.info("MCP 服务器 {} 已{}", serverName, enabled ? "启用" : "禁用");
        }
    }
    
    /**
     * 移除服务器
     */
    public void removeServer(String serverName) {
        servers.remove(serverName);
        logger.info("MCP 服务器已移除：{}", serverName);
    }
    
    /**
     * 调用 MCP JSON-RPC 方法
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> callMethod(String url, String method, Map<String, Object> params) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("jsonrpc", "2.0");
            request.put("id", System.currentTimeMillis());
            request.put("method", method);
            request.put("params", params != null ? params : new HashMap<>());
            
            String requestBody = objectMapper.writeValueAsString(request);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP 错误：" + response.statusCode());
            }
            
            return objectMapper.readValue(response.body(), List.class);
            
        } catch (Exception e) {
            logger.error("MCP 调用失败：{} {}", url, method, e);
            throw new RuntimeException("MCP 调用失败：" + e.getMessage(), e);
        }
    }
}
