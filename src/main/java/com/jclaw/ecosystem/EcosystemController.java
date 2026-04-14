package com.jclaw.ecosystem;

import com.jclaw.ecosystem.EcosystemIntegrationService.ExternalSystemConfig;
import com.jclaw.ecosystem.EcosystemIntegrationService.EcosystemMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生态集成 REST API 控制器
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/ecosystem")
@RequiredArgsConstructor
public class EcosystemController {
    
    private final EcosystemIntegrationService ecosystemService;
    
    /**
     * 连接 OpenClaw
     * POST /api/ecosystem/connect/openclaw
     */
    @PostMapping("/connect/openclaw")
    public ResponseEntity<Map<String, Object>> connectToOpenClaw(
            @RequestParam String endpoint,
            @RequestParam String apiKey) {
        
        boolean success = ecosystemService.connectToOpenClaw(endpoint, apiKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("systemId", "openclaw-main");
        response.put("message", success ? "OpenClaw 连接成功" : "OpenClaw 连接失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 连接 Claude Code
     * POST /api/ecosystem/connect/claude-code
     */
    @PostMapping("/connect/claude-code")
    public ResponseEntity<Map<String, Object>> connectToClaudeCode(
            @RequestParam String endpoint,
            @RequestParam String apiKey) {
        
        boolean success = ecosystemService.connectToClaudeCode(endpoint, apiKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("systemId", "claude-code-main");
        response.put("message", success ? "Claude Code 连接成功" : "Claude Code 连接失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 列出集成的系统
     * GET /api/ecosystem/systems
     */
    @GetMapping("/systems")
    public ResponseEntity<Map<String, Object>> listIntegratedSystems() {
        List<ExternalSystemConfig> systems = ecosystemService.listIntegratedSystems()
            .stream().toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", systems.size());
        response.put("systems", systems);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 调用外部能力
     * POST /api/ecosystem/call/{systemId}/{capability}
     */
    @PostMapping("/call/{systemId}/{capability}")
    public ResponseEntity<Map<String, Object>> callExternalCapability(
            @PathVariable String systemId,
            @PathVariable String capability,
            @RequestBody(required = false) Map<String, Object> params) {
        
        try {
            Object result = ecosystemService.callExternalCapability(systemId, capability, params != null ? params : Map.of());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("systemId", systemId);
            response.put("capability", capability);
            response.put("result", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 发送生态消息
     * POST /api/ecosystem/message
     */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody EcosystemMessage message) {
        boolean success = ecosystemService.sendMessage(message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("messageId", message.getMessageId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试连接
     * GET /api/ecosystem/test/{systemId}
     */
    @GetMapping("/test/{systemId}")
    public ResponseEntity<Map<String, Object>> testConnection(@PathVariable String systemId) {
        boolean success = ecosystemService.testConnection(systemId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("systemId", systemId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 断开连接
     * DELETE /api/ecosystem/disconnect/{systemId}
     */
    @DeleteMapping("/disconnect/{systemId}")
    public ResponseEntity<Map<String, Object>> disconnectSystem(@PathVariable String systemId) {
        ecosystemService.disconnectSystem(systemId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("systemId", systemId);
        response.put("message", "已断开连接");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取消息队列状态
     * GET /api/ecosystem/queue/status
     */
    @GetMapping("/queue/status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("queueSize", ecosystemService.getMessageQueueSize());
        
        return ResponseEntity.ok(response);
    }
}
