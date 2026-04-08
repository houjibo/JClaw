package com.jclaw.schedule.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cron 服务单元测试
 */
class CronServiceTest {

    private CronServiceImpl cronService;

    @BeforeEach
    void setUp() {
        cronService = new CronServiceImpl();
        // 手动初始化（@PostConstruct 在普通单元测试中不会自动调用）
        cronService.init();
    }

    @Test
    void testAddTask() {
        // 测试添加定时任务（Spring cron 需要 6 个字段：秒 分 时 日 月 周）
        assertDoesNotThrow(() -> {
            cronService.addTask("0 0 12 * * ?", () -> System.out.println("test"));
        });
    }

    @Test
    void testRemoveTask() {
        // 测试移除定时任务（不存在的任务不会抛异常）
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
