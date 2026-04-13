package com.jclaw.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Schedule 技能测试
 */
@SpringBootTest
class ScheduleSkillTest {
    
    @Autowired
    private SkillEngine skillEngine;
    
    @Test
    void testAddSchedule() {
        SkillResult result = skillEngine.execute("schedule", Map.of(
            "action", "add",
            "title", "测试日程",
            "date", "2026-04-14",
            "time", "10:00"
        ));
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testListSchedules() {
        SkillResult result = skillEngine.execute("schedule", Map.of(
            "action", "list"
        ));
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testShowToday() {
        SkillResult result = skillEngine.execute("schedule", Map.of(
            "action", "today"
        ));
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testAddScheduleMissingTitle() {
        SkillResult result = skillEngine.execute("schedule", Map.of(
            "action", "add"
        ));
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("title"));
    }
}
