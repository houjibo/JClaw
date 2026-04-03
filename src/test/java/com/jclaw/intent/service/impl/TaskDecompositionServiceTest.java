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
        Map<String, Object> task = Map.of(
            "description", "这是一个非常复杂的任务需要很多时间",
            "type", "coding"
        );

        int complexity = taskDecompositionService.estimateComplexity(task);

        assertTrue(complexity > 0);
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
