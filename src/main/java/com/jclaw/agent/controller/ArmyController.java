package com.jclaw.agent.controller;

import com.jclaw.agent.coordinator.ArmyCoordinator;
import com.jclaw.agent.coordinator.ArmyCoordinator.ArmyContext;
import com.jclaw.agent.coordinator.ArmyCoordinator.ArmyRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 联军架构 REST API 控制器
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/army")
@RequiredArgsConstructor
public class ArmyController {
    
    private final ArmyCoordinator armyCoordinator;
    
    /**
     * 创建联军任务
     * POST /api/army/tasks
     */
    @PostMapping("/tasks")
    public ResponseEntity<Map<String, Object>> createArmyTask(
            @RequestParam String taskId,
            @RequestParam String description,
            @RequestParam(required = false) List<String> roles) {
        
        List<ArmyRole> roleList;
        if (roles == null || roles.isEmpty()) {
            // 默认使用全部 5 大角色
            roleList = Arrays.asList(ArmyRole.values());
        } else {
            roleList = roles.stream()
                .map(r -> {
                    try {
                        return ArmyRole.valueOf(r.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        }
        
        ArmyContext context = armyCoordinator.createArmyTask(taskId, description, roleList);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("taskId", taskId);
        response.put("status", context.getStatus());
        response.put("roles", roleList.stream().map(ArmyRole::getCode).toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 启动标准作战流程
     * POST /api/army/battle
     */
    @PostMapping("/battle")
    public ResponseEntity<Map<String, Object>> startStandardBattle(
            @RequestParam String taskId,
            @RequestParam String description,
            @RequestParam(defaultValue = "3600000") long timeout) {
        
        ArmyContext context = armyCoordinator.standardBattle(taskId, description, timeout);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("taskId", taskId);
        response.put("status", context.getStatus());
        response.put("result", context.getResult());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取任务状态
     * GET /api/army/tasks/{taskId}
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> getTaskStatus(@PathVariable String taskId) {
        ArmyContext context = armyCoordinator.getTaskStatus(taskId);
        if (context == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("taskId", context.getTaskId());
        response.put("status", context.getStatus());
        response.put("description", context.getDescription());
        response.put("roles", context.getRequiredRoles());
        
        if (context.getAgents() != null) {
            Map<String, Object> agentsInfo = new HashMap<>();
            context.getAgents().forEach((role, agent) -> {
                Map<String, String> info = new HashMap<>();
                info.put("id", agent.getId());
                info.put("status", agent.getStatus());
                agentsInfo.put(role, info);
            });
            response.put("agents", agentsInfo);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 等待任务完成
     * GET /api/army/tasks/{taskId}/wait?timeout=5000
     */
    @GetMapping("/tasks/{taskId}/wait")
    public ResponseEntity<Map<String, Object>> waitForCompletion(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "5000") long timeout) {
        
        ArmyContext context = armyCoordinator.waitForCompletion(taskId, timeout);
        if (context == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("taskId", taskId);
        response.put("status", context.getStatus());
        response.put("result", context.getResult());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 列出所有活跃任务
     * GET /api/army/tasks
     */
    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> listActiveTasks() {
        List<ArmyContext> tasks = armyCoordinator.listActiveTasks();
        
        List<Map<String, Object>> taskList = tasks.stream().map(context -> {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("taskId", context.getTaskId());
            taskInfo.put("status", context.getStatus());
            taskInfo.put("description", context.getDescription());
            taskInfo.put("roles", context.getRequiredRoles());
            return taskInfo;
        }).toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", taskList.size());
        response.put("tasks", taskList);
        
        return ResponseEntity.ok(response);
    }
}
