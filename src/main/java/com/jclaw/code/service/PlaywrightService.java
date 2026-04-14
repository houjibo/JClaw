package com.jclaw.code.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Playwright 浏览器自动化服务
 * 
 * 功能：
 * - 浏览器控制
 * - 页面截图
 * - 元素交互
 * - 性能测试
 * 
 * 状态：基础框架实现，需要安装 Playwright
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Service
public class PlaywrightService {
    
    private boolean playwrightInstalled = false;
    
    /**
     * 检查 Playwright 是否安装
     */
    public boolean checkInstallation() {
        try {
            ProcessBuilder pb = new ProcessBuilder("npx", "playwright", "--version");
            Process process = pb.start();
            boolean completed = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
            playwrightInstalled = completed && (process.exitValue() == 0);
            log.info("Playwright 安装状态：{}", playwrightInstalled ? "已安装" : "未安装");
            return playwrightInstalled;
        } catch (Exception e) {
            log.warn("Playwright 未安装或不可用", e);
            playwrightInstalled = false;
            return false;
        }
    }
    
    /**
     * 安装 Playwright
     */
    public boolean installPlaywright() {
        log.info("正在安装 Playwright...");
        try {
            ProcessBuilder pb = new ProcessBuilder("npm", "install", "-D", "@playwright/test");
            pb.inheritIO();
            Process process = pb.start();
            boolean completed = process.waitFor(5, TimeUnit.MINUTES);
            int exitCode = completed ? process.exitValue() : -1;
            
            if (exitCode == 0) {
                log.info("Playwright 安装成功");
                playwrightInstalled = true;
                return true;
            } else {
                log.error("Playwright 安装失败，退出码：{}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("Playwright 安装异常", e);
            return false;
        }
    }
    
    /**
     * 运行 Playwright 测试
     */
    public TestResult runTest(String testFile, Map<String, String> options) {
        log.info("运行 Playwright 测试：{}", testFile);
        
        if (!playwrightInstalled) {
            if (!checkInstallation()) {
                return TestResult.error("Playwright 未安装，请先运行 installPlaywright()");
            }
        }
        
        try {
            List<String> command = new ArrayList<>();
            command.add("npx");
            command.add("playwright");
            command.add("test");
            command.add(testFile);
            
            if (options != null) {
                if (options.containsKey("browser")) {
                    command.add("--project=" + options.get("browser"));
                }
                if (options.containsKey("headed")) {
                    command.add("--headed");
                }
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.info(line);
                }
            }
            
            boolean completed = process.waitFor(5, TimeUnit.MINUTES);
            int exitCode = completed ? process.exitValue() : -1;
            
            TestResult result = new TestResult();
            result.setSuccess(exitCode == 0);
            result.setOutput(output.toString());
            result.setExitCode(exitCode);
            
            return result;
            
        } catch (Exception e) {
            log.error("Playwright 测试运行失败", e);
            return TestResult.error("测试运行异常：" + e.getMessage());
        }
    }
    
    /**
     * 页面截图
     */
    public String captureScreenshot(String url, String outputPath) {
        log.info("页面截图：{} -> {}", url, outputPath);
        
        String testCode = String.format("""
            import { test, expect } from '@playwright/test';
            
            test('capture screenshot', async ({ page }) => {
                await page.goto('%s');
                await page.screenshot({ path: '%s', fullPage: true });
            });
            """, url, outputPath);
        
        try {
            java.nio.file.Path tempTest = java.nio.file.Files.createTempFile("screenshot-test", ".spec.ts");
            java.nio.file.Files.writeString(tempTest, testCode);
            
            Map<String, String> options = new HashMap<>();
            options.put("browser", "chromium");
            
            TestResult result = runTest(tempTest.toString(), options);
            
            java.nio.file.Files.deleteIfExists(tempTest);
            
            if (result.isSuccess()) {
                return outputPath;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("截图失败", e);
            return null;
        }
    }
    
    /**
     * 性能测试
     */
    public PerformanceReport runPerformanceTest(String url, int iterations) {
        log.info("性能测试：{} ({} 次迭代)", url, iterations);
        
        PerformanceReport report = new PerformanceReport();
        report.setUrl(url);
        report.setIterations(iterations);
        
        List<Long> loadTimes = new ArrayList<>();
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                conn.getResponseCode();
                conn.disconnect();
                
                long loadTime = System.currentTimeMillis() - startTime;
                loadTimes.add(loadTime);
                
            } catch (Exception e) {
                log.warn("第 {} 次请求失败：{}", i + 1, e.getMessage());
            }
        }
        
        if (!loadTimes.isEmpty()) {
            double avgDouble = loadTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long avg = (long) avgDouble;
            long min = loadTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
            long max = loadTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
            
            report.setAverageLoadTime(avg);
            report.setMinLoadTime(min);
            report.setMaxLoadTime(max);
            report.setSuccessRate((long) ((double) loadTimes.size() / iterations * 100));
        }
        
        return report;
    }
    
    /**
     * 测试结果
     */
    public static class TestResult {
        private boolean success;
        private String output;
        private int exitCode;
        
        public static TestResult error(String message) {
            TestResult result = new TestResult();
            result.success = false;
            result.output = message;
            return result;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }
        public int getExitCode() { return exitCode; }
        public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    }
    
    /**
     * 性能报告
     */
    public static class PerformanceReport {
        private String url;
        private int iterations;
        private long averageLoadTime;
        private long minLoadTime;
        private long maxLoadTime;
        private long successRate;
        
        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public int getIterations() { return iterations; }
        public void setIterations(int iterations) { this.iterations = iterations; }
        public long getAverageLoadTime() { return averageLoadTime; }
        public void setAverageLoadTime(long averageLoadTime) { this.averageLoadTime = averageLoadTime; }
        public long getMinLoadTime() { return minLoadTime; }
        public void setMinLoadTime(long minLoadTime) { this.minLoadTime = minLoadTime; }
        public long getMaxLoadTime() { return maxLoadTime; }
        public void setMaxLoadTime(long maxLoadTime) { this.maxLoadTime = maxLoadTime; }
        public long getSuccessRate() { return successRate; }
        public void setSuccessRate(long successRate) { this.successRate = successRate; }
    }
}
