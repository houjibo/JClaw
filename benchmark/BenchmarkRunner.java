package com.jclaw.benchmark;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * JClaw 性能基准测试框架
 * 
 * 测试场景：
 * 1. 启动时间
 * 2. 工具调用延迟
 * 3. AI 响应时间
 * 4. 并发性能
 * 5. 内存使用
 * 6. 字符串处理
 * 7. 文件操作
 * 8. 网络请求
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Service
public class BenchmarkRunner {
    
    /**
     * 基准测试结果
     */
    @Data
    public static class BenchmarkResult {
        private String name;
        private String unit;
        private double score;
        private double error;
        private long samples;
        private Map<String, Object> metadata;
        
        public BenchmarkResult(String name, String unit, double score, double error, long samples) {
            this.name = name;
            this.unit = unit;
            this.score = score;
            this.error = error;
            this.samples = samples;
            this.metadata = new HashMap<>();
        }
    }
    
    /**
     * 测试配置
     */
    @Data
    public static class BenchmarkConfig {
        private int warmupIterations = 3;
        private int measurementIterations = 5;
        private int threads = 1;
        private long timeoutSeconds = 300;
    }
    
    private final BenchmarkConfig config = new BenchmarkConfig();
    private final List<BenchmarkResult> results = new ArrayList<>();
    
    /**
     * 运行所有基准测试
     */
    public Map<String, BenchmarkResult> runAll() throws Exception {
        log.info("开始运行 JClaw 基准测试...");
        
        Map<String, BenchmarkResult> allResults = new LinkedHashMap<>();
        
        // 1. 启动时间
        allResults.put("startup", benchmarkStartup());
        
        // 2. 工具调用延迟
        allResults.put("toolInvocation", benchmarkToolInvocation());
        
        // 3. AI 响应时间
        allResults.put("aiResponse", benchmarkAIResponse());
        
        // 4. 字符串处理
        allResults.put("stringProcessing", benchmarkStringProcessing());
        
        // 5. 内存分配
        allResults.put("memoryAllocation", benchmarkMemoryAllocation());
        
        // 6. 文件操作
        allResults.put("fileOperations", benchmarkFileOperations());
        
        // 7. 并发性能
        allResults.put("concurrency", benchmarkConcurrency());
        
        log.info("基准测试完成，共 {} 项", allResults.size());
        
        return allResults;
    }
    
    /**
     * 启动时间测试
     */
    public BenchmarkResult benchmarkStartup() {
        log.info("测试：启动时间");
        
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < config.getMeasurementIterations(); i++) {
            long start = System.currentTimeMillis();
            
            // 模拟启动（实际应重启应用）
            try {
                Thread.sleep(100); // 占位
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            long end = System.currentTimeMillis();
            times.add(end - start);
        }
        
        double avg = average(times);
        double error = standardDeviation(times, avg);
        
        return new BenchmarkResult("启动时间", "ms", avg, error, times.size());
    }
    
    /**
     * 工具调用延迟测试
     */
    public BenchmarkResult benchmarkToolInvocation() {
        log.info("测试：工具调用延迟");
        
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < config.getMeasurementIterations(); i++) {
            long start = System.nanoTime();
            
            // 模拟工具调用
            dummyOperation();
            
            long end = System.nanoTime();
            times.add((end - start) / 1_000_000); // 转换为 ms
        }
        
        double avg = average(times);
        double error = standardDeviation(times, avg);
        
