package com.jclaw.schedule.service.impl;

import com.jclaw.memory.service.MemoryService;
import com.jclaw.memory.service.KnowledgeService;
import com.jclaw.schedule.service.CronService;
import com.jclaw.ai.service.AiIntentRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cron 调度服务实现
 */
@Service
@Slf4j
public class CronServiceImpl implements CronService {

    @Autowired
    private MemoryService memoryService;
    
    @Autowired
    private KnowledgeService knowledgeService;
    
    @Autowired
    private AiIntentRecognitionService aiIntentRecognitionService;
    
    private ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledTask> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("cron-scheduler-");
        scheduler.initialize();
        scheduler.start();
        // 启动梦境时间任务（凌晨 2 点）
        startDreamTimeTask();
    }

    @Override
    public void addTask(String cron, Runnable task) {
        String taskId = "task_" + System.currentTimeMillis();
        try {
            // 使用 CronTrigger 解析 cron 表达式
            org.springframework.scheduling.support.CronTrigger trigger = new org.springframework.scheduling.support.CronTrigger(cron);
            scheduler.schedule(task, trigger);
            tasks.put(taskId, new ScheduledTask(task));
            log.info("添加定时任务：{} - {}", taskId, cron);
        } catch (Exception e) {
            log.error("添加定时任务失败：{}", cron, e);
            throw new IllegalArgumentException("Invalid cron expression: " + cron, e);
        }
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
        scheduler.schedule(() -> {
            log.info("=== 梦境时间任务启动 ===");
            Instant now = Instant.now();
            log.info("执行时间：{}", now);
            
            try {
                // 1. 整理记忆（获取最近记忆列表）
                log.info("[1/3] 整理记忆...");
                List<com.jclaw.memory.entity.Memory> memories = memoryService.listMemories(1, 50);
                log.info("获取到 {} 条记忆", memories.size());
                // TODO: 实现记忆合并、清理、去重逻辑
                
                // 2. 知识萃取（从最近记忆中萃取知识）
                log.info("[2/3] 知识萃取...");
                int extractedCount = 0;
                for (com.jclaw.memory.entity.Memory memory : memories) {
                    try {
                        com.jclaw.memory.entity.Knowledge knowledge = 
                            knowledgeService.extractFromMemory(memory.getId());
                        if (knowledge != null) {
                            extractedCount++;
                            log.info("萃取知识：{} - {}", knowledge.getId(), knowledge.getTitle());
                        }
                    } catch (Exception e) {
                        log.warn("知识萃取失败：{}", memory.getId(), e);
                    }
                }
                log.info("知识萃取完成：萃取 {} 条知识", extractedCount);
                
                // 3. 生成改进建议
                log.info("[3/3] 生成改进建议...");
                String improvementPrompt = String.format("""
                    基于今天的 %d 条记忆和 %d 条知识萃取，请生成 3-5 条改进建议。
                    建议应该涵盖：
                    1. 工作流程优化
                    2. 技术改进
                    3. 个人成长
                    
                    请以简洁的列表形式返回。
                    """, memories.size(), extractedCount);
                
                try {
                    String improvements = aiIntentRecognitionService.callLLM(improvementPrompt);
                    log.info("改进建议:\n{}", improvements);
                } catch (Exception e) {
                    log.error("生成改进建议失败", e);
                }
                
                log.info("=== 梦境时间任务完成 ===");
                log.info("下次执行：{} 次日凌晨 2:00", 
                    ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).plusDays(1).withHour(2).withMinute(0));
            } catch (Exception e) {
                log.error("梦境时间任务执行失败", e);
            }
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
        log.info("启动梦境时间任务：每天凌晨 2:00 (Asia/Shanghai)");
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
