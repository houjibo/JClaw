package com.jclaw.ai.service;

import com.jclaw.intent.entity.Intent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI 意图识别服务测试
 */
@SpringBootTest
@DisplayName("AI 意图识别服务测试")
class AiIntentRecognitionServiceTest {

    @Autowired
    private AiIntentRecognitionService aiIntentRecognitionService;

    @Test
    @DisplayName("测试 AI 意图识别 - 功能创建")
    void testRecognizeWithAI_Feature() {
        // Arrange
        String userInput = "我想创建一个用户管理系统，支持注册、登录和权限管理";

        // Act
        Intent intent = aiIntentRecognitionService.recognizeWithAI(userInput);

        // Assert
        assertNotNull(intent);
        assertNotNull(intent.getName());
        assertNotNull(intent.getType());
        assertNotNull(intent.getDescription());
        assertTrue(intent.getPriority() >= 1 && intent.getPriority() <= 5);
    }

    @Test
    @DisplayName("测试 AI 意图识别 - Bug 修复")
    void testRecognizeWithAI_Bug() {
        // Arrange
        String userInput = "修复登录页面的空指针异常，用户点击登录按钮时崩溃";

        // Act
        Intent intent = aiIntentRecognitionService.recognizeWithAI(userInput);

        // Assert
        assertNotNull(intent);
        assertEquals("bug", intent.getType().toLowerCase());
    }

    @Test
    @DisplayName("测试 AI 意图识别 - 查询功能")
    void testRecognizeWithAI_Query() {
        // Arrange
        String userInput = "查询所有用户的订单记录，按时间排序";

        // Act
        Intent intent = aiIntentRecognitionService.recognizeWithAI(userInput);

        // Assert
        assertNotNull(intent);
        assertEquals("query", intent.getType().toLowerCase());
    }

    @Test
    @DisplayName("测试生成澄清问题")
    void testGenerateClarificationQuestions() {
        // Arrange
        Intent intent = Intent.builder()
            .name("用户管理系统")
            .description("创建一个完整的用户管理系统")
            .type("feature")
            .context("用户需要注册、登录、权限管理等功能")
            .build();

        // Act
        List<String> questions = aiIntentRecognitionService.generateClarificationQuestions(intent);

        // Assert
        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertTrue(questions.size() >= 3);
    }

    @Test
    @DisplayName("测试降级方案 - 无效输入")
    void testRecognizeWithDefault() {
        // Arrange
        String userInput = "asdfghjkl";  // 无意义输入

        // Act
        Intent intent = aiIntentRecognitionService.recognizeWithAI(userInput);

        // Assert
        assertNotNull(intent);
        assertNotNull(intent.getName());
        assertNotNull(intent.getType());
    }
}
