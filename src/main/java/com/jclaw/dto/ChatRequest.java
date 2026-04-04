package com.jclaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    /**
     * 用户 ID
     */
    private String userId;
    
    /**
     * 用户消息
     */
    private String message;
    
    /**
     * 对话上下文（可选）
     */
    private String context;
    
    /**
     * 模型选择（可选，默认 qwen3.5-plus）
     */
    private String model;
}
