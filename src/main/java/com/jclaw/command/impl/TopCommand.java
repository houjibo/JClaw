package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.lang.management.*;
import java.util.*;

/**
 * Top 命令 - 系统资源监控
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class TopCommand extends Command {
    
    public TopCommand() {
        this.name = "top";
        this.description = "系统资源监控";
        this.aliases = Arrays.asList("resources");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        return showSystemResources();
    }
    
    private CommandResult showSystemResources() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        
        Runtime rt = Runtime.getRuntime();
        
        long uptime = runtime.getUptime();
        long days = uptime / (1000 * 60 * 60 * 24);
        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
        
        long heapUsed = memory.getHeapMemoryUsage().getUsed();
        long heapMax = memory.getHeapMemoryUsage().getMax();
        long nonHeapUsed = memory.getNonHeapMemoryUsage().getUsed();
        
        double systemLoad = os.getSystemLoadAverage();
        
        String report = String.format("""
            ## 系统资源监控
            
            ### 运行时间
            
            | 指标 | 值 |
            |------|------|
            | 运行时间 | %d天 %d小时 %d分钟 |
            | JVM 启动 | %s |
            
            ### CPU 信息
            
            | 指标 | 值 |
            |------|------|
            | 可用处理器 | %d |
            | 系统负载 | %.2f |
            | 系统 | %s %s |
            
            ### 内存使用
            
            | 指标 | 值 |
            |------|------|
            | Heap 使用 | %s |
            | Heap 最大 | %s |
            | Heap 使用率 | %.1f%% |
            | Non-Heap | %s |
            
            ### 线程
            
            | 指标 | 值 |
            |------|------|
            | 活动线程 | %d |
            | 峰值线程 | %d |
            | 守护线程 | %d |
            
            ### 进程
            
            | 指标 | 值 |
            |------|------|
            | PID | %s |
            | JVM 名称 | %s |
            
            ### 快速命令
            
            - `process` - 进程详情
            - `disk` - 磁盘使用
            - `env` - 环境变量
            """,
            days, hours, minutes,
            new java.util.Date(runtime.getStartTime()),
            rt.availableProcessors(),
            systemLoad,
            os.getName(), os.getVersion(),
            formatSize(heapUsed),
            formatSize(heapMax),
            (heapUsed * 100.0) / heapMax,
            formatSize(nonHeapUsed),
            thread.getThreadCount(),
            thread.getPeakThreadCount(),
            countDaemonThreads(),
            runtime.getName().split("@")[0],
            runtime.getName());
        
        return CommandResult.success("系统资源")
                .withDisplayText(report);
    }
    
    private int countDaemonThreads() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while (group.getParent() != null) {
            group = group.getParent();
        }
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        int count = 0;
        for (Thread t : threads) {
            if (t != null && t.isDaemon()) {
                count++;
            }
        }
        return count;
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    @Override
    public String getHelp() {
        return """
            命令：top
            别名：resources
            描述：系统资源监控
            
            用法：
              top                     # 显示系统资源
            
            示例：
              top
            """;
    }
}
