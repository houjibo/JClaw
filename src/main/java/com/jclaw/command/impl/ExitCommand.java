package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 退出应用或会话
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ExitCommand extends Command {
    
    public ExitCommand() {
        this.name = "exit";
        this.description = "退出应用或会话";
        this.aliases = Arrays.asList("quit", "bye", "q");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = true;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return exitApplication();
        }
        
        String[] parts = args.trim().split("\\s+");
        String action = parts[0];
        
        return switch (action) {
            case "session" -> exitSession();
            case "app", "application" -> exitApplication();
            default -> exitApplication();
        };
    }
    
    private CommandResult exitSession() {
        String report = """
            ## 会话已结束
            
            ### 会话统计
            
            - 消息数：0
            - 工具调用：0 次
            - 命令执行：0 次
            - 会话时长：0 分钟
            
            感谢使用！使用 `help` 重新开始
            """;
        
        return CommandResult.success("会话已结束")
                .withDisplayText(report);
    }
    
    private CommandResult exitApplication() {
        String report = """
            ## 退出应用
            
            ### 会话统计
            
            - 总会话数：0
            - 总消息数：0
            - 总工具调用：0 次
            - 总命令执行：0 次
            
            ### 感谢使用 JClaw!
            
            ```
              ____       _ _     _ 
             |  _ \\ ___ (_) | __| |
             | |_) / _ \\| | |/ _` |
             |  _ < (_) | | | (_| |
             |_| \\_\\___/|_|_|\\__,_|
            ```
            
            再见！👋
            """;
        
        Map<String, Object> data = new HashMap<>();
        data.put("exit", true);
        data.put("timestamp", new Date().toString());
        
        return CommandResult.success("感谢使用 JClaw")
                .withData(data)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：exit
            别名：quit, bye, q
            描述：退出应用或会话
            
            用法：
              exit                      # 退出应用
              exit session              # 结束会话
              exit app                  # 退出应用
            
            示例：
              exit
              exit session
              q
            """;
    }
}
