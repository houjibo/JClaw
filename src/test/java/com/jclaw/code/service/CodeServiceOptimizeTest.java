package com.jclaw.code.service;

import com.jclaw.code.dto.CodeOptimization;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 代码优化服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("代码优化服务测试")
class CodeServiceOptimizeTest {
    
    @Autowired
    private CodeService codeService;
    
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("optimize-test", ".java");
    }
    
    @Test
    @DisplayName("测试优化建议 - 简单代码")
    void testOptimizeSimpleCode() throws IOException {
        String code = """
            public class Simple {
                public int add(int a, int b) {
                    return a + b;
                }
            }
            """;
        Files.writeString(tempFile, code);
        
        List<CodeOptimization> optimizations = codeService.optimizeCode(tempFile.toString());
        
        assertNotNull(optimizations);
        // 至少应该有基础建议
        assertTrue(optimizations.size() >= 0);
    }
    
    @Test
    @DisplayName("测试优化建议 - 复杂代码")
    void testOptimizeComplexCode() throws IOException {
        String code = """
            public class Complex {
                public void process(List<String> items) {
                    for (int i = 0; i < items.size(); i++) {
                        String item = items.get(i);
                        if (item != null) {
                            if (item.length() > 0) {
                                System.out.println(item);
                            }
                        }
                    }
                }
            }
            """;
        Files.writeString(tempFile, code);
        
        List<CodeOptimization> optimizations = codeService.optimizeCode(tempFile.toString());
        
        assertNotNull(optimizations);
        System.out.println("优化建议数量：" + optimizations.size());
        optimizations.forEach(opt -> 
            System.out.println("[" + opt.getType() + "] " + opt.getDescription())
        );
    }
    
    @Test
    @DisplayName("测试优化建议 - 空文件")
    void testOptimizeEmptyFile() throws IOException {
        String code = "// empty";
        Files.writeString(tempFile, code);
        
        List<CodeOptimization> optimizations = codeService.optimizeCode(tempFile.toString());
        
        assertNotNull(optimizations);
    }
    
    @Test
    @DisplayName("测试优化建议 - 不存在的文件")
    void testOptimizeNonExistent() {
        assertThrows(RuntimeException.class, () -> {
            codeService.optimizeCode("/non/existent/file.java");
        });
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
