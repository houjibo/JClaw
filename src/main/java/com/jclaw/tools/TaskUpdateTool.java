package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 */
@Component
public class TaskUpdateTool extends Tool {
    
    public TaskUpdateTool() {
        this.name = "task_update";
        this.description = "更新任务状态或内容";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String taskId = (String) params.get("task_id");
        String status = (String) params.get("status");
        String title = (String) params.get("title");
        String description = (String) params.get("description");
        String priority = (String) params.get("priority");
        String output = (String) params.get("output");
        
        if (taskId == null || taskId.isBlank()) {
            return ToolResult.error("task_id 参数不能为空");
        }
        
        TaskCreateTool.Task task = TaskCreateTool.getTask(taskId);
        
        if (task == null) {
            return ToolResult.error("未找到任务：" + taskId);
        }
        
        List<String> updates = new ArrayList<>();
        
        // 更新状态
        if (status != null && !status.isBlank()) {
            String oldStatus = task.getStatus();
            task.setStatus(status);
            updates.add("状态：" + oldStatus + " → " + status);
            
            // 如果设置为 completed，记录完成时间
            if ("completed".equals(status)) {
                task.setCompletedAt(System.currentTimeMillis());
            } else if (task.getCompletedAt() != null) {
                task.setCompletedAt(null);
            }
        }
        
        // 更新标题
        if (title != null && !title.isBlank()) {
            updates.add("标题：" + task.getTitle() + " → " + title);
            task.setTitle(title);
        }
        
        // 更新描述
        if (description != null) {
            updates.add("描述：已更新");
            task.setDescription(description);
        }
        
        // 更新优先级
        if (priority != null && !priority.isBlank()) {
            updates.add("优先级：" + task.getPriority() + " → " + priority);
            task.setPriority(priority);
        }
        
        // 更新输出
        if (output != null) {
            updates.add("输出：已更新");
            task.setOutput(output);
        }
        
        if (updates.isEmpty()) {
            return ToolResult.error("没有提供任何更新内容");
        }
        
        System.out.println("[TaskUpdateTool] 更新任务：" + taskId);
        
        StringBuilder sb = new StringBuilder();
        sb.append("任务更新成功：").append(taskId).append("\n\n");
        sb.append("更新内容:\n");
        for (String update : updates) {
            sb.append("  - ").append(update).append("\n");
        }
        
        return ToolResult.success("任务更新成功", sb.toString());
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
                 task_id    - 任务 ID (必填)
                 status     - 新状态：pending, running, completed, failed (可选)
                 title      - 新标题 (可选)
                 description - 新描述 (可选)
                 priority   - 新优先级：low, medium, high (可选)
                 output     - 任务输出 (可选)
               
               示例:
                 task_update task_id="task_1" status="running"
                 task_update task_id="task_1" status="completed" output="任务完成"
               """.formatted(name, description);
    }
}
