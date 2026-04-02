package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 */
@Component
public class TaskStopTool extends Tool {
    
    public TaskStopTool() {
        this.name = "task_stop";
        this.description = "停止运行中的任务";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String taskId = (String) params.get("task_id");
        String reason = (String) params.get("reason");
        
        if (taskId == null || taskId.isBlank()) {
            return ToolResult.error("task_id 参数不能为空");
        }
        
        TaskCreateTool.Task task = TaskCreateTool.getTask(taskId);
        
        if (task == null) {
            return ToolResult.error("未找到任务：" + taskId);
        }
        
        // 检查任务状态
        String currentStatus = task.getStatus();
        if (!"running".equals(currentStatus)) {
            return ToolResult.error("任务当前状态为 " + currentStatus + "，无法停止（仅运行中的任务可停止）");
        }
        
        // 停止任务
        task.setStatus("failed");
        task.setCompletedAt(System.currentTimeMillis());
        
        String stopReason = reason != null ? reason : "用户手动停止";
        String output = "任务于 " + new Date() + " 被停止\n原因：" + stopReason;
        task.setOutput(output);
        
        System.out.println("[TaskStopTool] 停止任务：" + taskId + " 原因：" + stopReason);
        
        StringBuilder sb = new StringBuilder();
        sb.append("任务已停止\n\n");
        sb.append("任务 ID: ").append(task.getId()).append("\n");
        sb.append("任务标题：").append(task.getTitle()).append("\n");
        sb.append("停止时间：").append(new Date()).append("\n");
        sb.append("停止原因：").append(stopReason).append("\n");
        
        return ToolResult.success("任务已停止", sb.toString());
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
                 reason  - 停止原因 (可选)
               
               示例:
                 task_stop task_id="task_1"
                 task_stop task_id="task_1" reason="用户取消"
               """.formatted(name, description);
    }
}
