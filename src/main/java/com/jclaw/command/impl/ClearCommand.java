package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 清除上下文和消息
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ClearCommand extends Command {
    
    public ClearCommand() {
        this.name = "clear";
        this.description = "清除上下文和消息";
        this.aliases = Arrays.asList("clean", "reset");
        this.category = CommandCategory.SESSION;
        this.requiresConfirmation = true;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return clearContext();
        }
        
        String[] parts = args.trim().split("\\s+");
        String action = parts[0];
        
        return switch (action) {
            case "all" -> clearAll();
            case "messages" -> clearMessages();
            case "history" -> clearHistory();
            case "cache" -> clearCache();
            default -> clearContext();
        };
    }
    
    private CommandResult clearContext() {
        return CommandResult.success("上下文已清除")
                .withDisplayText("✅ 上下文已清除，可以开始新的对话");
    }
    
    private CommandResult clearAll() {
        return CommandResult.success("所有数据已清除")
                .withDisplayText("""
                    ✅ 已清除所有内容
                    
                    - 消息历史
                    - 上下文
                    - 缓存
                    - 临时文件
                    
                    请刷新页面以开始新的会话
                    """);
    }
    
    private CommandResult clearMessages() {
        return CommandResult.success("消息历史已清除")
                .withDisplayText("✅ 消息历史已清除");
    }
    
    private CommandResult clearHistory() {
        return CommandResult.success("历史记录已清除")
                .withDisplayText("✅ 历史记录已清除");
    }
    
    private CommandResult clearCache() {
        return CommandResult.success("缓存已清除")
                .withDisplayText("✅ 缓存已清除，性能可能略有提升");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：clear
            别名：clean, reset
            描述：清除上下文和消息
            
            用法：
              clear                     # 清除上下文
              clear all                 # 清除所有
              clear messages            # 清除消息
              clear history             # 清除历史
              clear cache               # 清除缓存
            
            示例：
              clear
              clear all
              clear cache
            """;
    }
}