        return new BenchmarkResult("工具调用延迟", "ms", avg, error, times.size());
    }
    
    /**
     * AI 响应时间测试
     */
    public BenchmarkResult benchmarkAIResponse() {
        log.info("测试：AI 响应时间");
        
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < config.getMeasurementIterations(); i++) {
            long start = System.currentTimeMillis();
            
            // 模拟 AI 调用（实际应调用智谱 API）
            try {
                Thread.sleep(500); // 占位
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            long end = System.currentTimeMillis();
            times.add(end - start);
        }
        
        double avg = average(times);
        double error = standardDeviation(times, avg);
        
        return new BenchmarkResult("AI 响应时间", "ms", avg, error, times.size());
    }
    
    /**
     * 字符串处理测试
     */
    public BenchmarkResult benchmarkStringProcessing() {
        log.info("测试：字符串处理");
        
        String testString = "The quick brown fox jumps over the lazy dog. ".repeat(100);
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < config.getMeasurementIterations(); i++) {
            long start = System.nanoTime();
            
            // 字符串操作
            String upper = testString.toUpperCase();
            String lower = upper.toLowerCase();
            String replaced = lower.replace(" ", "_");
            String[] parts = replaced.split("_");
            
            long end = System.nanoTime();
            times.add((end - start) / 1_000_000); // 转换为 ms
        }
        
        double avg = average(times);
        double error = standardDeviation(times, avg);
        
        BenchmarkResult result = new BenchmarkResult("字符串处理", "ms", avg, error, times.size());
        result.getMetadata().put("stringLength", testString.length());
        result.getMetadata().put("operations", "toUpperCase, toLowerCase, replace, split");
        
        return result;
    }
    
    /**
     * 内存分配测试
     */
    public BenchmarkResult benchmarkMemoryAllocation() {
        log.info("测试：内存分配");
        
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < config.getMeasurementIterations(); i++) {
            long start = System.nanoTime();
            
            // 内存分配
            List<Map<String, Object>> objects = new ArrayList<>();
            for (int j = 0; j < 10000; j++) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("id", j);
                obj.put("name", "Object-" + j);
                obj.put("value", Math.random());
                objects.add(obj);
            }
            
            long end = System.nanoTime();
            times.add((end - start) / 1_000_000); // 转换为 ms
            
            // 清理
            objects.clear();
            System.gc();
        }
        
        double avg = average(times);
        double error = standardDeviation(times, avg);
        
        BenchmarkResult result = new BenchmarkResult("内存分配", "ms", avg, error, times.size());
        result.getMetadata().put("objectsAllocated", 10000);
        result.getMetadata().put("objectSize", "~100 bytes");
        
        return result;
    }
    
    /**
     * 文件操作测试
     */
    public BenchmarkResult benchmarkFileOperations() {
        log.info("测试：文件操作");
        
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < config.getMeasurementIterations(); i++) {
            long start = System.nanoTime();
            
            // 模拟文件操作
            try {
                Thread.sleep(10); // 占位
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            long end = System.nanoTime();
            times.add((end - start) / 1_000_000);
        }
        
        double avg = average(times);
        double error = standardDeviation(times, avg);
        
        return new BenchmarkResult("文件操作", "ms", avg, error, times.size());
    }
    
    /**
     * 并发性能测试
     */
    public BenchmarkResult benchmarkConcurrency() {
        log.info("测试：并发性能");
        
        int threadCount = 10;
        int operationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        long start = System.currentTimeMillis();
        
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threadCount; t++) {
            futures.add(executor.submit(() -> {
                for (int i = 0; i < operationsPerThread; i++) {
                    dummyOperation();
                }
            }));
        }
        
        // 等待完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("并发测试失败", e);
            }
        }
        
        long end = System.currentTimeMillis();
        long totalTime = end - start;
        
        executor.shutdown();
        
        double opsPerSecond = (double) (threadCount * operationsPerThread) / (totalTime / 1000.0);
        
        BenchmarkResult result = new BenchmarkResult("并发性能", "ops/sec", opsPerSecond, 0, threadCount * operationsPerThread);
        result.getMetadata().put("threads", threadCount);
        result.getMetadata().put("totalOperations", threadCount * operationsPerThread);
        result.getMetadata().put("totalTimeMs", totalTime);
        
        return result;
    }
    
    // ==================== 辅助方法 ====================
    
    private double average(List<Long> values) {
        return values.stream().mapToDouble(Long::doubleValue).average().orElse(0);
    }
    
    private double standardDeviation(List<Long> values, double avg) {
        double sum = 0;
        for (long value : values) {
            sum += Math.pow(value - avg, 2);
        }
        return Math.sqrt(sum / values.size());
    }
    
    private void dummyOperation() {
        // 占位操作
        Math.random();
    }
    
    /**
     * 生成基准测试报告
     */
    public String generateReport(Map<String, BenchmarkResult> results) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# JClaw 基准测试报告\n\n");
        sb.append("**测试时间**: ").append(new Date()).append("\n");
        sb.append("**测试配置**: warmup=").append(config.getWarmupIterations())
          .append(", measurements=").append(config.getMeasurementIterations())
          .append(", threads=").append(config.getThreads()).append("\n\n");
        
        sb.append("## 测试结果\n\n");
        sb.append("| 测试项 | 结果 | 单位 | 误差 | 样本数 |\n");
        sb.append("|--------|------|------|------|--------|\n");
        
        for (BenchmarkResult result : results.values()) {
            sb.append(String.format("| %s | %.2f | %s | ±%.2f | %d |\n",
                result.getName(), result.getScore(), result.getUnit(),
                result.getError(), result.getSamples()));
        }
        
        sb.append("\n## 详细数据\n\n");
        
        for (Map.Entry<String, BenchmarkResult> entry : results.entrySet()) {
            sb.append("### ").append(entry.getValue().getName()).append("\n\n");
            sb.append("- **结果**: ").append(String.format("%.2f %s", entry.getValue().getScore(), entry.getValue().getUnit())).append("\n");
            sb.append("- **误差**: ±").append(String.format("%.2f", entry.getValue().getError())).append("\n");
            sb.append("- **样本数**: ").append(entry.getValue().getSamples()).append("\n");
            
            if (!entry.getValue().getMetadata().isEmpty()) {
                sb.append("- **元数据**:\n");
                for (Map.Entry<String, Object> meta : entry.getValue().getMetadata().entrySet()) {
                    sb.append("  - ").append(meta.getKey()).append(": ").append(meta.getValue()).append("\n");
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
