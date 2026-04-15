package com.jclaw.benchmark;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基准测试集成测试
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@SpringBootTest
public class BenchmarkRunnerTest {
    
    @Autowired
    private BenchmarkRunner benchmarkRunner;
    
    @Test
    void testBenchmarkStartup() {
        BenchmarkRunner.BenchmarkResult result = benchmarkRunner.benchmarkStartup();
        
        assertNotNull(result);
        assertEquals("启动时间", result.getName());
        assertEquals("ms", result.getUnit());
        assertTrue(result.getScore() > 0);
        System.out.println("启动时间：" + result.getScore() + " ± " + result.getError() + " " + result.getUnit());
    }
    
    @Test
    void testBenchmarkToolInvocation() {
        BenchmarkRunner.BenchmarkResult result = benchmarkRunner.benchmarkToolInvocation();
        
        assertNotNull(result);
        assertEquals("工具调用延迟", result.getName());
        assertTrue(result.getScore() > 0);
        System.out.println("工具调用延迟：" + result.getScore() + " ± " + result.getError() + " " + result.getUnit());
    }
    
    @Test
    void testBenchmarkStringProcessing() {
        BenchmarkRunner.BenchmarkResult result = benchmarkRunner.benchmarkStringProcessing();
        
        assertNotNull(result);
        assertEquals("字符串处理", result.getName());
        assertTrue(result.getScore() > 0);
        System.out.println("字符串处理：" + result.getScore() + " ± " + result.getError() + " " + result.getUnit());
    }
    
    @Test
    void testBenchmarkMemoryAllocation() {
        BenchmarkRunner.BenchmarkResult result = benchmarkRunner.benchmarkMemoryAllocation();
        
        assertNotNull(result);
        assertEquals("内存分配", result.getName());
        assertTrue(result.getScore() > 0);
        System.out.println("内存分配：" + result.getScore() + " ± " + result.getError() + " " + result.getUnit());
    }
    
    @Test
    void testBenchmarkConcurrency() {
        BenchmarkRunner.BenchmarkResult result = benchmarkRunner.benchmarkConcurrency();
        
        assertNotNull(result);
        assertEquals("并发性能", result.getName());
        assertEquals("ops/sec", result.getUnit());
        assertTrue(result.getScore() > 0);
        System.out.println("并发性能：" + result.getScore() + " " + result.getUnit());
    }
    
    @Test
    void testGenerateReport() throws Exception {
        var results = benchmarkRunner.runAll();
        String report = benchmarkRunner.generateReport(results);
        
        assertNotNull(report);
        assertTrue(report.contains("# JClaw 基准测试报告"));
        assertTrue(report.contains("启动时间"));
        assertTrue(report.contains("工具调用延迟"));
        assertTrue(report.contains("AI 响应时间"));
        
        System.out.println("\n=== 基准测试报告 ===\n");
        System.out.println(report);
    }
}
