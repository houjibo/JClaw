package com.jclaw.task.controller;

import com.jclaw.task.dto.TaskDTO;
import com.jclaw.task.dto.TaskUpdateRequest;
import com.jclaw.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理控制器
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    /**
     * 创建任务
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO task) {
        TaskDTO created = taskService.createTask(task);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String taskId) {
        try {
            TaskDTO task = taskService.getTask(taskId);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> listTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignee) {
        List<TaskDTO> tasks = taskService.listTasks(status, assignee);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable String taskId,
            @RequestBody TaskUpdateRequest request) {
        try {
            TaskDTO updated = taskService.updateTask(taskId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 停止任务
     */
    @PostMapping("/{taskId}/stop")
    public ResponseEntity<Void> stopTask(@PathVariable String taskId) {
        try {
            taskService.stopTask(taskId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }
}
