package com.jclaw.service;

import com.jclaw.dto.ChatRequest;
import com.jclaw.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatService 单元测试
 * IM platform service 层测试
 */
@DisplayName("ChatService 单元测试")
class ChatServiceTest {

    private ChatService chatService;
    private ChatRequest chatRequest;

    @BeforeEach
    void setUp() {
        chatService = new ChatService();
        chatRequest = new ChatRequest();
        chatRequest.setUserId("test-user-001");
        chatRequest.setMessage("你好");
    }

    @Test
    @DisplayName("测试构造函数初始化")
    void testConstructor() {
        // 验证实例创建成功
        assertNotNull(chatService);
    }

    @Test
    @DisplayName("测试发送消息 - 正常问候")
    void testSendMessage_Greeting() {
        // Arrange
        chatRequest.setMessage("你好");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getTimestamp());
        assertEquals("assistant", response.getRole());
        assertFalse(response.isStreaming());
        assertNotNull(response.getContent());
        assertTrue(response.getContent().contains("你好"));
        assertEquals("qwen-plus", response.getModel());
    }

    @Test
    @DisplayName("测试发送消息 - 自定义模型")
    void testSendMessage_CustomModel() {
        // Arrange
        chatRequest.setMessage("你好");
        chatRequest.setModel("glm-4.7");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertEquals("glm-4.7", response.getModel());
    }

    @Test
    @DisplayName("测试发送消息 - 代码相关关键词")
    void testSendMessage_CodeKeyword() {
        // Arrange
        chatRequest.setMessage("帮我分析一下这段代码");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("代码追溯"));
    }

    @Test
    @DisplayName("测试发送消息 - 影响分析关键词")
    void testSendMessage_ImpactAnalysisKeyword() {
        // Arrange
        chatRequest.setMessage("这次变更的影响范围是什么");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("影响分析"));
    }

    @Test
    @DisplayName("测试发送消息 - Agent 关键词")
    void testSendMessage_AgentKeyword() {
        // Arrange
        chatRequest.setMessage("当前有多少个 Agent 在运行");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("Agent"));
    }

    @Test
    @DisplayName("测试发送消息 - 通道关键词")
    void testSendMessage_ChannelKeyword() {
        // Arrange
        chatRequest.setMessage("通道管理支持哪些平台");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("飞书") || response.getContent().contains("通道"));
    }

    @Test
    @DisplayName("测试发送消息 - 记忆关键词")
    void testSendMessage_MemoryKeyword() {
        // Arrange
        chatRequest.setMessage("如何管理记忆");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("记忆"));
    }

    @Test
    @DisplayName("测试发送消息 - 感谢关键词")
    void testSendMessage_ThankYouKeyword() {
        // Arrange
        chatRequest.setMessage("谢谢帮助");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("不客气"));
    }

    @Test
    @DisplayName("测试发送消息 - 再见关键词")
    void testSendMessage_GoodbyeKeyword() {
        // Arrange
        chatRequest.setMessage("再见");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("再见"));
    }

    @Test
    @DisplayName("测试发送消息 - 默认回复")
    void testSendMessage_DefaultReply() {
        // Arrange
        chatRequest.setMessage("这个问题比较复杂");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertFalse(response.getContent().isEmpty());
    }

    @Test
    @DisplayName("测试发送消息 - null 用户 ID")
    void testSendMessage_NullUserId() {
        // Arrange
        chatRequest.setUserId(null);
        chatRequest.setMessage("你好");

        // Act - 不应该抛出异常
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getContent());
    }

    @Test
    @DisplayName("测试发送消息 - null 消息")
    void testSendMessage_NullMessage() {
        // Arrange
        chatRequest.setUserId("user-123");
        chatRequest.setMessage(null);

        // Act - 不应该抛出异常，会返回默认回复
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getContent());
    }

    @Test
    @DisplayName("测试流式消息")
    void testStreamMessage() throws IOException {
        // Arrange
        SseEmitter emitter = new SseEmitter();

        // Act - 不应该抛出异常
        assertDoesNotThrow(() -> chatService.streamMessage(chatRequest, emitter));

        // SseEmitter 无法直接验证内容，但方法执行成功即可
        assertNotNull(emitter);
    }

    @Test
    @DisplayName("测试获取对话历史 - 空历史")
    void testGetHistory_EmptyHistory() {
        // Act
        List<ChatResponse> history = chatService.getHistory("non-existent-user", 10);

        // Assert
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("测试获取对话历史 - 有历史记录")
    void testGetHistory_WithHistory() {
        // Arrange - 先发送几条消息
        chatService.sendMessage(chatRequest);
        chatRequest.setMessage("第二个问题");
        chatService.sendMessage(chatRequest);

        // Act
        List<ChatResponse> history = chatService.getHistory("test-user-001", 10);

        // Assert
        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("测试获取对话历史 - 限制返回数量")
    void testGetHistory_Limit() {
        // Arrange - 发送 5 条消息
        int total = 5;
        int limit = 3;
        for (int i = 0; i < total; i++) {
            ChatRequest request = new ChatRequest();
            request.setUserId("test-user-001");
            request.setMessage("消息 " + i);
            chatService.sendMessage(request);
        }

        // Act - 只获取最近 3 条
        List<ChatResponse> history = chatService.getHistory("test-user-001", limit);

        // Assert
        assertEquals(limit, history.size());
        // 历史记录保存的是 ChatResponse，而 ChatResponse 的 content 是 AI 生成的回复
        // 我们只验证数量正确，因为回复内容是基于关键词生成的不一定包含原始输入
        // 但至少每个记录都不为空
        for (ChatResponse response : history) {
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
        }
    }

    @Test
    @DisplayName("测试清空对话历史")
    void testClearHistory() {
        // Arrange - 先添加一些历史
        chatService.sendMessage(chatRequest);
        List<ChatResponse> historyBefore = chatService.getHistory("test-user-001", 10);
        assertFalse(historyBefore.isEmpty());

        // Act
        chatService.clearHistory("test-user-001");

        // Assert
        List<ChatResponse> historyAfter = chatService.getHistory("test-user-001", 10);
        assertTrue(historyAfter.isEmpty());
    }

    @Test
    @DisplayName("测试清空不存在用户的历史")
    void testClearHistory_NonExistentUser() {
        // Act - 不应该抛出异常
        assertDoesNotThrow(() -> chatService.clearHistory("non-existent-user"));

        // Assert - 执行成功即可
        List<ChatResponse> history = chatService.getHistory("non-existent-user", 10);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("测试对话历史长度限制")
    void testConversationHistory_Limit() {
        // Arrange - 发送 150 条消息（限制是 100）
        for (int i = 0; i < 150; i++) {
            ChatRequest request = new ChatRequest();
            request.setUserId("test-user-001");
            request.setMessage("消息 " + i);
            chatService.sendMessage(request);
        }

        // Act - 获取全部历史
        List<ChatResponse> history = chatService.getHistory("test-user-001", 200);

        // Assert - 不超过 100 条（这个是核心验证点）
        assertTrue(history.size() <= 100, "历史记录应该被截断，不超过 100 条，实际是 " + history.size());
        // 每个记录都不为空
        for (ChatResponse response : history) {
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertFalse(response.getContent().isEmpty());
        }
    }

    @Test
    @DisplayName("测试多用户对话隔离")
    void testMultipleUsers_Isolation() {
        // Arrange - 用户 1 发送消息
        ChatRequest request1 = new ChatRequest();
        request1.setUserId("user-1");
        request1.setMessage("用户 1 的消息");
        chatService.sendMessage(request1);

        // 用户 2 发送消息
        ChatRequest request2 = new ChatRequest();
        request2.setUserId("user-2");
        request2.setMessage("用户 2 的消息");
        chatService.sendMessage(request2);

        // Act - 分别获取历史
        List<ChatResponse> history1 = chatService.getHistory("user-1", 10);
        List<ChatResponse> history2 = chatService.getHistory("user-2", 10);

        // Assert - 互相隔离
        assertEquals(1, history1.size());
        assertEquals(1, history2.size());
        // 验证两个用户的历史互不干扰
        assertNotNull(history1.get(0));
        assertNotNull(history2.get(0));
        // 记录都存在且不为空，这就足够验证隔离性了
        assertFalse(history1.get(0).getContent().isEmpty());
        assertFalse(history2.get(0).getContent().isEmpty());
    }

    @Test
    @DisplayName("测试英文问候")
    void testSendMessage_EnglishGreeting() {
        // Arrange
        chatRequest.setMessage("Hello");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("你好") || response.getContent().contains("JClaw"));
    }

    @Test
    @DisplayName("测试英文再见")
    void testSendMessage_EnglishBye() {
        // Arrange
        chatRequest.setMessage("Bye");

        // Act
        ChatResponse response = chatService.sendMessage(chatRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getContent().contains("再见"));
    }
}
