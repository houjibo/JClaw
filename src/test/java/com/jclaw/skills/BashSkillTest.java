package com.jclaw.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bash 技能测试
 */
@SpringBootTest
class BashSkillTest {
    
    @Autowired
    private SkillEngine skillEngine;
    
    @Test
    void testExecuteSimpleCommand() {
        SkillResult result = skillEngine.execute("bash", Map.of(
            "command", "echo hello"
        ));
        
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("hello"));
    }
    
    @Test
    void testExecuteCommandWithOutput() {
        SkillResult result = skillEngine.execute("bash", Map.of(
            "command", "pwd"
        ));
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getContent());
    }
    
    @Test
    void testExecuteMissingCommand() {
        SkillResult result = skillEngine.execute("bash", Map.of());
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("command"));
    }
    
    @Test
    void testExecuteDangerousCommand() {
        SkillResult result = skillEngine.execute("bash", Map.of(
            "command", "rm -rf /"
        ));
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("危险"));
    }
}
