package com.jclaw.core;

/**
 * 工具分类枚举
 */
public enum ToolCategory {
    FILE("文件操作"),
    CODE("代码相关"),
    SEARCH("搜索"),
    GIT("版本控制"),
    NETWORK("网络"),
    TASK("任务管理"),
    SYSTEM("系统"),
    MCP("MCP 协议"),
    COMM("通信"),
    TEAM("团队协作"),
    AGENT("多 Agent"),
    SKILL("技能管理");
    
    private final String description;
    
    ToolCategory(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
