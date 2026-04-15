package com.jclaw.monitoring.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 监控指标 REST API
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {
    
    private final MeterRegistry meterRegistry;
    
    // 计数器
    private final Counter toolInvocationCounter;
    private final Counter aiRequestCounter;
    private final Counter errorCounter;
    
    // 计时器
    private final Timer apiLatencyTimer;
    private final Timer aiResponseTimer;
    
    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 初始化指标
        this.toolInvocationCounter = Counter.builder("jclaw.tool.invocations")
            .description("工具调用次数")
            .register(meterRegistry);
        
        this.aiRequestCounter = Counter.builder("jclaw.ai.requests")
            .description("AI 请求次数")
            .register(meterRegistry);
        
        this.errorCounter = Counter.builder("jclaw.errors")
            .description("错误次数")
            .register(meterRegistry);
        
        this.apiLatencyTimer = Timer.builder("jclaw.api.latency")
            .description("API 延迟")
            .register(meterRegistry);
        
        this.aiResponseTimer = Timer.builder("jclaw.ai.response")
            .description("AI 响应时间")
            .register(meterRegistry);
    }
    
    /**
     * 记录工具调用
     */
    public void recordToolInvocation(String toolName) {
        toolInvocationCounter.increment();
        meterRegistry.counter("jclaw.tool.invocations", "tool", toolName).increment();
    }
    
    /**
     * 记录 AI 请求
     */
    public void recordAiRequest(String model) {
        aiRequestCounter.increment();
        meterRegistry.counter("jclaw.ai.requests", "model", model).increment();
    }
    
    /**
     * 记录 AI 响应时间
     */
    public void recordAiResponse(long durationMs) {
        aiResponseTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 记录 API 延迟
     */
    public void recordApiLatency(String endpoint, long durationMs) {
        apiLatencyTimer.record(durationMs, TimeUnit.MILLISECONDS);
        meterRegistry.timer("jclaw.api.latency", "endpoint", endpoint).record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 记录错误
     */
    public void recordError(String type) {
        errorCounter.increment();
        meterRegistry.counter("jclaw.errors", "type", type).increment();
    }
    
    /**
     * 获取指标摘要
     */
    @GetMapping("/summary")
    public Map<String, Object> getMetricsSummary() {
        return Map.of(
            "toolInvocations", toolInvocationCounter.count(),
            "aiRequests", aiRequestCounter.count(),
            "errors", errorCounter.count(),
            "apiLatencyAvg", apiLatencyTimer.mean(TimeUnit.MILLISECONDS),
            "aiResponseAvg", aiResponseTimer.mean(TimeUnit.MILLISECONDS)
        );
    }
}
