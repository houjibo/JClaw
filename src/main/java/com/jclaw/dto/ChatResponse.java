package com.jclaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * 消息 ID
     */
    private String id;
    
    /**
     * 角色：user 或 assistant
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 使用的模型
     */
    private String model;
    
    /**
     * 是否流式
     */
    private boolean streaming;
}
