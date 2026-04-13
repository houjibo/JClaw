package com.jclaw.ai.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 服务接口 - 统一模型调用
 * 
 * @author JClaw
 * @since 2026-04-13
 */
public interface AiService {
    
    /**
     * 简单对话
     */
    String chat(String prompt);
    
    /**
     * 带上下文的对话
     */
    String chatWithContext(List<Message> messages);
    
    /**
     * 流式对话（回调方式）
     */
    void streamChat(String prompt, StreamCallback callback);
    
    /**
     * 消息结构
     */
    @Data
    @NoArgsConstructor
    class Message {
        private String role; // user, assistant, system
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    /**
     * 流式回调接口
     */
    interface StreamCallback {
        void onToken(String token);
        void onComplete();
        void onError(Throwable error);
    }
}
