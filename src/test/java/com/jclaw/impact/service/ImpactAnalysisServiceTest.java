package com.jclaw.impact.service;

import com.jclaw.impact.service.impl.ImpactAnalysisServiceImpl;
import com.jclaw.trace.service.ImpactAnalysis;
import com.jclaw.trace.service.TraceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ImpactAnalysisService 单元测试
 */
class ImpactAnalysisServiceTest {

    @Mock
    private TraceService traceService;

    @InjectMocks
    private ImpactAnalysisServiceImpl impactAnalysisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAnalyzeImpact() {
        // 准备测试数据
        ImpactAnalysis mockAnalysis = ImpactAnalysis.builder()
            .riskScore(50.0)
            .affectedNodes(java.util.Collections.emptyList())
            .relations(java.util.Collections.emptyList())
            .statistics(new java.util.HashMap<>())
            .build();

        when(traceService.analyzeImpact("code-unit-id")).thenReturn(mockAnalysis);

        // 执行测试
        ImpactAnalysis result = impactAnalysisService.analyzeImpact("code-unit-id");

        // 验证结果
        assertNotNull(result);
        assertEquals(50.0, result.getRiskScore());
        verify(traceService, times(1)).analyzeImpact("code-unit-id");
    }

    @Test
    void testCalculateRisk() {
        // 准备测试数据
        ImpactAnalysis mockAnalysis = ImpactAnalysis.builder()
            .riskScore(75.5)
            .affectedNodes(java.util.Collections.emptyList())
            .relations(java.util.Collections.emptyList())
            .statistics(new java.util.HashMap<>())
            .build();

        when(traceService.analyzeImpact("code-unit-id")).thenReturn(mockAnalysis);

        // 执行测试
        double result = impactAnalysisService.calculateRisk("code-unit-id");

        // 验证结果
        assertEquals(75.5, result, 0.01);
    }

    @Test
    void testAnalyzeChange_NonGitRepo() {
        // 执行测试（非 Git 仓库，返回空分析）
        ImpactAnalysis result = impactAnalysisService.analyzeChange("/non/git/repo");

        // 验证结果
        assertNotNull(result);
        // 虽然没有 Git 变更，仍然返回对象
        verify(traceService, never()).analyzeImpact(any());
    }

    @Test
    void testAnalyzeChange_HandlesException() {
        // 执行测试，即使发生异常也不应该崩溃
        ImpactAnalysis result = impactAnalysisService.analyzeChange("/invalid/path");

        // 验证结果（不抛出异常，返回空对象）
        assertNotNull(result);
    }
}
