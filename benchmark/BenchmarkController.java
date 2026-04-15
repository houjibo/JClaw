package com.jclaw.benchmark;

import com.jclaw.benchmark.BenchmarkRunner.BenchmarkResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 基准测试 REST API
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/benchmark")
@RequiredArgsConstructor
public class BenchmarkController {
    
    private final BenchmarkRunner benchmarkRunner;
    
    /**
     * 运行所有基准测试
     */
    @PostMapping("/run")
    public Map<String, BenchmarkResult> runAll() throws Exception {
        return benchmarkRunner.runAll();
    }
    
    /**
     * 获取基准测试报告
     */
    @PostMapping("/report")
    public String generateReport() throws Exception {
        Map<String, BenchmarkResult> results = benchmarkRunner.runAll();
        return benchmarkRunner.generateReport(results);
    }
    
    /**
     * 运行单项测试
     */
    @PostMapping("/run/{name}")
    public BenchmarkResult runSingle(@PathVariable String name) throws Exception {
        return switch (name) {
            case "startup" -> benchmarkRunner.benchmarkStartup();
            case "toolInvocation" -> benchmarkRunner.benchmarkToolInvocation();
            case "aiResponse" -> benchmarkRunner.benchmarkAIResponse();
            case "stringProcessing" -> benchmarkRunner.benchmarkStringProcessing();
            case "memoryAllocation" -> benchmarkRunner.benchmarkMemoryAllocation();
            case "fileOperations" -> benchmarkRunner.benchmarkFileOperations();
            case "concurrency" -> benchmarkRunner.benchmarkConcurrency();
            default -> throw new IllegalArgumentException("未知测试项：" + name);
        };
    }
}
