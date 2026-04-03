package com.jclaw.schedule.service.impl;

import com.jclaw.schedule.service.CronService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cron 调度服务实现
 */
@Service
@Slf4j
public class CronServiceImpl implements CronService {

    private final TaskScheduler scheduler = new ThreadPoolTaskScheduler();
    private final Map<String, ScheduledTask> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        ((ThreadPoolTaskScheduler) scheduler).initialize();
        // 启动梦境时间任务（凌晨 2 点）
        startDreamTimeTask();
    }

    @Override
    public void addTask(String cron, Runnable task) {
        String taskId = "task_" + System.currentTimeMillis();
        scheduler.schedule(task, trigger -> {
            // 解析 cron 表达式
            return null;
        });
        log.info("添加定时任务：{} - {}", taskId, cron);
    }

    @Override
    public void removeTask(String taskId) {
        ScheduledTask task = tasks.remove(taskId);
        if (task != null) {
            task.cancel();
            log.info("移除定时任务：{}", taskId);
        }
    }

    @Override
    public void startDreamTimeTask() {
        // 每天凌晨 2 点执行
        String cron = "0 0 2 * * ?";
        scheduler.schedule(() -> {
            log.info("=== 梦境时间任务启动 ===");
            log.info("执行时间：{}", Instant.now());
            // TODO: 执行自我进化任务
            // 1. 整理记忆
            // 2. 知识萃取
            // 3. 生成改进建议
        }, trigger -> {
            // 计算下次执行时间（凌晨 2 点）
            ZonedDateTime next = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
                .plusDays(1)
                .withHour(2)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
            return next.toInstant();
        });
        log.info("启动梦境时间任务：每天凌晨 2 点");
    }

    static class ScheduledTask {
        private final Runnable task;
        private boolean cancelled = false;

        ScheduledTask(Runnable task) {
            this.task = task;
        }

        void run() {
            if (!cancelled) {
                task.run();
            }
        }

        void cancel() {
            cancelled = true;
        }
    }
}
