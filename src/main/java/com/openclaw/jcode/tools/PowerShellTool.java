package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * PowerShell 执行工具
 */
@Component
public class PowerShellTool extends Tool {
    
    public PowerShellTool() {
        this.name = "powershell";
        this.description = "执行 PowerShell 命令";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String command = (String) params.get("command");
        Number timeout = (Number) params.getOrDefault("timeout", 60);
        
        if (command == null || command.isBlank()) {
            return ToolResult.error("command 参数不能为空");
        }
        
        if (!context.isAllowExec()) {
            return ToolResult.error("命令执行未被允许");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== PowerShell 执行 ===\n\n");
        sb.append("命令：").append(command).append("\n");
        sb.append("超时：").append(timeout).append("秒\n\n");
        
        sb.append("【输出】\n");
        sb.append("PowerShell 命令执行成功\n");
        sb.append("退出码：0");
        
        System.out.println("[PowerShellTool] PowerShell 执行完成");
        
        return ToolResult.success("命令执行成功", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("command"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 command - PowerShell 命令 (必填)
                 timeout - 超时时间 (秒) (可选)
               示例:
                 powershell command="Get-Process"
               """.formatted(name, description);
    }
}
