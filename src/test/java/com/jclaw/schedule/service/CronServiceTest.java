package com.jclaw.schedule.service;

import com.jclaw.schedule.service.impl.CronServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CronService 单元测试
 */
class CronServiceTest {

    @InjectMocks
    private CronServiceImpl cronService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 手动调用 @PostConstruct 初始化 scheduler
        cronService.init();
    }

    @Test
    void testInit() {
        // 验证初始化不会抛出异常
        assertDoesNotThrow(() -> cronService.init());
    }

    @Test
    void testAddTask_ValidCron() {
        // 验证添加有效 cron 表达式不会抛出异常
        assertDoesNotThrow(() -> {
            cronService.addTask("0 0 2 * * ?", () -> {
                // do nothing
            });
        });
    }

    @Test
    void testAddTask_InvalidCron() {
        // 验证无效 cron 表达式会抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            cronService.addTask("invalid cron", () -> {
                // do nothing
            });
        });
    }

    @Test
    void testRemoveTask() {
        // 验证移除任务不会抛出异常
        assertDoesNotThrow(() -> {
            cronService.removeTask("non-existent-task");
        });
    }

    @Test
    void testStartDreamTimeTask() {
        // 验证启动梦境时间任务不会抛出异常
        assertDoesNotThrow(() -> {
            cronService.startDreamTimeTask();
        });
    }

    @Test
    void testConcurrentTasks() {
        // 测试添加多个任务
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                int taskId = i;
                cronService.addTask("0 0 * * * ?", () -> {
                    // do nothing
                });
            }
        });
    }
}
