package com.jclaw.task.service.impl;

import com.jclaw.task.dto.TaskDTO;
import com.jclaw.task.dto.TaskUpdateRequest;
import com.jclaw.task.entity.Task;
import com.jclaw.task.mapper.TaskMapper;
import com.jclaw.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 任务管理服务实现
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    
    private final TaskMapper taskMapper;
    
    // 内存存储运行中的任务（用于支持 stop 操作）
    private static final Map<String, TaskContext> RUNNING_TASKS = new ConcurrentHashMap<>();
    
    @Override
    public TaskDTO createTask(TaskDTO task) {
        String taskId = UUID.randomUUID().toString().substring(0, 8);
        task.setId(taskId);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus("pending");
        
        Task entity = toEntity(task);
        taskMapper.insert(entity);
        
        log.info("创建任务：{}", taskId);
        return task;
    }
    
    @Override
    public TaskDTO getTask(String taskId) {
        Task entity = taskMapper.selectById(taskId);
        if (entity == null) {
            throw new IllegalArgumentException("任务不存在：" + taskId);
        }
        return toDTO(entity);
    }
    
    @Override
    public List<TaskDTO> listTasks(String status, String assignee) {
        List<Task> tasks = taskMapper.selectList(null);
        
        return tasks.stream()
            .filter(t -> status == null || status.isEmpty() || t.getStatus().equals(status))
            .filter(t -> assignee == null || assignee.isEmpty() || t.getAssignee().equals(assignee))
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public TaskDTO updateTask(String taskId, TaskUpdateRequest request) {
        Task entity = taskMapper.selectById(taskId);
        if (entity == null) {
            throw new IllegalArgumentException("任务不存在：" + taskId);
        }
        
        // 更新字段
        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getAssignee() != null) {
            entity.setAssignee(request.getAssignee());
        }
        if (request.getPriority() != null) {
            entity.setPriority(request.getPriority());
        }
        
        entity.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(entity);
        
        log.info("更新任务：{}", taskId);
        return toDTO(entity);
    }
    
    @Override
    public void stopTask(String taskId) {
        Task entity = taskMapper.selectById(taskId);
        if (entity == null) {
            throw new IllegalArgumentException("任务不存在：" + taskId);
        }
        
        // 检查任务是否在运行中
        TaskContext context = RUNNING_TASKS.get(taskId);
        if (context != null) {
            // 优雅终止
            context.setStopping(true);
            log.info("任务停止中：{}", taskId);
        }
        
        // 更新状态
        entity.setStatus("stopped");
        entity.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(entity);
        
        // 从运行列表中移除
        RUNNING_TASKS.remove(taskId);
        
        log.info("任务已停止：{}", taskId);
    }
    
    @Override
    public void deleteTask(String taskId) {
        taskMapper.deleteById(taskId);
        RUNNING_TASKS.remove(taskId);
        log.info("任务已删除：{}", taskId);
    }
    
    /**
     * 注册运行中的任务
     */
    public void registerRunningTask(String taskId, Thread thread) {
        RUNNING_TASKS.put(taskId, new TaskContext(thread));
        log.debug("注册运行任务：{}", taskId);
    }
    
    /**
     * 任务上下文
     */
    private static class TaskContext {
        private final Thread thread;
        private boolean stopping;
        
        public TaskContext(Thread thread) {
            this.thread = thread;
            this.stopping = false;
        }
        
        public boolean isStopping() {
            return stopping;
        }
        
        public void setStopping(boolean stopping) {
            this.stopping = stopping;
        }
    }
    
    // ========== 转换方法 ==========
    
    private Task toEntity(TaskDTO dto) {
        Task entity = new Task();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setAssignee(dto.getAssignee());
        entity.setPriority(dto.getPriority());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
    
    private TaskDTO toDTO(Task entity) {
        TaskDTO dto = new TaskDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setAssignee(entity.getAssignee());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
