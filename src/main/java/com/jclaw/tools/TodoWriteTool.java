package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
@Component
public class TodoWriteTool extends Tool {
    
    // 内存存储 TODO 列表（实际应持久化）
    private static final Map<String, List<TodoItem>> TODO_STORE = new ConcurrentHashMap<>();
    
    public TodoWriteTool() {
        this.name = "todo_write";
        this.description = "管理 TODO 列表";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = (String) params.get("action");
        String todoId = (String) params.get("id");
        String content = (String) params.get("content");
        Boolean completed = (Boolean) params.get("completed");
        String sessionId = context.getSessionId();
        
        if (action == null || action.isBlank()) {
            return ToolResult.error("action 参数不能为空");
        }
        
        List<TodoItem> todos = TODO_STORE.computeIfAbsent(
                sessionId != null ? sessionId : "default", 
                k -> new ArrayList<>()
        );
        
        try {
            return switch (action.toLowerCase()) {
                case "create" -> createTodo(todos, content);
                case "update" -> updateTodo(todos, todoId, content, completed);
                case "delete" -> deleteTodo(todos, todoId);
                case "list" -> listTodos(todos);
                case "clear" -> clearTodos(todos);
                default -> ToolResult.error("不支持的操作：" + action);
            };
        } catch (Exception e) {
            System.err.println("[TodoWriteTool] 操作失败：" + e.getMessage());
            return ToolResult.error("操作失败：" + e.getMessage());
        }
    }
    
    private ToolResult createTodo(List<TodoItem> todos, String content) {
        if (content == null || content.isBlank()) {
            return ToolResult.error("content 参数不能为空");
        }
        
        TodoItem item = new TodoItem();
        item.setId(UUID.randomUUID().toString().substring(0, 8));
        item.setContent(content);
        item.setCompleted(false);
        item.setCreatedAt(System.currentTimeMillis());
        
        todos.add(item);
        
        System.out.println("[TodoWriteTool] 创建 TODO: " + item.getId());
        return ToolResult.success("TODO 创建成功", formatTodo(item));
    }
    
    private ToolResult updateTodo(List<TodoItem> todos, String todoId, String content, Boolean completed) {
        if (todoId == null) {
            return ToolResult.error("id 参数不能为空");
        }
        
        for (TodoItem item : todos) {
            if (item.getId().equals(todoId)) {
                if (content != null) {
                    item.setContent(content);
                }
                if (completed != null) {
                    item.setCompleted(completed);
                }
                System.out.println("[TodoWriteTool] 更新 TODO: " + todoId);
                return ToolResult.success("TODO 更新成功", formatTodo(item));
            }
        }
        
        return ToolResult.error("未找到 TODO: " + todoId);
    }
    
    private ToolResult deleteTodo(List<TodoItem> todos, String todoId) {
        if (todoId == null) {
            return ToolResult.error("id 参数不能为空");
        }
        
        boolean removed = todos.removeIf(item -> item.getId().equals(todoId));
        
        if (removed) {
            System.out.println("[TodoWriteTool] 删除 TODO: " + todoId);
            return ToolResult.success("TODO 删除成功");
        } else {
            return ToolResult.error("未找到 TODO: " + todoId);
        }
    }
    
    private ToolResult listTodos(List<TodoItem> todos) {
        if (todos.isEmpty()) {
            return ToolResult.success("TODO 列表为空", "[]");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("TODO 列表:\n\n");
        
        for (int i = 0; i < todos.size(); i++) {
            TodoItem item = todos.get(i);
            sb.append(String.format("%d. [%s] %s (ID: %s)\n", 
                    i + 1, 
                    item.isCompleted() ? "✓" : "○",
                    item.getContent(),
                    item.getId()));
        }
        
        return ToolResult.success("找到 " + todos.size() + " 个 TODO", sb.toString());
    }
    
    private ToolResult clearTodos(List<TodoItem> todos) {
        todos.clear();
        System.out.println("[TodoWriteTool] 清空 TODO 列表");
        return ToolResult.success("TODO 列表已清空");
    }
    
    private String formatTodo(TodoItem item) {
        return String.format("[%s] %s", item.isCompleted() ? "✓" : "○", item.getContent());
    }
    
    static class TodoItem {
        private String id;
        private String content;
        private boolean completed;
        private long createdAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("action");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 action    - 操作：create, update, delete, list, clear (必填)
                 id        - TODO ID (update/delete 需要)
                 content   - TODO 内容 (create/update 需要)
                 completed - 是否完成 (update 需要)
               示例:
                 todo_write action="create" content="完成项目报告"
                 todo_write action="list"
                 todo_write action="update" id="abc123" completed=true
               """.formatted(name, description);
    }
}
