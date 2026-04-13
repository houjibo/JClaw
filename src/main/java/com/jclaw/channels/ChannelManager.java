package com.jclaw.channels;

import com.jclaw.channels.impl.FeishuChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息通道管理器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelManager {
    
    private final List<MessageChannel> channels;
    private final Map<String, MessageChannel> channelRegistry = new ConcurrentHashMap<>();
    
    /**
     * 初始化：注册所有通道
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        for (MessageChannel channel : channels) {
            channelRegistry.put(channel.getName(), channel);
            if (channel.isEnabled()) {
                log.info("启用消息通道：{}", channel.getName());
            } else {
                log.info("消息通道未启用：{}", channel.getName());
            }
        }
        log.info("消息通道管理器初始化完成，共 {} 个通道", channelRegistry.size());
    }
    
    /**
     * 发送消息
     */
    public boolean send(String channelName, String receiverId, String content) {
        MessageChannel channel = channelRegistry.get(channelName);
        if (channel == null) {
            log.error("未知通道：{}", channelName);
            return false;
        }
        
        if (!channel.isEnabled()) {
            log.warn("通道未启用：{}", channelName);
            return false;
        }
        
        log.info("发送消息到 {}: {}", channelName, receiverId);
        return channel.send(receiverId, content);
    }
    
    /**
     * 广播消息到所有启用的通道
     */
    public void broadcast(String content) {
        for (MessageChannel channel : channelRegistry.values()) {
            if (channel.isEnabled()) {
                // 广播需要知道接收者，这里简化处理
                log.info("广播消息到 {}: {}", channel.getName(), content);
            }
        }
    }
    
    /**
     * 获取通道
     */
    public MessageChannel getChannel(String name) {
        return channelRegistry.get(name);
    }
    
    /**
     * 获取飞书通道
     */
    public FeishuChannel getFeishuChannel() {
        MessageChannel channel = channelRegistry.get("feishu");
        return channel instanceof FeishuChannel ? (FeishuChannel) channel : null;
    }
    
    /**
     * 列出所有通道
     */
    public List<MessageChannel> listChannels() {
        return channels;
    }
}
