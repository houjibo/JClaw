package com.jclaw.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件读取技能测试
 */
@SpringBootTest
class FileReadSkillTest {
    
    @Autowired
    private SkillEngine skillEngine;
    
    @Test
    void testReadExistingFile() throws Exception {
        // 创建测试文件
        Path testFile = Files.createTempFile("test", ".txt");
        Files.writeString(testFile, "test content");
        
        try {
            SkillResult result = skillEngine.execute("file_read", Map.of(
                "path", testFile.toString()
            ));
            
            assertTrue(result.isSuccess());
            assertTrue(result.getContent().contains("test content"));
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
    
    @Test
    void testReadNonExistentFile() {
        SkillResult result = skillEngine.execute("file_read", Map.of(
            "path", "/nonexistent/file.txt"
        ));
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不存在"));
    }
    
    @Test
    void testReadMissingPath() {
        SkillResult result = skillEngine.execute("file_read", Map.of());
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("path"));
    }
}
