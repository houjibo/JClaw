package com.jclaw.channel.adapter;

/**
 * 消息监听器接口
 */
public interface MessageListener {
    
    /**
     * 接收消息
     */
    void onMessage(String from, String content);
}
