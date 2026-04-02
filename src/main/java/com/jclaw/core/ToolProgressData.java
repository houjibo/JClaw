package com.jclaw.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具进度数据实现
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public class ToolProgressData implements ToolProgress {
    
    /**
     * 工具名称
     */
    private final String toolName;
    
    /**
     * 进度类型
     */
    private final ProgressType type;
    
    /**
     * 完成百分比（0-100）
     */
    private int percentComplete;
    
    /**
     * 状态消息
     */
    private String statusMessage;
    
    /**
     * 附加数据
     */
    private final Map<String, Object> metadata;
    
    public ToolProgressData(String toolName, ProgressType type) {
        this.toolName = toolName;
        this.type = type;
        this.percentComplete = 0;
        this.statusMessage = "初始化...";
        this.metadata = new HashMap<>();
    }
    
    @Override
    public String getToolName() {
        return toolName;
    }
    
    @Override
    public ProgressType getType() {
        return type;
    }
    
    @Override
    public int getPercentComplete() {
        return percentComplete;
    }
    
    /**
     * 设置完成百分比
     */
    public void setPercentComplete(int percentComplete) {
        this.percentComplete = Math.max(0, Math.min(100, percentComplete));
    }
    
    @Override
    public String getStatusMessage() {
        return statusMessage;
    }
    
    /**
     * 设置状态消息
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    /**
     * 添加元数据
     */
    public void putMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 获取元数据
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>(this.metadata);
    }
    
    /**
     * 更新进度
     */
    public void update(int percentComplete, String statusMessage) {
        setPercentComplete(percentComplete);
        setStatusMessage(statusMessage);
    }
    
    /**
     * 标记完成
     */
    public void complete() {
        setPercentComplete(100);
        setStatusMessage("完成");
    }
    
    /**
     * 标记失败
     */
    public void fail(String errorMessage) {
        setStatusMessage("失败：" + errorMessage);
    }
    
    /**
     * 转换为 Map（用于 JSON 序列化）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("toolName", toolName);
        map.put("type", type.name());
        map.put("typeDisplay", type.getDisplayName());
        map.put("percentComplete", percentComplete);
        map.put("statusMessage", statusMessage);
        map.put("metadata", metadata);
        return map;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %d%% - %s", 
                type.getDisplayName(), toolName, percentComplete, statusMessage);
    }
}
