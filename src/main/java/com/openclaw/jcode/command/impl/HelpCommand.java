package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 显示命令和工具的帮助信息
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class HelpCommand extends Command {
    
    @Autowired(required = false)
    private CommandRegistry commandRegistry;
    
    public HelpCommand() {
        this.name = "help";
        this.description = "显示帮助信息";
        this.aliases = Arrays.asList("h", "?");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showGeneralHelp();
        }
        
        String topic = args.trim();
        
        // 查找命令帮助
        if (commandRegistry != null) {
            Command cmd = commandRegistry.getCommand(topic);
            if (cmd != null) {
                return CommandResult.success(cmd.getHelp())
                        .withDisplayText(cmd.getHelp());
            }
        }
        
        return CommandResult.error("未找到帮助主题：" + topic);
    }
    
    private CommandResult showGeneralHelp() {
        String help = """
            # JClaw 帮助系统
            
            ## 快速开始
            
            | 命令 | 说明 |
            |------|------|
            | help [command] | 查看命令帮助 |
            | git <subcommand> | Git 版本控制 |
            | config [key] [value] | 配置管理 |
            | cost [period] | 成本统计 |
            | session [action] | 会话管理 |
            | debug [action] | 调试工具 |
            | doctor [check] | 系统诊断 |
            | task [action] | 任务管理 |
            | mcp [action] | MCP 管理 |
            | plugin [action] | 插件管理 |
            
            ## 常用示例
            
            ```bash
            # 查看命令帮助
            help git
            
            # Git 操作
            git status
            git commit -m "修复 bug"
            git branch -a
            
            # 配置管理
            config                      # 查看所有配置
            config model                # 查看模型
            config model qwen3.5-plus   # 设置模型
            
            # 成本统计
            cost                        # 全部统计
            cost today                  # 今日统计
            cost month --detail         # 本月详细
            
            # 会话管理
            session list                # 列出会话
            session resume session-001  # 恢复会话
            ```
            
            ## 更多信息
            
            - 工具系统：45 个工具可用
            - 命令系统：10 个命令可用
            - API 文档：http://localhost:8080/api
            
            使用 `help <command>` 查看具体命令的详细说明。
            """;
        
        return CommandResult.success("JClaw 帮助")
                .withDisplayText(help);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：help
            别名：h, ?
            描述：显示帮助信息
            
            用法：
              help              # 显示一般帮助
              help <command>    # 显示命令帮助
            
            示例：
              help
              help git
              help config
            """;
    }
}
