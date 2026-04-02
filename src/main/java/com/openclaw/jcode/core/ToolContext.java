package com.openclaw.jcode.core;

import java.nio.file.Path;

/**
 * 工具执行上下文
 */
public class ToolContext {
    
    private Path workingDirectory;
    private String userId;
    private String sessionId;
    private boolean allowWrite;
    private boolean allowExec;
    private long maxReadSize;
    
    private ToolContext() {}
    
    public static ToolContext defaultContext() {
        ToolContext ctx = new ToolContext();
        ctx.workingDirectory = Path.of(System.getProperty("user.dir"));
        ctx.allowWrite = true;
        ctx.allowExec = true;
        ctx.maxReadSize = 10 * 1024 * 1024;
        return ctx;
    }
    
    // Getters and Setters
    public Path getWorkingDirectory() { return workingDirectory; }
    public void setWorkingDirectory(Path workingDirectory) { this.workingDirectory = workingDirectory; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public boolean isAllowWrite() { return allowWrite; }
    public void setAllowWrite(boolean allowWrite) { this.allowWrite = allowWrite; }
    
    public boolean isAllowExec() { return allowExec; }
    public void setAllowExec(boolean allowExec) { this.allowExec = allowExec; }
    
    public long getMaxReadSize() { return maxReadSize; }
    public void setMaxReadSize(long maxReadSize) { this.maxReadSize = maxReadSize; }
}
