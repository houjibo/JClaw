package com.jclaw.agent.controller;

import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.service.SubagentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Subagent 管理 REST API 控制器
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/subagents")
@RequiredArgsConstructor
public class SubagentController {
    
    private final SubagentService subagentService;
    
    /**
     * 创建 Subagent
     * POST /api/subagents
     */
    @PostMapping
    public ResponseEntity<Subagent> createSubagent(
            @RequestParam String parentAgentId,
            @RequestParam String role,
            @RequestParam String task) {
        Subagent subagent = subagentService.createSubagent(parentAgentId, role, task);
        return ResponseEntity.ok(subagent);
    }
    
    /**
     * 获取 Subagent 详情
     * GET /api/subagents/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Subagent> getSubagent(@PathVariable String id) {
        Subagent subagent = subagentService.getSubagent(id);
        if (subagent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subagent);
    }
    
    /**
     * 列出 Subagents
     * GET /api/subagents?parentAgentId=xxx
     */
    @GetMapping
    public ResponseEntity<List<Subagent>> listSubagents(
            @RequestParam(required = false) String parentAgentId) {
        List<Subagent> subagents = subagentService.listSubagents(parentAgentId);
        return ResponseEntity.ok(subagents);
    }
    
    /**
     * 更新 Subagent 状态
     * POST /api/subagents/{id}/status
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        subagentService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 提交 Subagent 结果
     * POST /api/subagents/{id}/result
     */
    @PostMapping("/{id}/result")
    public ResponseEntity<Void> submitResult(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String output = body.get("output");
        if (output != null) {
            subagentService.submitResult(id, output);
        }
        return ResponseEntity.ok().build();
    }
    
    /**
     * 等待 Subagent 完成
     * GET /api/subagents/{id}/wait?timeout=5000
     */
    @GetMapping("/{id}/wait")
    public ResponseEntity<Subagent> waitForCompletion(
            @PathVariable String id,
            @RequestParam(defaultValue = "5000") long timeout) {
        Subagent subagent = subagentService.waitForCompletion(id, timeout);
        if (subagent == null) {
            return ResponseEntity.ok().build(); // 超时返回空
        }
        return ResponseEntity.ok(subagent);
    }
    
    /**
     * 批量创建 Subagents（联军模式）
     * POST /api/subagents/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createBatchSubagents(
            @RequestParam String parentAgentId,
            @RequestBody List<Map<String, String>> tasks) {
        
        List<Subagent> subagents = tasks.stream()
            .map(task -> subagentService.createSubagent(
                parentAgentId,
                task.get("role"),
                task.get("task")
            ))
            .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", subagents.size());
        response.put("subagents", subagents);
        
        return ResponseEntity.ok(response);
    }
}
