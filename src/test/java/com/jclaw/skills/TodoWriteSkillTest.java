package com.jclaw.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO 技能测试
 */
@SpringBootTest
class TodoWriteSkillTest {
    
    @Autowired
    private SkillEngine skillEngine;
    
    @Test
    void testCreateTodo() {
        SkillResult result = skillEngine.execute("todo_write", Map.of(
            "action", "create",
            "content", "测试任务"
        ));
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
    
    @Test
    void testListTodos() {
        SkillResult result = skillEngine.execute("todo_write", Map.of(
            "action", "list"
        ));
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testCreateTodoMissingContent() {
        SkillResult result = skillEngine.execute("todo_write", Map.of(
            "action", "create"
        ));
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("content"));
    }
    
    @Test
    void testInvalidAction() {
        SkillResult result = skillEngine.execute("todo_write", Map.of(
            "action", "invalid"
        ));
        
        assertFalse(result.isSuccess());
    }
}
