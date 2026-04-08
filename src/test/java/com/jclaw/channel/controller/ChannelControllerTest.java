package com.jclaw.channel.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.channel.router.MessageRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ChannelController 单元测试
 */
@DisplayName("ChannelController 单元测试")
class ChannelControllerTest {

    @Mock
    private MessageRouter messageRouter;

    @InjectMocks
    private ChannelController channelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试列出所有通道")
    void testListChannels() {
        // Arrange
        List<String> mockChannels = Arrays.asList("feishu", "qq", "discord");
        when(messageRouter.getChannels()).thenReturn(mockChannels);

        // Act
        Result<List<String>> result = channelController.listChannels();

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(3, result.getData().size());
        verify(messageRouter, times(1)).getChannels();
    }

    @Test
    @DisplayName("测试发送消息到指定通道 - 成功")
    void testSendToChannel_Success() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("target", "user-123");
        request.put("content", "Hello World");
        
        doNothing().when(messageRouter).sendToChannel("discord", "user-123", "Hello World");

        // Act
        Result<Void> result = channelController.sendToChannel("discord", request);

        // Assert
        assertTrue(result.isSuccess());
        verify(messageRouter, times(1)).sendToChannel("discord", "user-123", "Hello World");
    }

    @Test
    @DisplayName("测试广播消息")
    void testBroadcast() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("content", "Broadcast message");
        
        doNothing().when(messageRouter).broadcast("Broadcast message");

        // Act
        Result<Void> result = channelController.broadcast(request);

        // Assert
        assertTrue(result.isSuccess());
        verify(messageRouter, times(1)).broadcast("Broadcast message");
    }

    @Test
    @DisplayName("测试获取通道状态")
    void testGetChannelStatus() {
        // Act
        Result<Map<String, Object>> result = channelController.getChannelStatus("discord");

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("discord", result.getData().get("name"));
        assertTrue((Boolean) result.getData().get("connected"));
    }
}
