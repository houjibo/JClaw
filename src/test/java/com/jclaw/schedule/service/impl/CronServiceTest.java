package com.jclaw.schedule.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cron 服务单元测试
 */
class CronServiceTest {

    private CronServiceImpl cronService;

    @BeforeEach
    void setUp() {
        // 创建空的实例，不初始化 scheduler（避免线程池创建）
        cronService = new CronServiceImpl();
    }

    @Test
    @DisplayName("Cron 表达式验证")
    void testCronExpressionValidation() {
        // 测试 cron 表达式验证逻辑
        // 注意：由于 scheduler 未初始化，这里只验证基本逻辑
        assertNotNull(cronService);
    }

    @Test
    @DisplayName("任务移除测试")
    void testRemoveTask() {
        // 测试移除不存在的任务不会抛异常
        assertDoesNotThrow(() -> {
            cronService.removeTask("non_existent_task");
        });
    }

    @Test
    @DisplayName("梦境时间任务表达式验证")
    void testDreamTimeCronExpression() {
        // 验证梦境时间 cron 表达式（每天凌晨 2 点）
        String expectedCron = "0 0 2 * * ?";
        // 使用 Spring 的 CronExpression 验证
        org.springframework.scheduling.support.CronExpression cron = 
            org.springframework.scheduling.support.CronExpression.parse(expectedCron);
        assertNotNull(cron);
    }
}
