package com.jclaw.channel.router;

import com.jclaw.channel.adapter.ChannelAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 消息路由器单元测试
 */
@ExtendWith(MockitoExtension.class)
class MessageRouterTest {

    @Mock
    private ChannelAdapter channelAdapter;

    @InjectMocks
    private MessageRouter messageRouter;

    @Test
    void testGetChannels() {
        when(channelAdapter.getName()).thenReturn("test");
        // 手动初始化 channelAdapters 列表
        messageRouter.setChannelAdapters(List.of(channelAdapter));
        messageRouter.init();

        List<String> channels = messageRouter.getChannels();

        assertNotNull(channels);
        assertTrue(channels.contains("test"));
    }

    @Test
    void testSendToChannel() {
        when(channelAdapter.getName()).thenReturn("test");
        when(channelAdapter.isConnected()).thenReturn(true);
        messageRouter.setChannelAdapters(List.of(channelAdapter));
        messageRouter.init();

        assertDoesNotThrow(() -> {
            messageRouter.sendToChannel("test", "target", "content");
        });
    }

    @Test
    void testBroadcast() {
        when(channelAdapter.getName()).thenReturn("test");
        when(channelAdapter.isConnected()).thenReturn(true);
        messageRouter.setChannelAdapters(List.of(channelAdapter));
        messageRouter.init();

        assertDoesNotThrow(() -> {
            messageRouter.broadcast("test content");
        });
    }
}
