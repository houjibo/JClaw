package com.jclaw.channel.adapter;

/**
 * 通道适配器接口
 */
public interface ChannelAdapter {
    
    /**
     * 获取通道名称
     */
    String getName();
    
    /**
     * 发送消息
     */
    void sendMessage(String target, String content);
    
    /**
     * 接收消息
     */
    void receiveMessage(MessageListener listener);
    
    /**
     * 通道状态
     */
    boolean isConnected();
}
