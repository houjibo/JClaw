package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 */
@Component
public class TaskListTool extends Tool {
    
    public TaskListTool() {
        this.name = "task_list";
        this.description = "列出所有任务";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String status = (String) params.get("status");
        String priority = (String) params.get("priority");
        Integer limit = (Integer) params.getOrDefault("limit", 20);
        
        List<TaskCreateTool.Task> tasks = TaskCreateTool.listTasks();
        
        // 过滤
        List<TaskCreateTool.Task> filtered = new ArrayList<>();
        for (TaskCreateTool.Task task : tasks) {
            if (status != null && !status.equals(task.getStatus())) continue;
            if (priority != null && !priority.equals(task.getPriority())) continue;
            filtered.add(task);
        }
        
        // 限制数量
        if (filtered.size() > limit) {
            filtered = filtered.subList(0, limit);
        }
        
        if (filtered.isEmpty()) {
            return ToolResult.success("没有任务", "[]");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("任务列表 (共 ").append(filtered.size()).append(" 个):\n\n");
        
        for (TaskCreateTool.Task task : filtered) {
            sb.append(String.format("[%s] %s (ID: %s)\n", 
                    getStatusIcon(task.getStatus()),
                    task.getTitle(),
                    task.getId()));
            sb.append(String.format("  优先级：%s | 创建时间：%s\n", 
                    task.getPriority(),
                    new Date(task.getCreatedAt())));
            if (task.getDescription() != null) {
                sb.append("  描述：").append(task.getDescription()).append("\n");
            }
            sb.append("\n");
        }
        
        return ToolResult.success("找到 " + filtered.size() + " 个任务", sb.toString());
    }
    
    private String getStatusIcon(String status) {
        return switch (status) {
            case "pending" -> "○";
            case "running" -> "⟳";
            case "completed" -> "✓";
            case "failed" -> "✗";
            default -> "?";
        };
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return true;
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 status   - 按状态过滤：pending, running, completed, failed (可选)
                 priority - 按优先级过滤：low, medium, high (可选)
                 limit    - 最大返回数量，默认 20 (可选)
               
               示例:
                 task_list
                 task_list status="pending"
                 task_list priority="high" limit=10
               """.formatted(name, description);
    }
}
