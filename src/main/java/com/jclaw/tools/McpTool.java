package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 */
@Component
public class McpTool extends Tool {
    
    private final HttpClient httpClient;
    
    public McpTool() {
        this.name = "mcp";
        this.description = "MCP 协议调用，支持远程工具和资源";
        this.category = ToolCategory.MCP;
        this.requiresConfirmation = true;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = (String) params.get("action");
        String serverUrl = (String) params.get("serverUrl");
        String toolName = (String) params.get("toolName");
        Map<String, Object> toolArgs = getMapParam(params.get("toolArgs"));
        
        if (action == null || action.isBlank()) {
            return ToolResult.error("action 参数不能为空");
        }
        
        try {
            return switch (action.toLowerCase()) {
                case "call" -> callTool(serverUrl, toolName, toolArgs);
                case "list" -> listTools(serverUrl);
                case "resources" -> listResources(serverUrl);
                default -> ToolResult.error("不支持的 MCP 操作：" + action);
            };
        } catch (Exception e) {
            System.err.println("[McpTool] MCP 操作失败：" + action + " - " + e.getMessage());
            return ToolResult.error("MCP 操作失败：" + e.getMessage());
        }
    }
    
    private ToolResult callTool(String serverUrl, String toolName, Map<String, Object> args) throws Exception {
        if (serverUrl == null || toolName == null) {
            return ToolResult.error("serverUrl 和 toolName 不能为空");
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("jsonrpc", "2.0");
        request.put("id", System.currentTimeMillis());
        request.put("method", "tools/call");
        
        Map<String, Object> methodParams = new HashMap<>();
        methodParams.put("name", toolName);
        methodParams.put("arguments", args);
        request.put("params", methodParams);
        
        String response = sendRequest(serverUrl, request);
        return ToolResult.success("MCP 工具调用成功", response);
    }
    
    private ToolResult listTools(String serverUrl) throws Exception {
        if (serverUrl == null) {
            return ToolResult.error("serverUrl 不能为空");
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("jsonrpc", "2.0");
        request.put("id", System.currentTimeMillis());
        request.put("method", "tools/list");
        request.put("params", new HashMap<>());
        
        String response = sendRequest(serverUrl, request);
        return ToolResult.success("MCP 工具列表查询成功", response);
    }
    
    private ToolResult listResources(String serverUrl) throws Exception {
        if (serverUrl == null) {
            return ToolResult.error("serverUrl 不能为空");
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("jsonrpc", "2.0");
        request.put("id", System.currentTimeMillis());
        request.put("method", "resources/list");
        request.put("params", new HashMap<>());
        
        String response = sendRequest(serverUrl, request);
        return ToolResult.success("MCP 资源列表查询成功", response);
    }
    
    private String sendRequest(String serverUrl, Map<String, Object> request) throws Exception {
        String json = toJson(request);
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new Exception("HTTP " + response.statusCode() + ": " + response.body());
        }
        
        return response.body();
    }
    
    private String toJson(Object obj) {
        // 简化实现，实际应使用 Jackson
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof String str) {
                    sb.append("\"").append(str).append("\"");
                } else if (entry.getValue() instanceof Map) {
                    sb.append(toJson(entry.getValue()));
                } else {
                    sb.append(entry.getValue());
                }
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        return obj.toString();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapParam(Object param) {
        if (param instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return new HashMap<>();
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("action");
    }
    
    static class McpResponse {
        private String jsonrpc;
        private String id;
        private McpResult result;
        private McpError error;
        
        public String getJsonrpc() { return jsonrpc; }
        public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public McpResult getResult() { return result; }
        public void setResult(McpResult result) { this.result = result; }
        public McpError getError() { return error; }
        public void setError(McpError error) { this.error = error; }
        
        static class McpResult {
            private List<ToolInfo> tools;
            private List<ResourceInfo> resources;
            private String content;
            
            public List<ToolInfo> getTools() { return tools; }
            public void setTools(List<ToolInfo> tools) { this.tools = tools; }
            public List<ResourceInfo> getResources() { return resources; }
            public void setResources(List<ResourceInfo> resources) { this.resources = resources; }
            public String getContent() { return content; }
            public void setContent(String content) { this.content = content; }
        }
        
        static class ToolInfo {
            private String name;
            private String description;
            
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
        }
        
        static class ResourceInfo {
            private String uri;
            private String name;
            private String description;
            
            public String getUri() { return uri; }
            public void setUri(String uri) { this.uri = uri; }
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
        }
        
        static class McpError {
            private int code;
            private String message;
            
            public int getCode() { return code; }
            public void setCode(int code) { this.code = code; }
            public String getMessage() { return message; }
            public void setMessage(String message) { this.message = message; }
        }
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 action    - MCP 操作：call, list, resources (必填)
                 serverUrl - MCP 服务器 URL (必填)
                 toolName  - 工具名称 (call 操作需要)
                 toolArgs  - 工具参数 (call 操作需要)
               示例:
                 mcp action="list" serverUrl="http://localhost:3000/mcp"
                 mcp action="call" serverUrl="http://localhost:3000/mcp" toolName="search"
               """.formatted(name, description);
    }
}
