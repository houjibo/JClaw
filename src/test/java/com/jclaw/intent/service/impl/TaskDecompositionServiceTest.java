package com.jclaw.intent.service.impl;

import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.service.TaskDecompositionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务分解服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class TaskDecompositionServiceTest {

    @InjectMocks
    private TaskDecompositionServiceImpl taskDecompositionService;

    @Test
    void testDecompose() {
        Intent intent = Intent.builder()
            .id("intent_001")
            .name("测试意图")
            .description("测试描述")
            .build();

        List<Map<String, Object>> tasks = taskDecompositionService.decompose(intent);

        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
        assertEquals("设计数据库 schema", tasks.get(0).get("title"));
    }

    @Test
    void testEstimateComplexity() {
        // 使用英文描述确保长度计算准确（每个字符算 1）
        Map<String, Object> task = Map.of(
            "description", "This is a very complex task that requires a lot of time and effort to implement properly with multiple modules",
            "type", "coding"
        );

        int complexity = taskDecompositionService.estimateComplexity(task);
        
        // 描述长度 108 字符，108/50 = 2，coding 类型 multiplier=2，2*2=4
        assertTrue(complexity > 0, "Complexity should be > 0, but was: " + complexity);
        assertEquals(4, complexity);
    }

    @Test
    void testAssignAgent() {
        Map<String, Object> codingTask = Map.of("type", "coding");
        Map<String, Object> designTask = Map.of("type", "design");
        Map<String, Object> testTask = Map.of("type", "testing");

        assertEquals("fullstack", taskDecompositionService.assignAgent(codingTask));
        assertEquals("architect", taskDecompositionService.assignAgent(designTask));
        assertEquals("qa", taskDecompositionService.assignAgent(testTask));
    }
}
