package com.jclaw.impact.service.impl;

import com.jclaw.trace.service.ImpactAnalysis;
import com.jclaw.trace.service.TraceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 影响分析服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ImpactAnalysisServiceTest {

    @Mock
    private TraceService traceService;

    @InjectMocks
    private ImpactAnalysisServiceImpl impactService;

    @Test
    void testAnalyzeImpact() {
        ImpactAnalysis mockAnalysis = ImpactAnalysis.builder()
            .codeUnitId("code_001")
            .riskScore(75.0)
            .build();
        
        when(traceService.analyzeImpact("code_001")).thenReturn(mockAnalysis);

        ImpactAnalysis result = impactService.analyzeImpact("code_001");

        assertNotNull(result);
        assertEquals(75.0, result.getRiskScore());
        verify(traceService, times(1)).analyzeImpact("code_001");
    }

    @Test
    void testCalculateRisk() {
        ImpactAnalysis mockAnalysis = ImpactAnalysis.builder()
            .riskScore(60.0)
            .build();
        
        when(traceService.analyzeImpact("code_001")).thenReturn(mockAnalysis);

        double risk = impactService.calculateRisk("code_001");

        assertEquals(60.0, risk);
    }

    @Test
    void testAnalyzeChange() {
        ImpactAnalysis mockAnalysis = ImpactAnalysis.builder()
            .riskScore(50.0)
            .build();
        
        when(traceService.analyzeImpact(any())).thenReturn(mockAnalysis);

        ImpactAnalysis result = impactService.analyzeChange("/test/file.java");

        assertNotNull(result);
        verify(traceService, times(1)).analyzeImpact(any());
    }
}
