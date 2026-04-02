package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 支持：list, resume, delete, export 等
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class SessionCommand extends Command {
    
    // 模拟会话存储
    private static final Map<String, Map<String, Object>> SESSIONS = new HashMap<>();
    static {
        addMockSession("session-001", "代码审查", 120);
        addMockSession("session-002", "Bug 修复", 45);
        addMockSession("session-003", "新功能开发", 200);
    }
    
    private static void addMockSession(String id, String title, int messages) {
        Map<String, Object> session = new HashMap<>();
        session.put("id", id);
        session.put("title", title);
        session.put("createdAt", LocalDateTime.now().minusHours((long)(Math.random() * 24)));
        session.put("messages", messages);
        session.put("status", "active");
        SESSIONS.put(id, session);
    }
    
    public SessionCommand() {
        this.name = "session";
        this.description = "会话管理（列表、恢复、删除、导出）";
        this.aliases = Arrays.asList("sess", "s");
        this.category = CommandCategory.SESSION;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("action", new CommandParameter("action", 
            "操作类型 (list, resume, delete, export, clear)", true)
            .choices("list", "resume", "delete", "export", "clear"));
        
        this.parameters.put("id", new CommandParameter("id", "会话 ID", false));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return listSessions();
        }
        
        String action = parts[0];
        String sessionId = parts.length > 1 ? parts[1] : null;
        
        return switch (action) {
            case "list" -> listSessions();
            case "resume" -> resumeSession(sessionId);
            case "delete" -> deleteSession(sessionId);
            case "export" -> exportSession(sessionId);
            case "clear" -> clearSessions();
            default -> CommandResult.error("未知操作：" + action);
        };
    }
    
    private CommandResult listSessions() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 会话列表\n\n");
        sb.append("| ID | 标题 | 消息数 | 创建时间 | 状态 |\n");
        sb.append("|----|------|--------|----------|------|\n");
        
        for (Map<String, Object> session : SESSIONS.values()) {
            sb.append(String.format("| %s | %s | %d | %s | %s |\n",
                    session.get("id"),
                    session.get("title"),
                    session.get("messages"),
                    ((LocalDateTime) session.get("createdAt")).format(
                            java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                    session.get("status")));
        }
        
        return CommandResult.success("会话列表")
                .withData("sessions", new ArrayList<>(SESSIONS.values()))
                .withDisplayText(sb.toString());
    }
    
    private CommandResult resumeSession(String sessionId) {
        if (sessionId == null) {
            return CommandResult.error("请指定会话 ID");
        }
        
        Map<String, Object> session = SESSIONS.get(sessionId);
        if (session == null) {
            return CommandResult.error("会话不存在：" + sessionId);
        }
        
        return CommandResult.success("已恢复到会话：" + sessionId)
                .withData("session", session);
    }
    
    private CommandResult deleteSession(String sessionId) {
        if (sessionId == null) {
            return CommandResult.error("请指定会话 ID");
        }
        
        if (SESSIONS.remove(sessionId) == null) {
            return CommandResult.error("会话不存在：" + sessionId);
        }
        
        return CommandResult.success("已删除会话：" + sessionId);
    }
    
    private CommandResult exportSession(String sessionId) {
        if (sessionId == null) {
            return CommandResult.error("请指定会话 ID");
        }
        
        Map<String, Object> session = SESSIONS.get(sessionId);
        if (session == null) {
            return CommandResult.error("会话不存在：" + sessionId);
        }
        
        String exportPath = "/tmp/session-" + sessionId + ".json";
        return CommandResult.success("会话已导出：" + exportPath)
                .withData("path", exportPath)
                .withData("session", session);
    }
    
    private CommandResult clearSessions() {
        int count = SESSIONS.size();
        SESSIONS.clear();
        return CommandResult.success("已清除所有 " + count + " 个会话");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：session
            别名：sess, s
            描述：会话管理
            
            用法：
              session list              # 列出所有会话
              session resume <id>       # 恢复会话
              session delete <id>       # 删除会话
              session export <id>       # 导出会话
              session clear             # 清除所有会话
            
            示例：
              session list
              session resume session-001
              session delete session-002
              session export session-003
            """;
    }
}
