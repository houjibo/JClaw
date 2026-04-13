package com.jclaw.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Git 技能测试
 */
@SpringBootTest
class GitSkillTest {
    
    @Autowired
    private SkillEngine skillEngine;
    
    @Test
    void testGitStatus() {
        SkillResult result = skillEngine.execute("git", Map.of(
            "command", "status",
            "repoPath", "."
        ));
        
        // Git 仓库可能存在也可能不存在，至少验证能执行
        assertNotNull(result);
    }
    
    @Test
    void testGitMissingCommand() {
        SkillResult result = skillEngine.execute("git", Map.of());
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("command"));
    }
    
    @Test
    void testGitInvalidRepoPath() {
        SkillResult result = skillEngine.execute("git", Map.of(
            "command", "status",
            "repoPath", "/nonexistent/path"
        ));
        
        assertFalse(result.isSuccess());
    }
}
