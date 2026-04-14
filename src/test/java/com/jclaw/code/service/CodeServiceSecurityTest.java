package com.jclaw.code.service;

import com.jclaw.code.dto.SecurityReport;
import com.jclaw.code.dto.Vulnerability;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 安全扫描服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("安全扫描服务测试")
class CodeServiceSecurityTest {
    
    @Autowired
    private CodeService codeService;
    
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("security-test", ".java");
    }
    
    @Test
    @DisplayName("测试安全扫描 - 安全代码")
    void testSecurityScanSafeCode() throws IOException {
        String code = """
            public class SafeCode {
                public String greet(String name) {
                    return "Hello, " + name;
                }
            }
            """;
        Files.writeString(tempFile, code);
        
        SecurityReport report = codeService.securityScan(tempFile.toString());
        
        assertNotNull(report);
        assertEquals(tempFile.toString(), report.getFilePath());
        assertTrue(report.getRiskScore() >= 0);
        assertNotNull(report.getRecommendations());
    }
    
    @Test
    @DisplayName("测试安全扫描 - System.exit")
    void testSecurityScanSystemExit() throws IOException {
        String code = """
            public class UnsafeCode {
                public void exit() {
                    System.exit(0);
                }
            }
            """;
        Files.writeString(tempFile, code);
        
        SecurityReport report = codeService.securityScan(tempFile.toString());
        
        assertNotNull(report);
        assertTrue(report.getRiskScore() > 0);
        
        List<Vulnerability> vulns = report.getVulnerabilities();
        boolean foundSystemExit = vulns.stream()
            .anyMatch(v -> v.getDescription().contains("System.exit"));
        assertTrue(foundSystemExit, "应该检测到 System.exit 风险");
    }
    
    @Test
    @DisplayName("测试安全扫描 - Runtime.exec")
    void testSecurityScanRuntimeExec() throws IOException {
        String code = """
            public class UnsafeCode {
                public void execute(String cmd) throws Exception {
                    Runtime.getRuntime().exec(cmd);
                }
            }
            """;
        Files.writeString(tempFile, code);
        
        SecurityReport report = codeService.securityScan(tempFile.toString());
        
        assertNotNull(report);
        assertTrue(report.getRiskScore() > 0);
        
        List<Vulnerability> vulns = report.getVulnerabilities();
        boolean foundExec = vulns.stream()
            .anyMatch(v -> v.getDescription().contains("命令注入") || v.getType().equals("Command Injection"));
        assertTrue(foundExec, "应该检测到命令注入风险");
    }
    
    @Test
    @DisplayName("测试安全扫描 - 多个漏洞")
    void testSecurityScanMultipleVulnerabilities() throws IOException {
        String code = """
            public class VeryUnsafeCode {
                public void dangerous(String cmd) throws Exception {
                    System.out.println("Starting...");
                    Runtime.getRuntime().exec(cmd);
                    System.exit(0);
                }
            }
            """;
        Files.writeString(tempFile, code);
        
        SecurityReport report = codeService.securityScan(tempFile.toString());
        
        assertNotNull(report);
        assertTrue(report.getRiskScore() > 0);
        assertTrue(report.getVulnerabilities().size() >= 1);
        
        System.out.println("=== 安全扫描报告 ===");
        System.out.println("风险评分：" + report.getRiskScore());
        System.out.println("漏洞数量：" + report.getVulnerabilities().size());
        report.getVulnerabilities().forEach(v -> 
            System.out.println("[" + v.getSeverity() + "] " + v.getDescription())
        );
        System.out.println("建议：" + report.getRecommendations());
    }
    
    @Test
    @DisplayName("测试安全扫描 - 不存在的文件")
    void testSecurityScanNonExistent() {
        assertThrows(RuntimeException.class, () -> {
            codeService.securityScan("/non/existent/file.java");
        });
    }
    
    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException e) {
            // 忽略
        }
    }
}
