package com.jclaw.controller;

import com.jclaw.monitoring.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 监控指标 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {
    
    private final MetricsService metricsService;
    
    /**
     * 获取所有指标
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(metricsService.getAllMetrics());
    }
    
    /**
     * Prometheus 格式指标
     */
    @GetMapping(value = "/prometheus", produces = "text/plain")
    public ResponseEntity<String> getPrometheusMetrics() {
        Map<String, Object> metrics = metricsService.getAllMetrics();
        
        StringBuilder sb = new StringBuilder();
        sb.append("# HELP jclaw_memory_heap_used JVM heap memory used (MB)\n");
        sb.append("# TYPE jclaw_memory_heap_used gauge\n");
        Map<String, Object> memory = (Map<String, Object>) metrics.get("memory");
        if (memory != null) {
            sb.append("jclaw_memory_heap_used ").append(memory.get("heapUsed")).append("\n");
        }
        
        sb.append("# HELP jclaw_threads_count Thread count\n");
        sb.append("# TYPE jclaw_threads_count gauge\n");
        Map<String, Object> threads = (Map<String, Object>) metrics.get("threads");
        if (threads != null) {
            sb.append("jclaw_threads_count ").append(threads.get("count")).append("\n");
        }
        
        sb.append("# HELP jclaw_skill_calls_total Total skill calls\n");
        sb.append("# TYPE jclaw_skill_calls_total counter\n");
        Map<String, Long> counters = (Map<String, Long>) metrics.get("counters");
        if (counters != null) {
            counters.forEach((k, v) -> {
                if (k.startsWith("skill.calls")) {
                    String name = k.replace(".", "_");
                    sb.append("jclaw_").append(name).append(" ").append(v).append("\n");
                }
            });
        }
        
        return ResponseEntity.ok(sb.toString());
    }
}
