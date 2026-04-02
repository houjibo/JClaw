package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
@Component
public class TaskCreateTool extends Tool {
    
    // 全局任务存储
    private static final Map<String, Task> TASK_STORE = new ConcurrentHashMap<>();
    private static int taskIdCounter = 0;
    
    public TaskCreateTool() {
        this.name = "task_create";
        this.description = "创建新任务";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String title = (String) params.get("title");
        String description = (String) params.get("description");
        String priority = (String) params.getOrDefault("priority", "medium");
        List<String> subtasks = getListParam(params.get("subtasks"));
        
        if (title == null || title.isBlank()) {
            return ToolResult.error("title 参数不能为空");
        }
        
        String taskId = "task_" + (++taskIdCounter);
        
        Task task = new Task();
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus("pending");
        task.setSubtasks(subtasks != null ? subtasks : new ArrayList<>());
        task.setCreatedAt(System.currentTimeMillis());
        task.setCreatedBy(context.getUserId() != null ? context.getUserId() : "system");
        
        TASK_STORE.put(taskId, task);
        
        System.out.println("[TaskCreateTool] 创建任务：" + taskId);
        
        return ToolResult.success("任务创建成功", formatTask(task));
    }
    
    private List<String> getListParam(Object param) {
        if (param == null) return null;
        if (param instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        if (param instanceof String str) {
            return Arrays.asList(str.split(","));
        }
        return null;
    }
    
    private String formatTask(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("任务：").append(task.getTitle()).append("\n");
        sb.append("ID: ").append(task.getId()).append("\n");
        sb.append("状态：").append(task.getStatus()).append("\n");
        sb.append("优先级：").append(task.getPriority()).append("\n");
        
        if (task.getDescription() != null) {
            sb.append("描述：").append(task.getDescription()).append("\n");
        }
        
        if (!task.getSubtasks().isEmpty()) {
            sb.append("子任务:\n");
            for (int i = 0; i < task.getSubtasks().size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(task.getSubtasks().get(i)).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    public static Task getTask(String taskId) {
        return TASK_STORE.get(taskId);
    }
    
    public static List<Task> listTasks() {
        return new ArrayList<>(TASK_STORE.values());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("title");
    }
    
    static class Task {
        private String id;
        private String title;
        private String description;
        private String status; // pending, running, completed, failed
        private String priority; // low, medium, high
        private List<String> subtasks;
        private long createdAt;
        private Long completedAt;
        private String createdBy;
        private String output;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public List<String> getSubtasks() { return subtasks; }
        public void setSubtasks(List<String> subtasks) { this.subtasks = subtasks; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        
        public Long getCompletedAt() { return completedAt; }
        public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
        
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
    }
}
