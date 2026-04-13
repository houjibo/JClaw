package com.jclaw.channels;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息基类
 */
@Data
@NoArgsConstructor
public class ChannelMessage {
    
    /**
     * 消息 ID
     */
    private String messageId;
    
    /**
     * 发送者 ID
     */
    private String senderId;
    
    /**
     * 接收者 ID（群聊或私聊）
     */
    private String receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型（text, image, file, etc.）
     */
    private String messageType;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    /**
     * 是否是群聊
     */
    private boolean isGroup;
    
    public ChannelMessage(String senderId, String content) {
        this.senderId = senderId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
}
