package com.jclaw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * JClaw 统一配置
 */
@Component
@ConfigurationProperties(prefix = "jcode")
@Validated
public class JClawProperties {
    
    /**
     * 工作目录
     */
    @NotBlank
    private String workspace = System.getProperty("user.home") + "/.openclaw/workspace";
    
    /**
     * 最大读取大小（字节）
     */
    @Min(1)
    @Max(100 * 1024 * 1024)
    private long maxReadSize = 10 * 1024 * 1024; // 10MB
    
    /**
     * 允许写操作
     */
    private boolean allowWrite = true;
    
    /**
     * 允许命令执行
     */
    private boolean allowExec = true;
    
    /**
     * 启用缓存
     */
    private boolean cacheEnabled = true;
    
    /**
     * 缓存过期时间（分钟）
     */
    @Min(1)
    @Max(60)
    private int cacheExpireMinutes = 10;
    
    /**
     * 启用 MCP
     */
    private boolean mcpEnabled = true;
    
    /**
     * 启用多 Agent
     */
    private boolean agentEnabled = true;
    
    /**
     * 流式输出超时（毫秒）
     */
    @Min(1000)
    @Max(300000)
    private long streamingTimeout = 30000; // 30 秒
    
    // Getters and Setters
    public String getWorkspace() { return workspace; }
    public void setWorkspace(String workspace) { this.workspace = workspace; }
    
    public long getMaxReadSize() { return maxReadSize; }
    public void setMaxReadSize(long maxReadSize) { this.maxReadSize = maxReadSize; }
    
    public boolean isAllowWrite() { return allowWrite; }
    public void setAllowWrite(boolean allowWrite) { this.allowWrite = allowWrite; }
    
    public boolean isAllowExec() { return allowExec; }
    public void setAllowExec(boolean allowExec) { this.allowExec = allowExec; }
    
    public boolean isCacheEnabled() { return cacheEnabled; }
    public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
    
    public int getCacheExpireMinutes() { return cacheExpireMinutes; }
    public void setCacheExpireMinutes(int cacheExpireMinutes) { this.cacheExpireMinutes = cacheExpireMinutes; }
    
    public boolean isMcpEnabled() { return mcpEnabled; }
    public void setMcpEnabled(boolean mcpEnabled) { this.mcpEnabled = mcpEnabled; }
    
    public boolean isAgentEnabled() { return agentEnabled; }
    public void setAgentEnabled(boolean agentEnabled) { this.agentEnabled = agentEnabled; }
    
    public long getStreamingTimeout() { return streamingTimeout; }
    public void setStreamingTimeout(long streamingTimeout) { this.streamingTimeout = streamingTimeout; }
}
