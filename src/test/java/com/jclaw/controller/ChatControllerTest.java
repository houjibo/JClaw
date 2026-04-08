package com.jclaw.controller;

import com.jclaw.service.ChatService;
import com.jclaw.dto.ChatRequest;
import com.jclaw.dto.ChatResponse;
import com.jclaw.common.entity.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ChatController 单元测试
 */
@DisplayName("ChatController 单元测试")
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private ChatRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        mockRequest = new ChatRequest();
        mockRequest.setUserId("user-123");
        mockRequest.setMessage("Hello AI");
    }

    @Test
    @DisplayName("测试发送消息 - 成功")
    void testSendMessage_Success() {
        // Arrange
        ChatResponse mockResponse = new ChatResponse();
        mockResponse.setContent("Hello Human");
        when(chatService.sendMessage(mockRequest)).thenReturn(mockResponse);

        // Act
        Result<ChatResponse> result = chatController.sendMessage(mockRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("Hello Human", result.getData().getContent());
        verify(chatService, times(1)).sendMessage(mockRequest);
    }

    @Test
    @DisplayName("测试发送消息 - 失败")
    void testSendMessage_Failure() {
        // Arrange
        when(chatService.sendMessage(mockRequest)).thenThrow(new RuntimeException("API 调用失败"));

        // Act
        Result<ChatResponse> result = chatController.sendMessage(mockRequest);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("AI 回复失败"));
        verify(chatService, times(1)).sendMessage(mockRequest);
    }

    @Test
    @DisplayName("测试流式对话")
    void testStreamMessage() throws IOException {
        // Arrange
        // Act
        SseEmitter emitter = chatController.streamMessage(mockRequest);

        // Assert
        assertNotNull(emitter);
        verify(chatService, times(1)).streamMessage(eq(mockRequest), any(SseEmitter.class));
    }

    @Test
    @DisplayName("测试获取聊天历史 - 成功")
    void testGetHistory_Success() {
        // Arrange
        List<ChatResponse> mockHistory = new ArrayList<>();
        ChatResponse response = new ChatResponse();
        response.setContent("Test response");
        mockHistory.add(response);
        when(chatService.getHistory("user-123", 20)).thenReturn(mockHistory);

        // Act
        Result<List<ChatResponse>> result = chatController.getHistory("user-123", 20);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(chatService, times(1)).getHistory("user-123", 20);
    }

    @Test
    @DisplayName("测试获取聊天历史 - 失败")
    void testGetHistory_Failure() {
        // Arrange
        when(chatService.getHistory("user-123", 20)).thenThrow(new RuntimeException("数据库错误"));

        // Act
        Result<List<ChatResponse>> result = chatController.getHistory("user-123", 20);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("获取历史失败"));
        verify(chatService, times(1)).getHistory("user-123", 20);
    }

    @Test
    @DisplayName("测试清空聊天历史 - 成功")
    void testClearHistory_Success() {
        // Arrange
        doNothing().when(chatService).clearHistory("user-123");

        // Act
        Result<Void> result = chatController.clearHistory("user-123");

        // Assert
        assertTrue(result.isSuccess());
        verify(chatService, times(1)).clearHistory("user-123");
    }

    @Test
    @DisplayName("测试清空聊天历史 - 失败")
    void testClearHistory_Failure() {
        // Arrange
        doThrow(new RuntimeException("清空失败")).when(chatService).clearHistory("user-123");

        // Act
        Result<Void> result = chatController.clearHistory("user-123");

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("清空历史失败"));
        verify(chatService, times(1)).clearHistory("user-123");
    }

    @Test
    @DisplayName("测试健康检查")
    void testHealth() {
        // Act
        Result<String> result = chatController.health();

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Chat API is running", result.getData());
    }
}
