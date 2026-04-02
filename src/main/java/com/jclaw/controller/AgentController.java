package com.jclaw.controller;

import com.jclaw.services.AgentCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Agent 协调器控制器
 * 
 * 提供以下端点：
 * - GET /api/agents - 列出所有 Agent
 * - POST /api/agents - 创建 Agent
 * - DELETE /api/agents/{id} - 删除 Agent
 * - POST /api/tasks - 创建任务
 * - POST /api/tasks/{id}/assign - 分配任务
 * - GET /api/tasks/{id} - 获取任务状态
 * - GET /api/stats - 获取统计信息
 */
@RestController
@RequestMapping("/api/agents")
public class AgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    private final AgentCoordinator coordinator;
    
    public AgentController(AgentCoordinator coordinator) {
        this.coordinator = coordinator;
    }
    
    /**
     * 列出所有 Agent
     */
    @GetMapping
    public List<Map<String, Object>> listAgents() {
        logger.info("列出所有 Agent");
        return coordinator.listAgents();
    }
    
    /**
     * 创建 Agent
     */
    @PostMapping
    public Map<String, Object> createAgent(@RequestBody Map<String, String> config) {
        String role = config.get("role");
        String model = config.get("model");
        
        if (role == null) {
            return Map.of("success", false, "error", "role 不能为空");
        }
        
        logger.info("创建 Agent：role={}, model={}", role, model);
        AgentCoordinator.AgentInstance agent = coordinator.createAgent(role, model != null ? model : "default");
        
        return Map.of(
            "success", true,
            "agent", Map.of(
                "id", agent.id,
                "role", agent.role,
                "model", agent.model,
                "status", agent.status
            )
        );
    }
    
    /**
     * 删除 Agent
     */
    @DeleteMapping("/{agentId}")
    public Map<String, Object> deleteAgent(@PathVariable String agentId) {
        logger.info("删除 Agent：{}", agentId);
        boolean deleted = coordinator.deleteAgent(agentId);
        
        return Map.of(
            "success", deleted,
            "agentId", agentId
        );
    }
    
    /**
     * 创建任务
     */
    @PostMapping("/tasks")
    public Map<String, Object> createTask(@RequestBody Map<String, String> config) {
        String description = config.get("description");
        
        if (description == null) {
            return Map.of("success", false, "error", "description 不能为空");
        }
        
        logger.info("创建任务：{}", description);
        AgentCoordinator.TaskInstance task = coordinator.createTask(description);
        
        return Map.of(
            "success", true,
            "task", Map.of(
                "id", task.id,
                "description", task.description,
                "status", task.status
            )
        );
    }
    
    /**
     * 分配任务
     */
    @PostMapping("/tasks/{taskId}/assign")
    public Map<String, Object> assignTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, String> config) {
        
        String agentId = config != null ? config.get("agentId") : null;
        String role = config != null ? config.get("role") : null;
        
        logger.info("分配任务：{}, agentId={}, role={}", taskId, agentId, role);
        
        String assignedAgent;
        if (agentId != null) {
            // 分配给指定 Agent
            boolean success = coordinator.assignTask(taskId, agentId);
            assignedAgent = success ? agentId : null;
        } else {
            // 自动分配
            assignedAgent = coordinator.autoAssignTask(taskId, role);
        }
        
        if (assignedAgent != null) {
            return Map.of(
                "success", true,
                "taskId", taskId,
                "assignedTo", assignedAgent
            );
        } else {
            return Map.of(
                "success", false,
                "error", "没有可用的 Agent"
            );
        }
    }
    
    /**
     * 获取任务状态
     */
    @GetMapping("/tasks/{taskId}")
    public Map<String, Object> getTaskStatus(@PathVariable String taskId) {
        logger.info("获取任务状态：{}", taskId);
        Map<String, Object> status = coordinator.getTaskStatus(taskId);
        
        if (status != null) {
            return Map.of(
                "success", true,
                "task", status
            );
        } else {
            return Map.of(
                "success", false,
                "error", "任务不存在"
            );
        }
    }
    
    /**
     * 获取任务结果
     */
    @GetMapping("/tasks/{taskId}/result")
    public Map<String, Object> getTaskResult(@PathVariable String taskId) {
        logger.info("获取任务结果：{}", taskId);
        String result = coordinator.getTaskResult(taskId);
        
        if (result != null) {
            return Map.of(
                "success", true,
                "result", result
            );
        } else {
            return Map.of(
                "success", false,
                "error", "任务结果不存在"
            );
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        logger.info("获取统计信息");
        return coordinator.getStats();
    }
    
    /**
     * 发送 Agent 间消息
     */
    @PostMapping("/message")
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> config) {
        String from = config.get("from");
        String to = config.get("to");
        String message = config.get("message");
        
        if (from == null || to == null || message == null) {
            return Map.of("success", false, "error", "from, to, message 不能为空");
        }
        
        logger.info("发送 Agent 消息：{} -> {}", from, to);
        coordinator.sendMessage(from, to, message);
        
        return Map.of("success", true);
    }
}
