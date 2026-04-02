package com.jclaw.core;

/**
 * 工具进度追踪接口
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public interface ToolProgress {
    
    /**
     * 获取工具名称
     */
    String getToolName();
    
    /**
     * 获取进度类型
     */
    ProgressType getType();
    
    /**
     * 获取完成百分比（0-100）
     */
    int getPercentComplete();
    
    /**
     * 获取状态消息
     */
    String getStatusMessage();
    
    /**
     * 进度类型枚举
     */
    enum ProgressType {
        /** Bash 命令执行 */
        BASH_EXECUTION("Bash 执行"),
        /** 文件读取 */
        FILE_READ("文件读取"),
        /** 文件写入 */
        FILE_WRITE("文件写入"),
        /** 文件编辑 */
        FILE_EDIT("文件编辑"),
        /** 网络搜索 */
        WEB_SEARCH("网络搜索"),
        /** 任务输出 */
        TASK_OUTPUT("任务输出"),
        /** MCP 调用 */
        MCP_CALL("MCP 调用"),
        /** Agent 工作 */
        AGENT_WORK("Agent 工作"),
        /** Git 操作 */
        GIT_OPERATION("Git 操作"),
        /** 代码搜索 */
        CODE_SEARCH("代码搜索"),
        /** 其他 */
        OTHER("其他");
        
        private final String displayName;
        
        ProgressType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
