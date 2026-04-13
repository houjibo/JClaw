package com.jclaw.channels;

/**
 * 消息通道接口
 */
public interface MessageChannel {
    
    /**
     * 通道名称
     */
    String getName();
    
    /**
     * 发送消息
     * 
     * @param receiverId 接收者 ID
     * @param content 消息内容
     * @return 是否成功
     */
    boolean send(String receiverId, String content);
    
    /**
     * 发送消息（带消息类型）
     */
    boolean send(String receiverId, String content, String messageType);
    
    /**
     * 是否启用
     */
    boolean isEnabled();
}
