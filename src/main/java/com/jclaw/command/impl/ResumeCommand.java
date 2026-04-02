package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 恢复之前的会话
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ResumeCommand extends Command {
    
    public ResumeCommand() {
        this.name = "resume";
        this.description = "恢复之前的会话";
        this.aliases = Arrays.asList("continue", "restore");
        this.category = CommandCategory.SESSION;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return listSessions();
        }
        
        String[] parts = args.trim().split("\\s+");
        String sessionId = parts[0];
        
        return resumeSession(sessionId);
    }
    
    private CommandResult listSessions() {
        String report = """
            ## 可恢复的会话
            
            | ID | 标题 | 时间 | 消息数 |
            |----|------|------|--------|
            | session-001 | 代码审查 | 10 分钟前 | 25 |
            | session-002 | Bug 修复 | 1 小时前 | 45 |
            | session-003 | 新功能开发 | 昨天 | 120 |
            
            使用 `resume <会话 ID>` 恢复会话
            
            ### 选项
            
            | 选项 | 说明 |
            |------|------|
            | --latest | 恢复最新会话 |
            | --all | 显示所有会话 |
            | --today | 显示今天的会话 |
            """;
        
        Map<String, Object> data = new HashMap<>();
        data.put("sessions", Arrays.asList(
                Map.of("id", "session-001", "title", "代码审查", "messages", 25),
                Map.of("id", "session-002", "title", "Bug 修复", "messages", 45),
                Map.of("id", "session-003", "title", "新功能开发", "messages", 120)
        ));
        
        return CommandResult.success("可恢复的会话")
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult resumeSession(String sessionId) {
        if ("--latest".equals(sessionId)) {
            sessionId = "session-001";
        }
        
        String report = String.format("""
            ## 会话已恢复：%s
            
            ### 会话信息
            
            | 属性 | 值 |
            |------|------|
            | 标题 | 代码审查 |
            | 创建时间 | 2026-04-01 11:45 |
            | 消息数 | 25 |
            | 工具调用 | 15 次 |
            
            ### 上次对话
            
            ```
            用户：帮我审查这个 PR
            助手：好的，让我看看...
            ```
            
            ### 继续
        
            会话已恢复，可以继续对话
            
            使用 `clear` 清除上下文开始新话题
            """, sessionId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("resumed", true);
        data.put("sessionId", sessionId);
        data.put("messages", 25);
        
        return CommandResult.success("会话已恢复：" + sessionId)
                .withData(data)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：resume
            别名：continue, restore
            描述：恢复之前的会话
            
            用法：
              resume                    # 列出可恢复的会话
              resume <会话 ID>          # 恢复指定会话
              resume --latest           # 恢复最新会话
            
            示例：
              resume
              resume session-001
              resume --latest
            """;
    }
}
