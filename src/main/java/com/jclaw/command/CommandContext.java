package com.jclaw.command;

import com.jclaw.core.ToolContext;

/**
 * 命令上下文 - 提供命令执行所需的环境
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public class CommandContext {
    
    /**
     * 工具上下文（继承自 Tool 系统）
     */
    private final ToolContext toolContext;
    
    /**
     * 当前工作目录
     */
    private final String workingDirectory;
    
    /**
     * 用户 ID
     */
    private final String userId;
    
    /**
     * 会话 ID
     */
    private final String sessionId;
    
    /**
     * 是否交互模式
     */
    private final boolean interactive;
    
    public CommandContext(ToolContext toolContext, String userId, String sessionId, boolean interactive) {
        this.toolContext = toolContext;
        this.workingDirectory = toolContext.getWorkingDirectory().toString();
        this.userId = userId;
        this.sessionId = sessionId;
        this.interactive = interactive;
    }
    
    /**
     * 获取工作目录
     */
    public String getWorkingDirectory() {
        return workingDirectory;
    }
    
    /**
     * 获取用户 ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * 获取会话 ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * 是否交互模式
     */
    public boolean isInteractive() {
        return interactive;
    }
    
    /**
     * 获取工具上下文
     */
    public ToolContext getToolContext() {
        return toolContext;
    }
    
    /**
     * 输出消息到控制台
     */
    public void output(String message) {
        System.out.println("[Command] " + message);
    }
    
    /**
     * 输出错误到控制台
     */
    public void error(String message) {
        System.err.println("[Command] ERROR: " + message);
    }
}
