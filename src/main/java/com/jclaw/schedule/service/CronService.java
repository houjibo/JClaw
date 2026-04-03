package com.jclaw.schedule.service;

/**
 * Cron 调度服务接口
 */
public interface CronService {
    
    /**
     * 添加定时任务
     */
    void addTask(String cron, Runnable task);
    
    /**
     * 移除定时任务
     */
    void removeTask(String taskId);
    
    /**
     * 启动梦境时间任务
     */
    void startDreamTimeTask();
}
