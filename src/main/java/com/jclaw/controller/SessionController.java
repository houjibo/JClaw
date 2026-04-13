package com.jclaw.controller;

import com.jclaw.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会话 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    
    private final SessionManager sessionManager;
    
    /**
     * 创建会话
     */
    @PostMapping
    public ResponseEntity<SessionManager.Session> createSession(@RequestBody Map<String, String> request) {
        String userId = request.getOrDefault("user_id", "anonymous");
        SessionManager.Session session = sessionManager.createSession(userId);
        return ResponseEntity.ok(session);
    }
    
    /**
     * 获取会话
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionManager.Session> getSession(@PathVariable String sessionId) {
        SessionManager.Session session = sessionManager.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }
    
    /**
     * 添加消息
     */
    @PostMapping("/{sessionId}/messages")
    public ResponseEntity<Map<String, String>> addMessage(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {
        String role = request.get("role");
        String content = request.get("content");
        
        if (role == null || content == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "缺少参数：role, content"));
        }
        
        sessionManager.addMessage(sessionId, role, content);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    /**
     * 获取历史消息
     */
    @GetMapping("/{sessionId}/history")
    public ResponseEntity<List<SessionManager.Message>> getHistory(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "20") int limit) {
        List<SessionManager.Message> history = sessionManager.getHistory(sessionId, limit);
        return ResponseEntity.ok(history);
    }
    
    /**
     * 设置上下文
     */
    @PostMapping("/{sessionId}/context")
    public ResponseEntity<Map<String, String>> setContext(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {
        request.forEach((key, value) -> sessionManager.setContext(sessionId, key, value));
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    /**
     * 获取上下文
     */
    @GetMapping("/{sessionId}/context/{key}")
    public ResponseEntity<Map<String, Object>> getContext(
            @PathVariable String sessionId,
            @PathVariable String key) {
        Object value = sessionManager.getContext(sessionId, key);
        return value != null 
            ? ResponseEntity.ok(Map.of("key", key, "value", value))
            : ResponseEntity.notFound().build();
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> deleteSession(@PathVariable String sessionId) {
        sessionManager.deleteSession(sessionId);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }
    
    /**
     * 列出所有会话
     */
    @GetMapping
    public ResponseEntity<List<SessionManager.Session>> listSessions() {
        return ResponseEntity.ok(sessionManager.listSessions());
    }
}
