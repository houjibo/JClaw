package com.jclaw.test.service.impl;

import com.jclaw.test.service.TestRecommenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试推荐服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class TestRecommenderServiceTest {

    @InjectMocks
    private TestRecommenderServiceImpl testService;

    @Test
    void testRecommendTests() {
        List<Map<String, Object>> recommendations = testService.recommendTests("/test/file.java");

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        assertEquals("MemoryServiceTest", recommendations.get(0).get("testName"));
    }

    @Test
    void testAnalyzeCoverage() {
        Map<String, Object> coverage = testService.analyzeCoverage("/test/file.java");

        assertNotNull(coverage);
        assertEquals(75.5, coverage.get("lineCoverage"));
        assertEquals(68.2, coverage.get("branchCoverage"));
    }

    @Test
    void testGenerateTestSuggestions() {
        List<Map<String, Object>> suggestions = testService.generateTestSuggestions("code_001");

        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        assertEquals("添加边界测试", suggestions.get(0).get("suggestion"));
    }
}
