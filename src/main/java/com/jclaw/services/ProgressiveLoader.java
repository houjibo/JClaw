package com.jclaw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * 渐进式加载服务
 * 
 * 功能：
 * - 并行启动优化
 * - 特性开关系统
 * - 按需加载
 * - 启动时间优化
 */
@Service
public class ProgressiveLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(ProgressiveLoader.class);
    
    /**
     * 启动阶段
     */
    public enum StartupPhase {
        CORE,       // 核心组件
        SERVICES,   // 服务层
        TOOLS,      // 工具层
        READY       // 就绪
    }
    
    /**
     * 特性开关
     */
    public static class FeatureFlag {
        public String name;
        public boolean enabled;
        public String description;
        public StartupPhase phase;
        
        public FeatureFlag(String name, boolean enabled, String description, StartupPhase phase) {
            this.name = name;
            this.enabled = enabled;
            this.description = description;
            this.phase = phase;
        }
    }
    
    /**
     * 启动指标
     */
    public static class StartupMetrics {
        public long startTime;
        public long corePhaseTime;
        public long servicesPhaseTime;
        public long toolsPhaseTime;
        public long totalTime;
        public Map<String, Long> componentTimes = new HashMap<>();
    }
    
    /**
     * 特性开关存储
     */
    private final ConcurrentHashMap<String, FeatureFlag> featureFlags = new ConcurrentHashMap<>();
    
    /**
     * 启动指标
     */
    private final StartupMetrics metrics = new StartupMetrics();
    
    /**
     * 当前启动阶段
     */
    private StartupPhase currentPhase = StartupPhase.CORE;
    
    /**
     * 异步执行器
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    public ProgressiveLoader() {
        metrics.startTime = System.currentTimeMillis();
        
        // 初始化特性开关
        initFeatureFlags();
    }
    
    /**
     * 初始化特性开关
     */
    private void initFeatureFlags() {
        featureFlags.put("sse.enabled", new FeatureFlag("sse.enabled", true, "SSE 流式输出", StartupPhase.SERVICES));
        featureFlags.put("mcp.enabled", new FeatureFlag("mcp.enabled", true, "MCP 协议支持", StartupPhase.SERVICES));
        featureFlags.put("agent.enabled", new FeatureFlag("agent.enabled", true, "多 Agent 协调", StartupPhase.SERVICES));
        featureFlags.put("terminal.ui", new FeatureFlag("terminal.ui", true, "终端 UI", StartupPhase.TOOLS));
        featureFlags.put("notebook.edit", new FeatureFlag("notebook.edit", true, "Notebook 编辑", StartupPhase.TOOLS));
        featureFlags.put("permission.tracking", new FeatureFlag("permission.tracking", true, "权限追踪", StartupPhase.SERVICES));
        featureFlags.put("hotreload.config", new FeatureFlag("hotreload.config", true, "配置热重载", StartupPhase.SERVICES));
    }
    
    /**
     * 并行启动核心组件
     */
    public CompletableFuture<Void> parallelStartup(List<Runnable> tasks, String phaseName) {
        logger.info("并行启动阶段：{}", phaseName);
        long phaseStart = System.currentTimeMillis();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Runnable task : tasks) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(task, executor);
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .whenComplete((v, throwable) -> {
                long phaseTime = System.currentTimeMillis() - phaseStart;
                metrics.componentTimes.put(phaseName, phaseTime);
                logger.info("阶段 {} 完成，耗时：{}ms", phaseName, phaseTime);
            });
    }
    
    /**
     * 检查特性是否启用
     */
    public boolean isFeatureEnabled(String featureName) {
        FeatureFlag flag = featureFlags.get(featureName);
        return flag != null && flag.enabled;
    }
    
    /**
     * 启用/禁用特性
     */
    public void setFeatureEnabled(String featureName, boolean enabled) {
        FeatureFlag flag = featureFlags.get(featureName);
        if (flag != null) {
            flag.enabled = enabled;
            logger.info("特性 {} 已{}", featureName, enabled ? "启用" : "禁用");
        }
    }
    
    /**
     * 列出所有特性
     */
    public List<Map<String, Object>> listFeatures() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FeatureFlag flag : featureFlags.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("name", flag.name);
            info.put("enabled", flag.enabled);
            info.put("description", flag.description);
            info.put("phase", flag.phase.name());
            list.add(info);
        }
        return list;
    }
    
    /**
     * 获取启动指标
     */
    public Map<String, Object> getStartupMetrics() {
        Map<String, Object> metricsMap = new HashMap<>();
        metricsMap.put("startTime", metrics.startTime);
        metricsMap.put("corePhaseTime", metrics.corePhaseTime);
        metricsMap.put("servicesPhaseTime", metrics.servicesPhaseTime);
        metricsMap.put("toolsPhaseTime", metrics.toolsPhaseTime);
        metricsMap.put("totalTime", metrics.totalTime);
        metricsMap.put("componentTimes", metrics.componentTimes);
        return metricsMap;
    }
    
    /**
     * 标记阶段完成
     */
    public void markPhaseComplete(StartupPhase phase) {
        long now = System.currentTimeMillis();
        long elapsed = now - metrics.startTime;
        
        switch (phase) {
            case CORE:
                metrics.corePhaseTime = elapsed;
                break;
            case SERVICES:
                metrics.servicesPhaseTime = elapsed - metrics.corePhaseTime;
                break;
            case TOOLS:
                metrics.toolsPhaseTime = elapsed - metrics.corePhaseTime - metrics.servicesPhaseTime;
                break;
            case READY:
                metrics.totalTime = elapsed;
                logger.info("启动完成，总耗时：{}ms", elapsed);
                break;
        }
        
        currentPhase = phase;
    }
    
    /**
     * 获取当前启动阶段
     */
    public StartupPhase getCurrentPhase() {
        return currentPhase;
    }
    
    /**
     * 优化启动（预加载）
     */
    public void preloadComponents(List<String> components) {
        logger.info("预加载组件：{}", components);
        
        for (String component : components) {
            // 模拟预加载
            CompletableFuture.runAsync(() -> {
                logger.debug("预加载：{}", component);
                // 实际应该加载组件
            }, executor);
        }
    }
    
    /**
     * 关闭服务
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
