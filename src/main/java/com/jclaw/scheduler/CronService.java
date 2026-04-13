package com.jclaw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务服务
 */
@Slf4j
@Service
public class CronService {
    
    private final Map<String, Runnable> tasks = new ConcurrentHashMap<>();
    
    /**
     * 注册定时任务
     */
    public void register(String name, Runnable task, String cronExpression) {
        tasks.put(name, task);
        log.info("注册定时任务：{} ({})", name, cronExpression);
        // 注意：实际 cron 调度需要 @Scheduled 注解，这里简化实现
    }
    
    /**
     * 移除定时任务
     */
    public void unregister(String name) {
        tasks.remove(name);
        log.info("移除定时任务：{}", name);
    }
    
    /**
     * 列出所有任务
     */
    public List<String> listTasks() {
        return new ArrayList<>(tasks.keySet());
    }
    
    /**
     * 手动触发任务
     */
    public void trigger(String name) {
        Runnable task = tasks.get(name);
        if (task != null) {
            log.info("手动触发任务：{}", name);
            task.run();
        } else {
            log.warn("任务不存在：{}", name);
        }
    }
    
    /**
     * 示例：每日凌晨 2 点执行（梦境时间）
     */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Shanghai")
    public void dreamTimeTask() {
        log.info("=== 梦境时间任务执行 ===");
        // TODO: 实现梦境时间逻辑
        // 1. 阅读 memory 文件
        // 2. 更新 MEMORY.md
        // 3. 萌发创意
        // 4. 持续改进
    }
    
    /**
     * 示例：每小时心跳检查
     */
    @Scheduled(cron = "0 0 * * * ?", zone = "Asia/Shanghai")
    public void heartbeatTask() {
        log.info("心跳检查");
        // TODO: 实现心跳检查逻辑
    }
}
