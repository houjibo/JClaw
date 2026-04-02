package com.jclaw.command;

import com.jclaw.core.ToolContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public abstract class Command {
    
    /**
     * 命令名称（唯一标识）
     */
    protected String name;
    
    /**
     * 命令描述
     */
    protected String description;
    
    /**
     * 命令别名（支持多个）
     */
    protected List<String> aliases;
    
    /**
     * 命令类别
     */
    protected CommandCategory category;
    
    /**
     * 是否需要确认
     */
    protected boolean requiresConfirmation = false;
    
    /**
     * 是否支持非交互模式
     */
    protected boolean supportsNonInteractive = true;
    
    /**
     * 命令参数定义
     */
    protected Map<String, CommandParameter> parameters = new HashMap<>();
    
    /**
     * 执行命令
     * @param args 命令参数字符串
     * @param context 命令上下文
     * @return 命令执行结果
     */
    public abstract CommandResult execute(String args, CommandContext context);
    
    /**
     * 获取命令帮助信息
     */
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("命令：").append(name).append("\n");
        sb.append("描述：").append(description).append("\n");
        
        if (aliases != null && !aliases.isEmpty()) {
            sb.append("别名：").append(String.join(", ", aliases)).append("\n");
        }
        
        sb.append("类别：").append(category).append("\n");
        sb.append("参数：\n");
        
        for (Map.Entry<String, CommandParameter> entry : parameters.entrySet()) {
            CommandParameter param = entry.getValue();
            sb.append("  --").append(entry.getKey());
            if (param.required) {
                sb.append(" <必填>");
            }
            sb.append(": ").append(param.description).append("\n");
        }
        
        return sb.toString();
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }
    
    public CommandCategory getCategory() { return category; }
    public void setCategory(CommandCategory category) { this.category = category; }
    
    public boolean isRequiresConfirmation() { return requiresConfirmation; }
    public void setRequiresConfirmation(boolean requiresConfirmation) { this.requiresConfirmation = requiresConfirmation; }
    
    public boolean isSupportsNonInteractive() { return supportsNonInteractive; }
    public void setSupportsNonInteractive(boolean supportsNonInteractive) { this.supportsNonInteractive = supportsNonInteractive; }
    
    public Map<String, CommandParameter> getParameters() { return parameters; }
    public void setParameters(Map<String, CommandParameter> parameters) { this.parameters = parameters; }
    
    /**
     * 命令类别枚举
     */
    public enum CommandCategory {
        GIT,              // Git 相关
        CONFIG,           // 配置管理
        SESSION,          // 会话管理
        COST,             // 成本追踪
        DEBUG,            // 调试工具
        DIAGNOSTIC,       // 诊断工具
        PLUGIN,           // 插件管理
        SYSTEM,           // 系统命令
        CUSTOM,           // 自定义命令
        AI,               // AI 助手
        MODEL,            // 模型服务
        FILE,             // 文件操作
        NETWORK,          // 网络工具
        BUILD,            // 构建工具
        TASK,             // 任务管理
        MCP,              // MCP 协议
        AGENT,            // Agent 协作
        OTHER             // 其他
    }
    
    /**
     * 命令参数定义
     */
    public static class CommandParameter {
        public String name;
        public String description;
        public boolean required;
        public String defaultValue;
        public List<String> choices; // 可选值
        
        public CommandParameter(String name, String description, boolean required) {
            this.name = name;
            this.description = description;
            this.required = required;
        }
        
        public CommandParameter choices(String... choices) {
            this.choices = List.of(choices);
            return this;
        }
        
        public CommandParameter defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }
    }
}
