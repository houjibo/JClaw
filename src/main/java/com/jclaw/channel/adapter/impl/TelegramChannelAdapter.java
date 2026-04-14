package com.jclaw.channel.adapter.impl;

import com.jclaw.channel.adapter.ChannelAdapter;
import com.jclaw.channel.adapter.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Telegram 通道适配器（基础框架）
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Component
public class TelegramChannelAdapter implements ChannelAdapter {
    
    private String botToken;
    private String webhookUrl;
    private boolean enabled = false;
    
    public TelegramChannelAdapter() {
        log.info("Telegram 通道适配器初始化");
    }
    
    @Override
    public String getName() {
        return "telegram";
    }
    
    /**
     * 初始化配置
     */
    public void initialize(Map<String, String> config) {
        this.botToken = config.get("botToken");
        this.webhookUrl = config.get("webhookUrl");
        this.enabled = config.get("enabled") != null && Boolean.parseBoolean(config.get("enabled"));
        log.info("Telegram 通道配置加载：enabled={}", this.enabled);
    }
    
    @Override
    public void sendMessage(String target, String content) {
        if (!enabled) {
            log.warn("Telegram 通道未启用");
            return;
        }
        log.info("发送 Telegram 消息：{} - {}", target, content.substring(0, Math.min(50, content.length())));
    }
    
    @Override
    public void receiveMessage(MessageListener listener) {
        log.info("Telegram 消息接收器初始化");
    }
    
    @Override
    public boolean isConnected() {
        return enabled;
    }
    
    /**
     * 发送文件
     */
    public void sendFile(String chatId, String filePath, String caption) {
        log.info("发送 Telegram 文件：{} - {}", chatId, filePath);
    }
    
    /**
     * 获取 Bot 信息
     */
    public Map<String, Object> getBotInfo() {
        Map<String, Object> info = new java.util.HashMap<>();
        info.put("name", getName());
        info.put("enabled", enabled);
        return info;
    }
}
