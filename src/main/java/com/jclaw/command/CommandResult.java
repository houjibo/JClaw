package com.jclaw.command;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令执行结果
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public class CommandResult {
    
    /**
     * 结果类型
     */
    public enum ResultType {
        SUCCESS,        // 成功
        ERROR,          // 错误
        SKIP,           // 跳过
        CONFIRMATION,   // 需要确认
        INTERACTIVE     // 需要交互
    }
    
    /**
     * 结果类型
     */
    private final ResultType type;
    
    /**
     * 结果消息
     */
    private final String message;
    
    /**
     * 结果数据
     */
    private final Map<String, Object> data;
    
    /**
     * 显示内容（用于前端展示）
     */
    private String displayText;
    
    /**
     * 确认提示（当 type=CONFIRMATION 时）
     */
    private String confirmationPrompt;
    
    public CommandResult(ResultType type, String message) {
        this.type = type;
        this.message = message;
        this.data = new HashMap<>();
    }
    
    /**
     * 创建成功结果
     */
    public static CommandResult success(String message) {
        return new CommandResult(ResultType.SUCCESS, message);
    }
    
    /**
     * 创建成功结果（带数据）
     */
    public static CommandResult success(String message, Map<String, Object> data) {
        CommandResult result = new CommandResult(ResultType.SUCCESS, message);
        result.data.putAll(data);
        return result;
    }
    
    /**
     * 创建错误结果
     */
    public static CommandResult error(String message) {
        return new CommandResult(ResultType.ERROR, message);
    }
    
    /**
     * 创建跳过结果
     */
    public static CommandResult skip() {
        return new CommandResult(ResultType.SKIP, "命令已跳过");
    }
    
    /**
     * 创建需要确认的结果
     */
    public static CommandResult confirmation(String prompt) {
        CommandResult result = new CommandResult(ResultType.CONFIRMATION, "需要确认");
        result.confirmationPrompt = prompt;
        return result;
    }
    
    /**
     * 添加数据
     */
    public CommandResult withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
    
    /**
     * 批量添加数据
     */
    public CommandResult withData(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }
    
    /**
     * 设置显示文本
     */
    public CommandResult withDisplayText(String text) {
        this.displayText = text;
        return this;
    }
    
    // Getters
    public ResultType getType() { return type; }
    public String getMessage() { return message; }
    public Map<String, Object> getData() { return data; }
    public String getDisplayText() { return displayText; }
    public String getConfirmationPrompt() { return confirmationPrompt; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CommandResult{type=").append(type);
        sb.append(", message='").append(message).append("'");
        if (displayText != null) {
            sb.append(", displayText='").append(displayText).append("'");
        }
        if (!data.isEmpty()) {
            sb.append(", data=").append(data);
        }
        sb.append("}");
        return sb.toString();
    }
}
