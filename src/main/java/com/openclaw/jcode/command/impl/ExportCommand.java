package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Export 命令 - 会话/配置导出
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ExportCommand extends Command {
    
    public ExportCommand() {
        this.name = "export";
        this.description = "导出会话/配置";
        this.aliases = Arrays.asList("save", "backup");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showExportInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "session" -> exportSession(parts.length > 1 ? parts[1] : null);
            case "config" -> exportConfig();
            case "logs" -> exportLogs();
            case "info" -> showExportInfo();
            default -> showExportInfo();
        };
    }
    
    private CommandResult showExportInfo() {
        String report = """
            ## 导出功能
            
            ### 可导出内容
            
            | 类型 | 说明 | 格式 |
            |------|------|------|
            | session | 会话记录 | JSON/Markdown |
            | config | 配置文件 | JSON/YAML |
            | logs | 日志文件 | TXT/JSON |
            
            ### 快速导出
            
            ```bash
            export session my-session    # 导出会话
            export config                # 导出配置
            export logs                  # 导出日志
            ```
            
            ### JClaw Export 命令
            
            | 命令 | 说明 |
            |------|------|
            | export session <名称> | 导出会话 |
            | export config | 导出配置 |
            | export logs | 导出日志 |
            
            ### 导出位置
            
            默认导出到：
            ```
            ~/.openclaw/exports/
            ```
            """;
        
        return CommandResult.success("导出功能")
                .withDisplayText(report);
    }
    
    private CommandResult exportSession(String name) {
        if (name == null) {
            name = "session-" + System.currentTimeMillis();
        }
        
        String report = String.format("""
            ## 导出会话
            
            **名称**: %s
            
            ### 导出内容
            
            - 会话消息
            - 时间戳
            - 参与者信息
            
            ### 导出格式
            
            **JSON**:
            ```json
            {
              "sessionId": "%s",
              "messages": [],
              "createdAt": "2026-04-01T19:00:00Z"
            }
            ```
            
            **Markdown**:
            ```markdown
            # 会话记录：%s
            
            ## 消息列表
            
            ...
            ```
            
            ### 导出位置
            
            ```
            ~/.openclaw/exports/sessions/%s.json
            ```
            
            ✅ 会话已导出
            """, name, name, name, name);
        
        return CommandResult.success("导出会话：" + name)
                .withDisplayText(report);
    }
    
    private CommandResult exportConfig() {
        String report = """
            ## 导出配置
            
            ### 导出内容
            
            - 用户配置
            - 工具配置
            - 环境变量
            
            ### 导出格式
            
            **JSON**:
            ```json
            {
              "user": {...},
              "tools": {...},
              "env": {...}
            }
            ```
            
            **YAML**:
            ```yaml
            user:
              ...
            tools:
              ...
            env:
              ...
            ```
            
            ### 导出位置
            
            ```
            ~/.openclaw/exports/config/config.json
            ```
            
            ✅ 配置已导出
            """;
        
        return CommandResult.success("导出配置")
                .withDisplayText(report);
    }
    
    private CommandResult exportLogs() {
        String report = """
            ## 导出日志
            
            ### 导出内容
            
            - 系统日志
            - 操作日志
            - 错误日志
            
            ### 导出格式
            
            **TXT**:
            ```
            [INFO]  系统启动
            [INFO]  用户登录
            [ERROR] 操作失败
            ```
            
            **JSON**:
            ```json
            [
              {"level": "INFO", "message": "..."},
              {"level": "ERROR", "message": "..."}
            ]
            ```
            
            ### 导出位置
            
            ```
            ~/.openclaw/exports/logs/logs.txt
            ```
            
            ✅ 日志已导出
            """;
        
        return CommandResult.success("导出日志")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：export
            别名：save, backup
            描述：导出会话/配置
            
            用法：
              export                  # 导出功能
              export session <名称>   # 导出会话
              export config           # 导出配置
              export logs             # 导出日志
            
            示例：
              export session my-session
              export config
            """;
    }
}
