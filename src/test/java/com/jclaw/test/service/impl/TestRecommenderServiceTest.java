package com.jclaw.test.service.impl;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试推荐服务测试（Jacoco 集成）
 */
@SpringBootTest
@DisplayName("测试推荐服务测试")
class TestRecommenderServiceTest {

    @Autowired
    private TestRecommenderServiceImpl testRecommenderService;

    @Test
    @DisplayName("测试推荐测试用例")
    void testRecommendTests() {
        // Arrange
        String filePath = "/src/main/java/com/example/UserService.java";

        // Act
        List<Map<String, Object>> recommendations = testRecommenderService.recommendTests(filePath);

        // Assert
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // 验证推荐结构
        for (Map<String, Object> rec : recommendations) {
            assertTrue(rec.containsKey("testName"));
            assertTrue(rec.containsKey("reason"));
            assertTrue(rec.containsKey("priority"));
            assertTrue(rec.containsKey("confidence"));
        }
    }

    @Test
    @DisplayName("测试覆盖率分析")
    void testAnalyzeCoverage() {
        // Arrange
        String filePath = "/src/main/java/com/example/UserService.java";

        // Act
        Map<String, Object> coverage = testRecommenderService.analyzeCoverage(filePath);

        // Assert
        assertNotNull(coverage);
        assertTrue(coverage.containsKey("filePath"));
        assertTrue(coverage.containsKey("lineCoverage"));
        assertTrue(coverage.containsKey("branchCoverage"));
        assertTrue(coverage.containsKey("uncoveredLines"));
        
        // 验证覆盖率范围
        Double lineCoverage = (Double) coverage.get("lineCoverage");
        assertTrue(lineCoverage >= 0 && lineCoverage <= 100);
    }

    @Test
    @DisplayName("测试生成测试建议")
    void testGenerateTestSuggestions() {
        // Arrange
        String codeUnitId = "code_unit_001";

        // Act
        List<Map<String, Object>> suggestions = testRecommenderService.generateTestSuggestions(codeUnitId);

        // Assert
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        
        // 验证建议结构
        for (Map<String, Object> suggestion : suggestions) {
            assertTrue(suggestion.containsKey("suggestion"));
            assertTrue(suggestion.containsKey("reason"));
            assertTrue(suggestion.containsKey("priority"));
            assertTrue(suggestion.containsKey("effort"));
        }
    }

    @Test
    @DisplayName("测试覆盖率趋势分析")
    void testGetCoverageTrend() {
        // Arrange
        String projectId = "project_001";
        int days = 7;

        // Act
        Map<String, Object> trend = testRecommenderService.getCoverageTrend(projectId, days);

        // Assert
        assertNotNull(trend);
        assertTrue(trend.containsKey("projectId"));
        assertTrue(trend.containsKey("days"));
        assertTrue(trend.containsKey("trend"));
        assertTrue(trend.containsKey("currentCoverage"));
        assertTrue(trend.containsKey("trendDirection"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> trendData = (List<Map<String, Object>>) trend.get("trend");
        assertNotNull(trendData);
        assertEquals(days + 1, trendData.size());  // 包括今天
    }

    @Test
    @DisplayName("测试从 Jacoco 报告分析覆盖率")
    void testAnalyzeCoverageFromJacocoReport() {
        // Arrange
        String reportPath = "/non/existent/report.xml";

        // Act
        Map<String, Object> result = testRecommenderService.analyzeCoverageFromJacocoReport(reportPath);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error") || result.containsKey("lineCoverage"));
    }

    @Test
    @DisplayName("测试推荐测试 - 不同文件类型")
    void testRecommendTests_DifferentFileTypes() {
        // Arrange
        String serviceFile = "/src/main/java/com/example/UserService.java";
        String controllerFile = "/src/main/java/com/example/UserController.java";
        String entityFile = "/src/main/java/com/example/User.java";

        // Act
        List<Map<String, Object>> serviceRecs = testRecommenderService.recommendTests(serviceFile);
        List<Map<String, Object>> controllerRecs = testRecommenderService.recommendTests(controllerFile);
        List<Map<String, Object>> entityRecs = testRecommenderService.recommendTests(entityFile);

        // Assert
        assertNotNull(serviceRecs);
        assertNotNull(controllerRecs);
        assertNotNull(entityRecs);
        
        // 验证推荐包含对应的测试类名
        assertTrue(serviceRecs.stream()
            .anyMatch(r -> r.get("testName").toString().contains("UserServiceTest")));
    }

    @Test
    @DisplayName("测试覆盖率分析 - 边界情况")
    void testAnalyzeCoverage_EdgeCases() {
        // Arrange
        String emptyPath = "";

        // Act & Assert - 空路径应该返回错误信息
        assertDoesNotThrow(() -> {
            Map<String, Object> result = testRecommenderService.analyzeCoverage(emptyPath);
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
        });
        
        // null 路径应该返回包含错误信息的 Map
        Map<String, Object> nullResult = testRecommenderService.analyzeCoverage(null);
        assertNotNull(nullResult);
        assertTrue(nullResult.containsKey("error"));
    }
}
