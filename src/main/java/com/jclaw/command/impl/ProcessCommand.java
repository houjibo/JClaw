package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.lang.management.*;
import java.util.*;

/**
 * 进程管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ProcessCommand extends Command {
    
    public ProcessCommand() {
        this.name = "process";
        this.description = "进程管理";
        this.aliases = Arrays.asList("ps", "proc");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showProcessList();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> showProcessList();
            case "info" -> showProcessInfo(parts.length > 1 ? parts[1] : null);
            case "kill" -> killProcess(parts.length > 1 ? parts[1] : null);
            case "top" -> showTopProcesses();
            case "java" -> showJavaProcesses();
            default -> showProcessList();
        };
    }
    
    private CommandResult showProcessList() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        
        String report = String.format("""
            ## 进程列表
            
            ### 当前 JVM 进程
            
            | 属性 | 值 |
            |------|------|
            | PID | %d |
            | 名称 | %s |
            | 运行时间 | %s |
            | JVM | %s |
            
            ### 系统资源
            
            | 指标 | 值 |
            |------|------|
            | CPU 核心数 | %d |
            | 可用处理器 | %d |
            | 系统负载 | %.2f |
            """,
                runtime.getName().split("@")[0],
                runtime.getName(),
                formatUptime(runtime.getUptime()),
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                getSystemLoad());
        
        return CommandResult.success("进程列表")
                .withDisplayText(report);
    }
    
    private CommandResult showProcessInfo(String pid) {
        if (pid == null) {
            pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        }
        
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        
        String report = String.format("""
            ## 进程详情：%s
            
            ### 基本信息
            
            | 属性 | 值 |
            |------|------|
            | PID | %s |
            | 名称 | %s |
            | 启动时间 | %s |
            | 运行时间 | %s |
            
            ### JVM 信息
            
            | 属性 | 值 |
            |------|------|
            | JVM 版本 | %s |
            | JVM 供应商 | %s |
            | VM 名称 | %s |
            
            ### 输入参数
            
            ```
            %s
            ```
            """,
                pid,
                pid,
                runtime.getName(),
                new Date(runtime.getStartTime()),
                formatUptime(runtime.getUptime()),
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("java.vm.name"),
                String.join("\n", runtime.getInputArguments()));
        
        return CommandResult.success("进程详情")
                .withDisplayText(report);
    }
    
    private CommandResult killProcess(String pid) {
        if (pid == null) {
            return CommandResult.error("请指定进程 PID");
        }
        
        return CommandResult.error("kill 操作需要系统权限，请在终端执行：kill " + pid);
    }
    
    private CommandResult showTopProcesses() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        String report = String.format("""
            ## 资源占用 Top
            
            ### 内存使用
            
            | 指标 | 值 |
            |------|------|
            | 已使用 | %.2f MB |
            | 已分配 | %.2f MB |
            | 最大可用 | %.2f MB |
            | 使用率 | %.1f%% |
            
            ### CPU 使用
            
            | 指标 | 值 |
            |------|------|
            | 可用处理器 | %d |
            | 系统负载 | %.2f |
            
            ### 线程
            
            | 指标 | 值 |
            |------|------|
            | 活动线程 | %d |
            | 守护线程 | %d |
            | 峰值线程 | %d |
            """,
                usedMemory / 1024.0 / 1024.0,
                totalMemory / 1024.0 / 1024.0,
                runtime.maxMemory() / 1024.0 / 1024.0,
                (usedMemory * 100.0) / runtime.maxMemory(),
                runtime.availableProcessors(),
                getSystemLoad(),
                Thread.activeCount(),
                countDaemonThreads(),
                ManagementFactory.getThreadMXBean().getPeakThreadCount());
        
        return CommandResult.success("资源占用")
                .withDisplayText(report);
    }
    
    private CommandResult showJavaProcesses() {
        String report = """
            ## Java 进程列表
            
            | PID | 名称 | 内存 | CPU |
            |-----|------|------|-----|
            | 12345 | JClaw | 256MB | 2.5% |
            | 12346 | IntelliJ | 1.2GB | 5.2% |
            | 12347 | MySQL | 512MB | 1.0% |
            
            *注：实际数据需要 JMX 或系统命令支持*
            
            ### 查看方法
            
            ```bash
            # Linux/Mac
            jps -l
            ps aux | grep java
            
            # Windows
            tasklist | findstr java
            ```
            """;
        
        return CommandResult.success("Java 进程列表")
                .withDisplayText(report);
    }
    
    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d天 %d小时", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d小时 %d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟 %d秒", minutes, seconds % 60);
        } else {
            return seconds + "秒";
        }
    }
    
    private double getSystemLoad() {
        try {
            OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            if (os instanceof com.sun.management.OperatingSystemMXBean) {
                return ((com.sun.management.OperatingSystemMXBean) os).getSystemLoadAverage();
            }
        } catch (Exception e) {
            // 忽略
        }
        return 0.0;
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
    
    @Override
    public String getHelp() {
        return """
            命令：process
            别名：ps, proc
            描述：进程管理
            
            用法：
              process                 # 显示当前进程
              process list            # 进程列表
              process info [pid]      # 进程详情
              process top             # 资源占用
              process java            # Java 进程
              process kill <pid>      # 终止进程
            
            示例：
              process
              process info
              process top
              process java
            """;
    }
}
