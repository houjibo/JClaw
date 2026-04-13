package com.jclaw.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控服务
 */
@Slf4j
@Service
public class MetricsService {
    
    private final MemoryMXBean memoryMXBean;
    private final ThreadMXBean threadMXBean;
    
    // 指标存储
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, Long> gauges = new ConcurrentHashMap<>();
    
    public MetricsService() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }
    
    /**
     * 增加计数器
     */
    public void increment(String name) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * 增加计数器（指定数量）
     */
    public void increment(String name, long amount) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).addAndGet(amount);
    }
    
    /**
     * 获取计数器值
     */
    public long getCount(String name) {
        return counters.getOrDefault(name, new AtomicLong(0)).get();
    }
    
    /**
     * 设置指标值
     */
    public void setGauge(String name, long value) {
        gauges.put(name, value);
    }
    
    /**
     * 获取所有指标
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> metrics = new java.util.HashMap<>();
        
        // JVM 内存
        var heapUsage = memoryMXBean.getHeapMemoryUsage();
        metrics.put("memory", Map.of(
            "heapUsed", heapUsage.getUsed() / 1024 / 1024, // MB
            "heapCommitted", heapUsage.getCommitted() / 1024 / 1024,
            "heapMax", heapUsage.getMax() / 1024 / 1024,
            "nonHeapUsed", memoryMXBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024
        ));
        
        // 线程
        metrics.put("threads", Map.of(
            "count", threadMXBean.getThreadCount(),
            "peak", threadMXBean.getPeakThreadCount(),
            "daemon", threadMXBean.getDaemonThreadCount()
        ));
        
        // 自定义计数器
        Map<String, Long> counterValues = new java.util.HashMap<>();
        counters.forEach((k, v) -> counterValues.put(k, v.get()));
        metrics.put("counters", counterValues);
        
        // 自定义指标
        metrics.put("gauges", gauges);
        
        return metrics;
    }
    
    /**
     * 记录技能调用
     */
    public void recordSkillCall(String skillName, long durationMs, boolean success) {
        increment("skill.calls.total");
        increment("skill.calls." + skillName);
        
        if (success) {
            increment("skill.calls.success");
        } else {
            increment("skill.calls.failure");
        }
        
        setGauge("skill.last.duration." + skillName, durationMs);
    }
    
    /**
     * 记录 AI 调用
     */
    public void recordAiCall(long durationMs, boolean success) {
        increment("ai.calls.total");
        
        if (success) {
            increment("ai.calls.success");
        } else {
            increment("ai.calls.failure");
        }
        
        setGauge("ai.last.duration", durationMs);
    }
    
    /**
     * 记录消息发送
     */
    public void recordMessageSent(String channel) {
        increment("messages.sent.total");
        increment("messages.sent." + channel);
    }
}
