package com.jclaw.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SkillEngineTest {
    
    @Autowired
    private SkillEngine skillEngine;
    
    @Test
    void testFileReadSkill() {
        SkillResult result = skillEngine.execute("file_read", Map.of(
            "path", "README.md"
        ));
        assertNotNull(result);
    }
    
    @Test
    void testBashSkill() {
        SkillResult result = skillEngine.execute("bash", Map.of(
            "command", "echo hello"
        ));
        assertTrue(result.isSuccess());
        assertTrue(result.getContent().contains("hello"));
    }
    
    @Test
    void testGrepSkill() {
        SkillResult result = skillEngine.execute("grep", Map.of(
            "pattern", "JClaw",
            "path", "README.md"
        ));
        assertNotNull(result);
    }
}
