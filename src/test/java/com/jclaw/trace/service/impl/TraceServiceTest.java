package com.jclaw.trace.service.impl;

import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.entity.CallRelationship;
import com.jclaw.trace.mapper.CodeUnitMapper;
import com.jclaw.trace.mapper.CallRelationshipMapper;
import com.jclaw.trace.service.ImpactAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 代码追溯服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class TraceServiceTest {

    @Mock
    private CodeUnitMapper codeUnitMapper;

    @Mock
    private CallRelationshipMapper callRelationshipMapper;

    @InjectMocks
    private TraceServiceImpl traceService;

    @Test
    void testParseCodeFile() {
        String filePath = "/src/main/java/Test.java";

        CodeUnit result = traceService.parseCodeFile(filePath);

        assertNotNull(result);
        assertEquals(filePath, result.getFilePath());
    }

    @Test
    void testGetCallChain() {
        when(callRelationshipMapper.selectList(any())).thenReturn(List.of());

        List<CallRelationship> result = traceService.getCallChain("code_001");

        assertNotNull(result);
        verify(callRelationshipMapper, times(1)).selectList(any());
    }

    @Test
    void testAnalyzeImpact() {
        when(callRelationshipMapper.selectList(any())).thenReturn(List.of());

        ImpactAnalysis result = traceService.analyzeImpact("code_001");

        assertNotNull(result);
        assertEquals("code_001", result.getCodeUnitId());
        assertTrue(result.getRiskScore() >= 0);
    }

    @Test
    void testGetCodeUnit() {
        CodeUnit mockUnit = CodeUnit.builder()
            .id("code_001")
            .unitName("TestClass")
            .build();
        
        when(codeUnitMapper.selectById("code_001")).thenReturn(mockUnit);

        CodeUnit result = traceService.getCodeUnit("code_001");

        assertNotNull(result);
        assertEquals("TestClass", result.getUnitName());
    }
}
