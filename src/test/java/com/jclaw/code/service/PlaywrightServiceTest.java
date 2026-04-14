package com.jclaw.code.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Playwright 服务测试
 */
@DisplayName("Playwright 服务测试")
class PlaywrightServiceTest {
    
    private PlaywrightService playwrightService;
    
    @BeforeEach
    void setUp() {
        playwrightService = new PlaywrightService();
    }
    
    @Test
    @DisplayName("测试检查 Playwright 安装")
    void testCheckInstallation() {
        boolean installed = playwrightService.checkInstallation();
        // 不强制要求安装，只验证方法不抛异常
        assertNotNull(installed);
    }
    
    @Test
    @DisplayName("测试运行测试 - 未安装情况")
    void testRunTestNotInstalled() {
        PlaywrightService.TestResult result = playwrightService.runTest("test.spec.ts", null);
        
        assertNotNull(result);
        // 如果未安装，应该返回错误
        if (!result.isSuccess()) {
            assertTrue(result.getOutput().contains("未安装") || result.getOutput().contains("Playwright"));
        }
    }
    
    @Test
    @DisplayName("测试错误结果创建")
    void testErrorResult() {
        PlaywrightService.TestResult result = PlaywrightService.TestResult.error("测试错误");
        
        assertFalse(result.isSuccess());
        assertEquals("测试错误", result.getOutput());
    }
    
    @Test
    @DisplayName("测试性能报告创建")
    void testPerformanceReport() {
        PlaywrightService.PerformanceReport report = new PlaywrightService.PerformanceReport();
        
        assertNotNull(report);
        report.setUrl("https://example.com");
        report.setIterations(10);
        report.setAverageLoadTime(500);
        report.setMinLoadTime(300);
        report.setMaxLoadTime(800);
        report.setSuccessRate(100L);
        
        assertEquals("https://example.com", report.getUrl());
        assertEquals(10, report.getIterations());
        assertEquals(500, report.getAverageLoadTime());
    }
    
    @Test
    @DisplayName("测试性能测试 - 本地文件")
    void testPerformanceTestLocalFile() {
        // 测试本地文件，不需要网络
        PlaywrightService.PerformanceReport report = playwrightService.runPerformanceTest(
            "file:///nonexistent", 1
        );
        
        assertNotNull(report);
        assertNotNull(report.getUrl());
    }
}
