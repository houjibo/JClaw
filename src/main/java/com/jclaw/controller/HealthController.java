package com.jclaw.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统健康检查 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        
        HealthStatus status = new HealthStatus();
        status.setStatus("UP");
        status.setTimestamp(LocalDateTime.now());
        status.setUptime(runtime.getUptime() / 1000); // 秒
        status.setStartTime(runtime.getStartTime());
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 系统信息
     */
    @GetMapping("/system/info")
    public ResponseEntity<SystemInfo> systemInfo() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        
        SystemInfo info = new SystemInfo();
        info.setJavaVersion(System.getProperty("java.version"));
        info.setJavaVendor(System.getProperty("java.vendor"));
        info.setOsName(System.getProperty("os.name"));
        info.setOsVersion(System.getProperty("os.version"));
        info.setOsArch(System.getProperty("os.arch"));
        info.setAvailableProcessors(Runtime.getRuntime().availableProcessors());
        info.setMaxMemory(Runtime.getRuntime().maxMemory() / 1024 / 1024); // MB
        info.setTotalMemory(Runtime.getRuntime().totalMemory() / 1024 / 1024); // MB
        info.setFreeMemory(Runtime.getRuntime().freeMemory() / 1024 / 1024); // MB
        info.setUptime(runtime.getUptime() / 1000); // 秒
        
        return ResponseEntity.ok(info);
    }
    
    @Data
    public static class HealthStatus {
        private String status;
        private LocalDateTime timestamp;
        private long uptime;
        private long startTime;
    }
    
    @Data
    public static class SystemInfo {
        private String javaVersion;
        private String javaVendor;
        private String osName;
        private String osVersion;
        private String osArch;
        private int availableProcessors;
        private long maxMemory;
        private long totalMemory;
        private long freeMemory;
        private long uptime;
    }
}
