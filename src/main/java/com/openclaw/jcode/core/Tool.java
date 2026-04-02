package com.openclaw.jcode.core;

import java.util.Map;

/**
 * 所有编码工具都必须继承此类
 */
public abstract class Tool {
    
    /** 工具唯一标识 */
    protected String name;
    
    /** 工具描述 */
    protected String description;
    
    /** 工具分类 */
    protected ToolCategory category;
    
    /** 是否需要用户确认 */
    protected boolean requiresConfirmation = false;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ToolCategory getCategory() { return category; }
    public void setCategory(ToolCategory category) { this.category = category; }
    
    public boolean isRequiresConfirmation() { return requiresConfirmation; }
    public void setRequiresConfirmation(boolean requiresConfirmation) { this.requiresConfirmation = requiresConfirmation; }
    
    /**
     * 执行工具
     * @param params 参数
     * @param context 执行上下文
     * @return 执行结果
     */
    public abstract ToolResult execute(Map<String, Object> params, ToolContext context);
    
    /**
     * 验证参数
     * @param params 参数
     * @return 是否有效
     */
    public boolean validate(Map<String, Object> params) {
        return true;
    }
    
    /**
     * 获取帮助信息
     * @return 帮助文本
     */
    public String getHelp() {
        return String.format("%s - %s", name, description);
    }
}
