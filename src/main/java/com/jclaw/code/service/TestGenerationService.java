package com.jclaw.code.service;

import com.jclaw.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试生成服务
 * 
 * 功能：
 * - 基于源代码生成单元测试
 * - 基于变更推荐测试
 * - 测试覆盖率分析
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestGenerationService {
    
    @Autowired
    private AiService aiService;
    
    /**
     * 为指定文件生成单元测试
     */
    public String generateTests(String sourceFilePath, String testFramework) {
        log.info("生成单元测试：{} - {}", sourceFilePath, testFramework);
        
        try {
            String sourceCode = Files.readString(Paths.get(sourceFilePath));
            String language = detectLanguage(sourceFilePath);
            
            // 使用智谱 AI 生成测试
            String prompt = String.format(
                "请为以下 %s 代码生成 %s 单元测试，只返回测试代码：\\n\\n" +
                "代码：\\n%s",
                language, testFramework, sourceCode.substring(0, Math.min(5000, sourceCode.length()))
            );
            
            String aiResponse = aiService.chat(prompt);
            
            if (aiResponse != null && !aiResponse.isEmpty() && !aiResponse.contains("待实现")) {
                log.info("AI 生成测试成功");
                return aiResponse;
            }
            
            // Fallback：生成基础测试框架
            return generateBasicTest(sourceFilePath, testFramework);
            
        } catch (IOException e) {
            log.error("读取源文件失败：{}", sourceFilePath, e);
            return "测试生成失败：" + e.getMessage();
        }
    }
    
    /**
     * 生成基础测试框架（Fallback）
     */
    private String generateBasicTest(String sourceFilePath, String testFramework) {
        String className = extractClassName(sourceFilePath);
        
        if ("junit".equals(testFramework.toLowerCase())) {
            return String.format(
                """
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;
                
                public class %sTest {
                    
                    @Test
                    void testConstructor() {
                        // TODO: 实现测试
                        %s instance = new %s();
                        assertNotNull(instance);
                    }
                    
                    @Test
                    void testBusinessLogic() {
                        // TODO: 实现业务逻辑测试
                    }
                }
                """,
                className, className, className
            );
        }
        
        return "// 不支持的测试框架：" + testFramework;
    }
    
    /**
     * 保存测试文件
     */
    public String saveTestFile(String testCode, String sourceFilePath) {
        try {
            String testPath = sourceFilePath.replace("/src/main/", "/src/test/")
                .replace(".java", "Test.java");
            
            Path path = Paths.get(testPath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, testCode);
            
            log.info("测试文件保存成功：{}", testPath);
            return testPath;
            
        } catch (IOException e) {
            log.error("保存测试文件失败", e);
            return null;
        }
    }
    
    /**
     * 检测编程语言
     */
    private String detectLanguage(String filePath) {
        if (filePath.endsWith(".java")) return "Java";
        if (filePath.endsWith(".ts")) return "TypeScript";
        if (filePath.endsWith(".js")) return "JavaScript";
        if (filePath.endsWith(".py")) return "Python";
        return "Unknown";
    }
    
    /**
     * 提取类名
     */
    private String extractClassName(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        return fileName.replace(".java", "");
    }
    
    /**
     * 分析测试覆盖率
     */
    public TestCoverageReport analyzeCoverage(String projectPath) {
        log.info("分析测试覆盖率：{}", projectPath);
        
        // TODO: 实际实现覆盖率分析
        TestCoverageReport report = new TestCoverageReport();
        report.setProjectPath(projectPath);
        report.setOverallCoverage(0.0);
        report.setCoveredLines(0);
        report.setTotalLines(0);
        
        return report;
    }
    
    /**
     * 测试覆盖率报告
     */
    public static class TestCoverageReport {
        private String projectPath;
        private double overallCoverage;
        private int coveredLines;
        private int totalLines;
        private List<FileCoverage> fileCoverages = new ArrayList<>();
        
        // Getters and Setters
        public String getProjectPath() { return projectPath; }
        public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
        public double getOverallCoverage() { return overallCoverage; }
        public void setOverallCoverage(double overallCoverage) { this.overallCoverage = overallCoverage; }
        public int getCoveredLines() { return coveredLines; }
        public void setCoveredLines(int coveredLines) { this.coveredLines = coveredLines; }
        public int getTotalLines() { return totalLines; }
        public void setTotalLines(int totalLines) { this.totalLines = totalLines; }
        public List<FileCoverage> getFileCoverages() { return fileCoverages; }
        public void setFileCoverages(List<FileCoverage> fileCoverages) { this.fileCoverages = fileCoverages; }
    }
    
    /**
     * 文件覆盖率
     */
    public static class FileCoverage {
        private String filePath;
        private double coverage;
        private int coveredLines;
        private int totalLines;
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public double getCoverage() { return coverage; }
        public void setCoverage(double coverage) { this.coverage = coverage; }
        public int getCoveredLines() { return coveredLines; }
        public void setCoveredLines(int coveredLines) { this.coveredLines = coveredLines; }
        public int getTotalLines() { return totalLines; }
        public void setTotalLines(int totalLines) { this.totalLines = totalLines; }
    }
}
