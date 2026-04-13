package com.jclaw.controller;

import com.jclaw.mcp.McpProtocol;
import com.jclaw.mcp.McpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpController {
    
    private final McpService mcpService;
    
    /**
     * 列出工具
     */
    @GetMapping("/tools")
    public ResponseEntity<List<McpProtocol.Tool>> listTools() {
        return ResponseEntity.ok(mcpService.listTools());
    }
    
    /**
     * 调用工具
     */
    @PostMapping("/tools/{toolName}")
    public ResponseEntity<McpProtocol.Response> callTool(
            @PathVariable String toolName,
            @RequestBody(required = false) Map<String, Object> args) {
        McpProtocol.Response response = mcpService.callTool(toolName, args != null ? args : Map.of());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 列出资源
     */
    @GetMapping("/resources")
    public ResponseEntity<List<McpProtocol.Resource>> listResources() {
        return ResponseEntity.ok(mcpService.listResources());
    }
    
    /**
     * 读取资源
     */
    @GetMapping("/resources/read")
    public ResponseEntity<Map<String, String>> readResource(@RequestParam String uri) {
        String content = mcpService.readResource(uri);
        return ResponseEntity.ok(Map.of("content", content));
    }
    
    /**
     * 列出提示词
     */
    @GetMapping("/prompts")
    public ResponseEntity<List<McpProtocol.Prompt>> listPrompts() {
        return ResponseEntity.ok(mcpService.listPrompts());
    }
}
