package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 */
@Component
public class TaskOutputTool extends Tool {
    
    public TaskOutputTool() {
        this.name = "task_output";
        this.description = "获取任务执行输出";
        this.category = ToolCategory.TASK;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String taskId = (String) params.get("task_id");
        Integer lines = (Integer) params.getOrDefault("lines", 100);
        Boolean tail = (Boolean) params.getOrDefault("tail", true);
        
        if (taskId == null || taskId.isBlank()) {
            return ToolResult.error("task_id 参数不能为空");
        }
        
        TaskCreateTool.Task task = TaskCreateTool.getTask(taskId);
        
        if (task == null) {
            return ToolResult.error("未找到任务：" + taskId);
        }
        
        String output = task.getOutput();
        
        if (output == null || output.isBlank()) {
            return ToolResult.success("任务暂无输出", "任务 " + taskId + " 暂无输出内容");
        }
        
        // 处理输出行数限制
        String[] outputLines = output.split("\n");
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== 任务输出 (").append(taskId).append(") ===\n\n");
        
        if (outputLines.length > lines) {
            if (tail) {
                // 显示最后 N 行
                sb.append("... (省略前面 ").append(outputLines.length - lines).append(" 行)\n\n");
                for (int i = outputLines.length - lines; i < outputLines.length; i++) {
                    sb.append(outputLines[i]).append("\n");
                }
            } else {
                // 显示前 N 行
                for (int i = 0; i < lines; i++) {
                    sb.append(outputLines[i]).append("\n");
                }
                sb.append("\n... (省略后面 ").append(outputLines.length - lines).append(" 行)");
            }
        } else {
            sb.append(output);
        }
        
        sb.append("\n\n=== 任务状态 ===\n");
        sb.append("状态：").append(task.getStatus()).append("\n");
        sb.append("创建时间：").append(new Date(task.getCreatedAt())).append("\n");
        if (task.getCompletedAt() != null) {
            sb.append("完成时间：").append(new Date(task.getCompletedAt())).append("\n");
        }
        
        System.out.println("[TaskOutputTool] 获取任务输出：" + taskId + " (" + output.length() + " 字符)");
        
        return ToolResult.success("获取成功", sb.toString());
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
                 lines   - 显示行数，默认 100 (可选)
                 tail    - 是否显示最后 N 行，默认 true (可选)
               
               示例:
                 task_output task_id="task_1"
                 task_output task_id="task_1" lines=50 tail=true
               """.formatted(name, description);
    }
}
