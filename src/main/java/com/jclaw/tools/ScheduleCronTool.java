package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 定时任务工具
 */
@Component
public class ScheduleCronTool extends Tool {
    
    private static final List<Map<String, String>> SCHEDULES = new ArrayList<>();
    
    public ScheduleCronTool() {
        this.name = "schedule_cron";
        this.description = "创建定时任务";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String cron = (String) params.get("cron");
        String command = (String) params.get("command");
        String name = (String) params.get("name");
        
        if (cron == null) return ToolResult.error("cron 参数不能为空");
        if (command == null) return ToolResult.error("command 参数不能为空");
        
        Map<String, String> schedule = new HashMap<>();
        schedule.put("id", "schedule_" + (SCHEDULES.size() + 1));
        schedule.put("cron", cron);
        schedule.put("command", command);
        schedule.put("name", name != null ? name : "未命名任务");
        schedule.put("status", "active");
        
        SCHEDULES.add(schedule);
        
        StringBuilder sb = new StringBuilder();
        sb.append("定时任务已创建\n\n");
        sb.append("ID: ").append(schedule.get("id")).append("\n");
        sb.append("名称：").append(schedule.get("name")).append("\n");
        sb.append("Cron: ").append(cron).append("\n");
        sb.append("命令：").append(command).append("\n");
        sb.append("状态：active");
        
        return ToolResult.success("定时任务已创建", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { 
        return params.containsKey("cron") && params.containsKey("command"); 
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 cron    - Cron 表达式 (必填)
                 command - 执行命令 (必填)
                 name    - 任务名称 (可选)
               示例:
                 schedule_cron cron="0 0 * * *" command="backup" name="每日备份"
               """.formatted(name, description);
    }
}
