package com.jclaw.code.service;

import com.jclaw.code.dto.CodeExplanation;
import com.jclaw.code.service.impl.CodeServiceImpl;
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
 * 代码服务 AI 集成测试（使用智谱 GLM 4.7）
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("代码服务 AI 集成测试")
class CodeServiceAiIntegrationTest {
    
    @Autowired
    private CodeService codeService;
    
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".java");
        String testCode = """
            package com.example;
            
            import java.util.List;
            import java.util.ArrayList;
            
            /**
             * 用户服务类
             */
            public class UserService {
                
                public List<String> getUsers() {
                    List<String> users = new ArrayList<>();
                    users.add("Alice");
                    users.add("Bob");
                    return users;
                }
                
                public String getUserById(String id) {
                    if (id == null || id.isEmpty()) {
                        return null;
                    }
                    return "User-" + id;
                }
            }
            """;
        Files.writeString(tempFile, testCode);
    }
    
    @Test
    @DisplayName("测试 AI 代码解释 - 智谱 GLM 4.7")
    void testExplainCodeWithAi() {
        CodeExplanation result = codeService.explainCode(tempFile.toString(), "java");
        
        assertNotNull(result);
        assertEquals(tempFile.toString(), result.getFilePath());
        assertEquals("java", result.getLanguage());
        
        // AI 生成的摘要应该包含内容
        assertNotNull(result.getSummary());
        assertFalse(result.getSummary().isBlank());
        
        // 详细解释应该有内容
        assertNotNull(result.getDetailedExplanation());
        assertFalse(result.getDetailedExplanation().isBlank());
        
        System.out.println("=== AI 代码解释 ===");
        System.out.println("摘要：" + result.getSummary());
        System.out.println("详细解释：" + result.getDetailedExplanation());
        System.out.println("复杂度：" + result.getComplexity());
        System.out.println("行数：" + result.getLinesOfCode());
    }
    
    @Test
    @DisplayName("测试代码复杂度计算")
    void testComplexityCalculation() {
        CodeExplanation result = codeService.explainCode(tempFile.toString(), "java");
        
        assertNotNull(result);
        assertTrue(result.getComplexity() >= 1);
        assertTrue(result.getLinesOfCode() > 0);
        
        System.out.println("代码复杂度：" + result.getComplexity());
        System.out.println("代码行数：" + result.getLinesOfCode());
    }
    
    @Test
    @DisplayName("测试提取依赖")
    void testExtractDependencies() {
        CodeExplanation result = codeService.explainCode(tempFile.toString(), "java");
        
        assertNotNull(result);
        assertNotNull(result.getDependencies());
        
        System.out.println("依赖列表：" + result.getDependencies());
    }
    
    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException e) {
            // 忽略
        }
    }
}
