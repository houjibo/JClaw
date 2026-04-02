package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 任务管理命令 - 封装 Task 工具为命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class TaskCommand extends Command {
    
    // 模拟任务存储
    private static final Map<String, Map<String, Object>> TASKS = new HashMap<>();
    static {
        addMockTask("task-001", "代码审查", "pending");
        addMockTask("task-002", "Bug 修复", "running");
        addMockTask("task-003", "单元测试", "completed");
    }
    
    private static void addMockTask(String id, String title, String status) {
        Map<String, Object> task = new HashMap<>();
        task.put("id", id);
        task.put("title", title);
        task.put("status", status);
        task.put("createdAt", new Date().toString());
        task.put("output", "任务输出内容...");
        TASKS.put(id, task);
    }
    
    public TaskCommand() {
        this.name = "task";
        this.description = "任务管理（创建、列表、更新、停止、输出）";
        this.aliases = Arrays.asList("t");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("action", new CommandParameter("action", 
            "操作类型 (create, list, get, update, stop, output)", true)
            .choices("create", "list", "get", "update", "stop", "output"));
        
        this.parameters.put("id", new CommandParameter("id", "任务 ID", false));
        this.parameters.put("title", new CommandParameter("title", "任务标题", false));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+", 2);
        
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return listTasks();
        }
        
        String action = parts[0];
        String rest = parts.length > 1 ? parts[1] : "";
        String[] subParts = rest.split("\\s+");
        String taskId = subParts.length > 0 ? subParts[0] : null;
        
        return switch (action) {
            case "list" -> listTasks();
            case "create" -> createTask(rest);
            case "get" -> getTask(taskId);
            case "update" -> updateTask(rest);
            case "stop" -> stopTask(taskId);
            case "output" -> getTaskOutput(taskId);
            default -> CommandResult.error("未知操作：" + action);
        };
    }
    
    private CommandResult listTasks() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 任务列表\n\n");
        sb.append("| ID | 标题 | 状态 | 创建时间 |\n");
        sb.append("|----|------|------|----------|\n");
        
        for (Map<String, Object> task : TASKS.values()) {
            sb.append(String.format("| %s | %s | %s | %s |\n",
                    task.get("id"),
                    task.get("title"),
                    task.get("status"),
                    task.get("createdAt")));
        }
        
        return CommandResult.success("任务列表")
                .withData("tasks", new ArrayList<>(TASKS.values()))
                .withDisplayText(sb.toString());
    }
    
    private CommandResult createTask(String args) {
        String title = args != null && !args.isBlank() ? args : "新任务";
        String id = "task-" + String.format("%03d", TASKS.size() + 1);
        
        Map<String, Object> task = new HashMap<>();
        task.put("id", id);
        task.put("title", title);
        task.put("status", "pending");
        task.put("createdAt", new Date().toString());
        task.put("output", "");
        
        TASKS.put(id, task);
        
        return CommandResult.success("任务已创建：" + id)
                .withData("task", task);
    }
    
    private CommandResult getTask(String taskId) {
        if (taskId == null) {
            return CommandResult.error("请指定任务 ID");
        }
        
        Map<String, Object> task = TASKS.get(taskId);
        if (task == null) {
            return CommandResult.error("任务不存在：" + taskId);
        }
        
        return CommandResult.success("任务详情")
                .withData("task", task);
    }
    
    private CommandResult updateTask(String args) {
        String[] parts = args != null ? args.split("\\s+", 2) : new String[0];
        if (parts.length < 2) {
            return CommandResult.error("用法：task update <id> <status>");
        }
        
        String taskId = parts[0];
        String status = parts[1];
        
        Map<String, Object> task = TASKS.get(taskId);
        if (task == null) {
            return CommandResult.error("任务不存在：" + taskId);
        }
        
        task.put("status", status);
        
        return CommandResult.success("任务已更新")
                .withData("task", task);
    }
    
    private CommandResult stopTask(String taskId) {
        if (taskId == null) {
            return CommandResult.error("请指定任务 ID");
        }
        
        Map<String, Object> task = TASKS.get(taskId);
        if (task == null) {
            return CommandResult.error("任务不存在：" + taskId);
        }
        
        task.put("status", "stopped");
        
        return CommandResult.success("任务已停止：" + taskId);
    }
    
    private CommandResult getTaskOutput(String taskId) {
        if (taskId == null) {
            return CommandResult.error("请指定任务 ID");
        }
        
        Map<String, Object> task = TASKS.get(taskId);
        if (task == null) {
            return CommandResult.error("任务不存在：" + taskId);
        }
        
        return CommandResult.success("任务输出")
                .withData("output", task.get("output"))
                .withDisplayText("## 任务输出\n\n```\n" + task.get("output") + "\n```");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：task
            别名：t
            描述：任务管理
            
            用法：
              task list                     # 列出所有任务
              task create [标题]            # 创建任务
              task get <id>                 # 获取任务详情
              task update <id> <status>     # 更新任务状态
              task stop <id>                # 停止任务
              task output <id>              # 获取任务输出
            
            示例：
              task list
              task create 代码审查
              task get task-001
              task update task-001 running
              task stop task-001
            """;
    }
}
