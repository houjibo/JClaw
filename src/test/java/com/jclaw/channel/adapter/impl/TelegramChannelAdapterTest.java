package com.jclaw.channel.adapter.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Telegram 通道适配器测试
 */
@DisplayName("Telegram 通道适配器测试")
class TelegramChannelAdapterTest {
    
    private TelegramChannelAdapter adapter;
    
    @BeforeEach
    void setUp() {
        adapter = new TelegramChannelAdapter();
    }
    
    @Test
    @DisplayName("测试获取名称")
    void testGetName() {
        assertEquals("telegram", adapter.getName());
    }
    
    @Test
    @DisplayName("测试连接状态 - 默认禁用")
    void testIsConnectedDefault() {
        assertFalse(adapter.isConnected());
    }
    
    @Test
    @DisplayName("测试初始化 - 启用状态")
    void testInitializeEnabled() {
        Map<String, String> config = Map.of("enabled", "true");
        adapter.initialize(config);
        assertTrue(adapter.isConnected());
    }
    
    @Test
    @DisplayName("测试发送消息 - 不抛出异常")
    void testSendMessage() {
        assertDoesNotThrow(() -> {
            adapter.sendMessage("chat123", "test message");
        });
    }
    
    @Test
    @DisplayName("测试接收消息 - 不抛出异常")
    void testReceiveMessage() {
        assertDoesNotThrow(() -> {
            adapter.receiveMessage((from, content) -> {});
        });
    }
    
    @Test
    @DisplayName("测试获取 Bot 信息")
    void testGetBotInfo() {
        Map<String, Object> info = adapter.getBotInfo();
        assertNotNull(info);
        assertEquals("telegram", info.get("name"));
    }
}
