package com.jclaw.schedule.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cron 服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CronServiceTest {

    @InjectMocks
    private CronServiceImpl cronService;

    @Test
    void testAddTask() {
        // 测试添加定时任务
        assertDoesNotThrow(() -> {
            cronService.addTask("0 0 * * * ?", () -> System.out.println("test"));
        });
    }

    @Test
    void testRemoveTask() {
        // 测试移除定时任务
        assertDoesNotThrow(() -> {
            cronService.removeTask("task_001");
        });
    }

    @Test
    void testStartDreamTimeTask() {
        // 测试启动梦境时间任务
        assertDoesNotThrow(() -> {
            cronService.startDreamTimeTask();
        });
    }
}
