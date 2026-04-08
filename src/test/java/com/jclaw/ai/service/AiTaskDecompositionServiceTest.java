package com.jclaw.ai.service;

import com.jclaw.intent.entity.Intent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI 任务分解服务测试
 */
@SpringBootTest
@DisplayName("AI 任务分解服务测试")
class AiTaskDecompositionServiceTest {

    @Autowired
    private AiTaskDecompositionService aiTaskDecompositionService;

    @Test
    @DisplayName("测试 AI 任务分解")
    void testDecomposeWithAI() {
        // Arrange
        Intent intent = Intent.builder()
            .name("电商订单系统")
            .description("实现一个完整的电商订单系统，包括下单、支付、发货等功能")
            .type("feature")
            .priority(1)
            .build();

        // Act
        List<Map<String, Object>> tasks = aiTaskDecompositionService.decomposeWithAI(intent);

        // Assert
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.size() >= 3);
        
        // 验证任务结构
        for (Map<String, Object> task : tasks) {
            assertTrue(task.containsKey("title"));
            assertTrue(task.containsKey("description"));
            assertTrue(task.containsKey("type"));
            assertTrue(task.containsKey("priority"));
            assertTrue(task.containsKey("agent"));
        }
    }

    @Test
    @DisplayName("测试 AI 复杂度评估")
    void testEstimateComplexityWithAI() {
        // Arrange
        Map<String, Object> task = Map.of(
            "title", "实现分布式事务",
            "description", "使用 Seata 实现分布式事务管理，支持 TCC 和 AT 模式",
            "type", "coding"
        );

        // Act
        int complexity = aiTaskDecompositionService.estimateComplexityWithAI(task);

        // Assert
        assertTrue(complexity >= 1 && complexity <= 10);
    }

    @Test
    @DisplayName("测试 AI Agent 分配")
    void testAssignAgentWithAI() {
        // Arrange
        Map<String, Object> designTask = Map.of(
            "title", "系统架构设计",
            "description", "设计微服务架构和数据库 schema",
            "type", "design"
        );

        Map<String, Object> codingTask = Map.of(
            "title", "实现用户服务",
            "description", "实现用户注册、登录、权限管理等功能",
            "type", "coding"
        );

        Map<String, Object> testingTask = Map.of(
            "title", "编写集成测试",
            "description", "编写端到端集成测试用例",
            "type", "testing"
        );

        // Act & Assert
        String designAgent = aiTaskDecompositionService.assignAgentWithAI(designTask);
        assertNotNull(designAgent);
        assertTrue(List.of("architect", "fullstack", "qa", "devops", "analyst").contains(designAgent));

        String codingAgent = aiTaskDecompositionService.assignAgentWithAI(codingTask);
        assertNotNull(codingAgent);

        String testingAgent = aiTaskDecompositionService.assignAgentWithAI(testingTask);
        assertNotNull(testingAgent);
    }

    @Test
    @DisplayName("测试降级方案 - 默认任务分解")
    void testDecomposeWithDefault() {
        // Arrange
        Intent intent = Intent.builder()
            .name("测试项目")
            .description("测试描述")
            .type("task")
            .build();

        // Act
        List<Map<String, Object>> tasks = aiTaskDecompositionService.decomposeWithAI(intent);

        // Assert
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
    }

    @Test
    @DisplayName("测试降级方案 - 默认复杂度评估")
    void testEstimateComplexityWithDefault() {
        // Arrange
        Map<String, Object> task = Map.of(
            "description", "这是一个非常复杂的任务需要很多时间",
            "type", "coding"
        );

        // Act - 使用公共方法（会内部调用默认实现当 AI 失败时）
        int complexity = aiTaskDecompositionService.estimateComplexityWithAI(task);

        // Assert
        assertTrue(complexity >= 1 && complexity <= 10);
    }
}
