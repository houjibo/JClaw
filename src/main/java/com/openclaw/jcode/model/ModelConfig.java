package com.openclaw.jcode.model;

/**
 * 模型配置记录类
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public record ModelConfig(
    String name,
    String displayName,
    String apiUrl,
    String modelName,
    String apiKeyEnv
) {
    /**
     * 获取 API Key 环境变量名
     */
    public String apiKeyEnv() {
        return apiKeyEnv;
    }
    
    /**
     * 获取模型名称
     */
    public String modelName() {
        return modelName;
    }
    
    /**
     * 获取 API URL
     */
    public String apiUrl() {
        return apiUrl;
    }
    
    /**
     * 获取显示名称
     */
    public String displayName() {
        return displayName;
    }
    
    /**
     * 获取名称
     */
    public String name() {
        return name;
    }
}
