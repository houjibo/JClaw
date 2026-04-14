package com.jclaw.code.service.impl;

import com.jclaw.code.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 代码工具服务测试
 */
@DisplayName("代码工具服务测试")
class CodeServiceImplTest {
    
    private CodeServiceImpl codeService;
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        codeService = new CodeServiceImpl();
        
        // 创建临时测试文件
        tempFile = Files.createTempFile("test", ".java");
        String testCode = """
            package com.example;
            
            import java.util.List;
            import java.util.ArrayList;
            
            public class TestClass {
                public void method1() {
                    if (true) {
                        for (int i = 0; i < 10; i++) {
                            System.out.println(i);
                        }
                    }
                }
                
                private String method2(String input) {
                    return input.trim();
                }
            }
            """;
        Files.writeString(tempFile, testCode);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }
    
    @Test
    @DisplayName("测试代码解释")
    void testExplainCode() {
        CodeExplanation result = codeService.explainCode(tempFile.toString(), "java");
        
        assertNotNull(result);
        assertEquals(tempFile.toString(), result.getFilePath());
        assertEquals("java", result.getLanguage());
        assertNotNull(result.getSummary());
        assertTrue(result.getLinesOfCode() > 0);
        assertTrue(result.getComplexity() >= 1);
    }
    
    @Test
    @DisplayName("测试代码优化建议")
    void testOptimizeCode() {
        List<CodeOptimization> result = codeService.optimizeCode(tempFile.toString());
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getType());
        assertNotNull(result.get(0).getSuggestion());
    }
    
    @Test
    @DisplayName("测试安全扫描")
    void testSecurityScan() {
        SecurityReport result = codeService.securityScan(tempFile.toString());
        
        assertNotNull(result);
        assertEquals(tempFile.toString(), result.getFilePath());
        assertTrue(result.getRiskScore() >= 0);
        assertNotNull(result.getRecommendations());
    }
    
    @Test
    @DisplayName("测试生成文档")
    void testGenerateDocs() {
        CodeDocumentation result = codeService.generateDocs(tempFile.toString());
        
        assertNotNull(result);
        assertEquals(tempFile.toString(), result.getFilePath());
        assertNotNull(result.getOverview());
    }
    
    @Test
    @DisplayName("测试调试信息提取")
    void testDebugCode() {
        DebugInfo result = codeService.debugCode(tempFile.toString(), 5);
        
        assertNotNull(result);
        assertEquals(tempFile.toString(), result.getFilePath());
        assertEquals(5, result.getLineNumber());
        assertNotNull(result.getContext());
    }
    
    @Test
    @DisplayName("测试调试信息 - 行号超出范围")
    void testDebugCodeInvalidLine() {
        assertThrows(IllegalArgumentException.class, () -> {
            codeService.debugCode(tempFile.toString(), 1000);
        });
    }
}
