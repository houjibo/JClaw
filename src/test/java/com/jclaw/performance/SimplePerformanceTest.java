package com.jclaw.performance;

import com.jclaw.intent.service.TaskDecompositionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简化的性能基准测试
 * 测试核心服务的响应时间
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("🏎️ 性能基准测试")
class PerformanceBenchmarkTest {

    @Autowired
    private TaskDecompositionService taskDecompositionService;

    @Test
    @Order(1)
    @DisplayName("任务分解 - 响应时间")
    void testTaskDecompositionPerformance() {
        Instant start = Instant.now();
        
        // 执行 100 次任务分解
        for (int i = 0; i < 100; i++) {
            taskDecompositionService.decompose(
                com.jclaw.intent.entity.Intent.builder()
                    .id("perf_test_" + i)
                    .name("性能测试意图")
                    .description("测试任务分解性能")
                    .build()
            );
        }
        
        Duration elapsed = Duration.between(start, Instant.now());
        long avgMs = elapsed.toMillis() / 100;
        
        System.out.printf("✅ 任务分解服务：100 次执行，平均响应时间=%dms%n", avgMs);
        
        // 性能要求：平均响应时间 < 50ms
        assertTrue(avgMs < 50, "任务分解响应时间应 < 50ms，实际：" + avgMs + "ms");
    }

    @Test
    @Order(2)
    @DisplayName("复杂度评估 - 批量性能")
    void testComplexityEstimationPerformance() {
        Map<String, Object> testTask = Map.of(
            "description", "这是一个复杂的任务需要实现多个功能模块",
            "type", "coding"
        );
        
        Instant start = Instant.now();
        
        // 执行 1000 次复杂度评估
        for (int i = 0; i < 1000; i++) {
            taskDecompositionService.estimateComplexity(testTask);
        }
        
        Duration elapsed = Duration.between(start, Instant.now());
        long elapsedMs = elapsed.toMillis();
        
        // 避免除零错误
        if (elapsedMs == 0) {
            elapsedMs = 1;  // 至少 1ms
        }
        
        long opsPerSecond = 1000 * 1000 / elapsedMs;
        
        System.out.printf("✅ 复杂度评估：1000 次执行，耗时=%dms, 吞吐量=%d ops/sec%n", elapsed.toMillis(), opsPerSecond);
        
        // 性能要求：吞吐量 > 10000 ops/sec
        assertTrue(opsPerSecond > 10000, "复杂度评估吞吐量应 > 10000 ops/sec，实际：" + opsPerSecond);
    }
}
