package com.jclaw.command;

/**
 * 命令类别枚举
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public enum CommandCategory {
    GIT("Git 版本控制"),
    SYSTEM("系统管理"),
    FILE("文件操作"),
    NETWORK("网络工具"),
    BUILD("构建工具"),
    SESSION("会话管理"),
    TASK("任务管理"),
    PLUGIN("插件管理"),
    MCP("MCP 协议"),
    AGENT("Agent 协作"),
    AI("AI 助手"),
    MODEL("模型服务"),
    CONFIG("配置管理"),
    DEBUG("调试工具"),
    COST("成本追踪"),
    DIAGNOSTIC("诊断工具"),
    OTHER("其他");
    
    private final String description;
    
    CommandCategory(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name() + " (" + description + ")";
    }
}
