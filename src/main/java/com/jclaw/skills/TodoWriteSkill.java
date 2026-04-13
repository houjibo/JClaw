package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务管理技能（TODO list）
 */
@Slf4j
@Service
public class TodoWriteSkill implements Skill {
    
    // 内存存储 TODO 列表
    private static final Map<String, List<TodoItem>> TODO_STORE = new ConcurrentHashMap<>();
    
    @Override
    public String getName() {
        return "todo_write";
    }
    
    @Override
    public String getDescription() {
        return "管理 TODO 列表";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String action = (String) params.get("action");
            String todoId = (String) params.get("id");
            String content = (String) params.get("content");
            
            if (action == null) {
                return SkillResult.error("缺少参数：action (create/update/delete/list)");
            }
            
            List<TodoItem> todos = TODO_STORE.computeIfAbsent(
                "default", k -> new ArrayList<>());
            
            switch (action.toLowerCase()) {
                case "create":
                    return createTodo(todos, content);
                case "update":
                    return updateTodo(todos, todoId, params);
                case "delete":
                    return deleteTodo(todos, todoId);
                case "list":
                    return listTodos(todos);
                case "complete":
                    return completeTodo(todos, todoId);
                default:
                    return SkillResult.error("未知操作：" + action);
            }
            
        } catch (Exception e) {
            log.error("TODO 操作失败", e);
            return SkillResult.error("操作失败：" + e.getMessage());
        }
    }
    
    private SkillResult createTodo(List<TodoItem> todos, String content) {
        if (content == null || content.isEmpty()) {
            return SkillResult.error("缺少参数：content");
        }
        
        TodoItem item = new TodoItem();
        item.setId(UUID.randomUUID().toString().substring(0, 6));
        item.setContent(content);
        item.setCreatedAt(LocalDateTime.now());
        item.setStatus("pending");
        
        todos.add(item);
        
        log.info("创建 TODO: {} - {}", item.getId(), content);
        
        return SkillResult.success("TODO 创建成功", Map.of(
            "id", item.getId(),
            "content", content
        ));
    }
    
    private SkillResult updateTodo(List<TodoItem> todos, String todoId, Map<String, Object> params) {
        if (todoId == null) {
            return SkillResult.error("缺少参数：id");
        }
        
        for (TodoItem item : todos) {
            if (item.getId().equals(todoId)) {
                if (params.containsKey("content")) {
                    item.setContent((String) params.get("content"));
                }
                if (params.containsKey("priority")) {
                    item.setPriority((String) params.get("priority"));
                }
                item.setUpdatedAt(LocalDateTime.now());
                
                log.info("更新 TODO: {}", todoId);
                return SkillResult.success("TODO 更新成功");
            }
        }
        
        return SkillResult.error("未找到 TODO: " + todoId);
    }
    
    private SkillResult deleteTodo(List<TodoItem> todos, String todoId) {
        if (todoId == null) {
            return SkillResult.error("缺少参数：id");
        }
        
        boolean removed = todos.removeIf(item -> item.getId().equals(todoId));
        
        if (removed) {
            log.info("删除 TODO: {}", todoId);
            return SkillResult.success("TODO 删除成功");
        }
        
        return SkillResult.error("未找到 TODO: " + todoId);
    }
    
    private SkillResult listTodos(List<TodoItem> todos) {
        if (todos.isEmpty()) {
            return SkillResult.success("TODO 列表为空", Map.of("data", "[]"));
        }
        
        StringBuilder sb = new StringBuilder("TODO 列表:\n\n");
        for (TodoItem item : todos) {
            sb.append(String.format("[%s] %s - %s (优先级：%s)\n",
                item.getStatus().equals("completed") ? "✓" : "○",
                item.getId(),
                item.getContent(),
                item.getPriority() != null ? item.getPriority() : "中"));
        }
        
        return SkillResult.success(sb.toString(), Map.of(
            "count", todos.size()
        ));
    }
    
    private SkillResult completeTodo(List<TodoItem> todos, String todoId) {
        if (todoId == null) {
            return SkillResult.error("缺少参数：id");
        }
        
        for (TodoItem item : todos) {
            if (item.getId().equals(todoId)) {
                item.setStatus("completed");
                item.setCompletedAt(LocalDateTime.now());
                
                log.info("完成 TODO: {}", todoId);
                return SkillResult.success("TODO 已完成");
            }
        }
        
        return SkillResult.error("未找到 TODO: " + todoId);
    }
    
    /**
     * TODO 项
     */
    public static class TodoItem {
        private String id;
        private String content;
        private String status; // pending, completed
        private String priority; // low, medium, high
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime completedAt;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    }
}
