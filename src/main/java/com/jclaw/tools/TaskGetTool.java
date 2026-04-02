package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 */
@Component
public class TaskGetTool extends Tool {
    
    public TaskGetTool() {
        this.name = "task_get";
        this.description = "获取任务详细信息";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String taskId = (String) params.get("task_id");
        
        if (taskId == null || taskId.isBlank()) {
            return ToolResult.error("task_id 参数不能为空");
        }
        
        TaskCreateTool.Task task = TaskCreateTool.getTask(taskId);
        
        if (task == null) {
            return ToolResult.error("未找到任务：" + taskId);
        }
        
        String output = formatTaskDetails(task);
        System.out.println("[TaskGetTool] 获取任务详情：" + taskId);
        
        return ToolResult.success("获取成功", output);
    }
    
    private String formatTaskDetails(TaskCreateTool.Task task) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== 任务详情 ===\n\n");
        sb.append("ID: ").append(task.getId()).append("\n");
        sb.append("标题：").append(task.getTitle()).append("\n");
        sb.append("状态：").append(getStatusText(task.getStatus())).append("\n");
        sb.append("优先级：").append(getPriorityText(task.getPriority())).append("\n");
        
        if (task.getDescription() != null) {
            sb.append("描述：").append(task.getDescription()).append("\n");
        }
        
        sb.append("创建时间：").append(new Date(task.getCreatedAt())).append("\n");
        
        if (task.getCompletedAt() != null) {
            sb.append("完成时间：").append(new Date(task.getCompletedAt())).append("\n");
        }
        
        sb.append("创建人：").append(task.getCreatedBy()).append("\n");
        
        if (!task.getSubtasks().isEmpty()) {
            sb.append("\n子任务:\n");
            for (int i = 0; i < task.getSubtasks().size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(task.getSubtasks().get(i)).append("\n");
            }
        }
        
        if (task.getOutput() != null) {
            sb.append("\n输出:\n").append(task.getOutput()).append("\n");
        }
        
        return sb.toString();
    }
    
    private String getStatusText(String status) {
        return switch (status) {
            case "pending" -> "⏳ 待处理";
            case "running" -> "🔄 进行中";
            case "completed" -> "✅ 已完成";
            case "failed" -> "❌ 已失败";
            default -> "❓ 未知";
        };
    }
    
    private String getPriorityText(String priority) {
        return switch (priority) {
            case "low" -> "🟢 低";
            case "medium" -> "🟡 中";
            case "high" -> "🔴 高";
            default -> "⚪ 未知";
        };
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("task_id");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 task_id - 任务 ID (必填)
               
               示例:
                 task_get task_id="task_1"
               """.formatted(name, description);
    }
}
