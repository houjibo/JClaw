package com.jclaw.model;

import java.util.List;
import java.util.Map;

/**
 * 聊天请求
 */
public class ChatRequest {
    
    private String model;
    private List<Map<String, String>> messages;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;
    private String systemPrompt;
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<Map<String, String>> getMessages() { return messages; }
    public void setMessages(List<Map<String, String>> messages) { this.messages = messages; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
}
