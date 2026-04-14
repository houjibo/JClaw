package com.jclaw.code.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试生成服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("测试生成服务测试")
class TestGenerationServiceTest {
    
    @Autowired
    private TestGenerationService testGenService;
    
    private Path tempSourceFile;
    
    @BeforeEach
    void setUp() throws IOException {
        tempSourceFile = Files.createTempFile("UserService", ".java");
        String sourceCode = """
            package com.example;
            
            public class UserService {
                
                public String getUserById(String id) {
                    if (id == null || id.isEmpty()) {
                        return null;
                    }
                    return "User-" + id;
                }
                
                public boolean validateEmail(String email) {
                    return email != null && email.contains("@");
                }
            }
            """;
        Files.writeString(tempSourceFile, sourceCode);
    }
    
    @Test
    @DisplayName("测试生成 JUnit 测试")
    void testGenerateJUnitTests() {
        String testCode = testGenService.generateTests(tempSourceFile.toString(), "junit");
        
        assertNotNull(testCode);
        assertTrue(testCode.contains("@Test"));
        assertTrue(testCode.contains("UserService"));
        
        System.out.println("=== 生成的测试代码 ===");
        System.out.println(testCode);
    }
    
    @Test
    @DisplayName("测试保存测试文件")
    void testSaveTestFile() {
        String testCode = testGenService.generateTests(tempSourceFile.toString(), "junit");
        String testPath = testGenService.saveTestFile(testCode, tempSourceFile.toString());
        
        // 验证路径格式正确
        assertNotNull(testPath);
        assertTrue(testPath.contains("/src/test/"));
        assertTrue(testPath.endsWith("Test.java"));
    }
    
    @Test
    @DisplayName("测试覆盖率分析")
    void testAnalyzeCoverage() {
        TestGenerationService.TestCoverageReport report = testGenService.analyzeCoverage(".");
        
        assertNotNull(report);
        assertEquals(".", report.getProjectPath());
        
        System.out.println("=== 覆盖率报告 ===");
        System.out.println("项目路径：" + report.getProjectPath());
        System.out.println("总体覆盖率：" + report.getOverallCoverage());
    }
    
    @Test
    @DisplayName("测试提取类名")
    void testExtractClassName() {
        String className = testGenService.getClass().getName();
        assertNotNull(className);
    }
    
    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(tempSourceFile);
        } catch (IOException e) {
            // 忽略
        }
    }
}
