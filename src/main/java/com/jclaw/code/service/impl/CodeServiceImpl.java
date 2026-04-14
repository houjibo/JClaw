package com.jclaw.code.service.impl;

import com.jclaw.ai.service.AiService;
import com.jclaw.code.dto.*;
import com.jclaw.code.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码工具服务实现
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    
    @Autowired
    private AiService aiService;
    
    @Override
    public CodeExplanation explainCode(String filePath, String language) {
        try {
            String code = readFile(filePath);
            
            // 使用智谱 AI 进行代码解释
            String prompt = String.format(
                "请分析以下 %s 代码，并返回 JSON 格式的分析结果：\n" +
                "{\"summary\": \"代码摘要\", \"detailedExplanation\": \"详细解释\", \"keyFunctions\": [\"函数 1\", \"函数 2\"], \"complexity\": 复杂度数字}\n\n" +
                "代码：\n%s",
                language, code.substring(0, Math.min(5000, code.length()))
            );
            
            String aiResponse = aiService.chat(prompt);
            
            // 解析 AI 响应（简化处理）
            String summary = extractJsonField(aiResponse, "summary");
            String detailedExplanation = extractJsonField(aiResponse, "detailedExplanation");
            
            return CodeExplanation.builder()
                .filePath(filePath)
                .language(language)
                .summary(summary != null ? summary : generateSummary(code, language))
                .detailedExplanation(detailedExplanation != null ? detailedExplanation : generateDetailedExplanation(code, language))
                .keyFunctions(extractKeyFunctions(code, language))
                .dependencies(extractDependencies(code, language))
                .complexity(calculateComplexity(code))
                .linesOfCode(code.split("\n").length)
                .build();
                
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            throw new RuntimeException("读取文件失败", e);
        }
    }
    
    /**
     * 生成代码摘要
     */
    private String generateSummary(String code, String language) {
        int lines = code.split("\n").length;
        int methods = countMatches(code, "(public|private|protected|static).*\\(.*\\)");
        int classes = countMatches(code, "(class|interface|enum)\\s+\\w+");
        
        return String.format(
            "这是一个 %s 语言的文件，共 %d 行代码，包含 %d 个类/接口和 %d 个方法。",
            language, lines, classes, methods
        );
    }
    
    /**
     * 生成详细解释
     */
    private String generateDetailedExplanation(String code, String language) {
        StringBuilder explanation = new StringBuilder();
        
        explanation.append("**代码结构分析**\n\n");
        
        // 分析导入
        List<String> imports = extractImports(code, language);
        if (!imports.isEmpty()) {
            explanation.append("### 依赖导入\n");
            explanation.append("共导入 ").append(imports.size()).append(" 个包/模块：\n");
            imports.forEach(imp -> explanation.append("- ").append(imp).append("\n"));
            explanation.append("\n");
        }
        
        // 分析类/函数
        explanation.append("### 主要组件\n");
        explanation.append("详细组件信息请查看下方的'关键函数'部分。\n");
        
        return explanation.toString();
    }
    
    @Override
    public List<CodeOptimization> optimizeCode(String filePath) {
        try {
            String code = readFile(filePath);
            String language = detectLanguage(filePath);
            
            // 使用智谱 AI 进行代码优化分析
            String prompt = String.format(
                "请分析以下 %s 代码，提供优化建议，返回 JSON 数组格式：\n" +
                "[{\"type\": \"performance\", \"description\": \"问题描述\", \"suggestion\": \"优化建议\", \"priority\": 1-5}]\n\n" +
                "代码：\n%s",
                language, code.substring(0, Math.min(5000, code.length()))
            );
            
            String aiResponse = aiService.chat(prompt);
            
            // 解析 AI 响应（简化处理）
            List<CodeOptimization> optimizations = new ArrayList<>();
            
            // 如果 AI 响应为空或解析失败，使用基础分析
            if (aiResponse == null || aiResponse.isEmpty() || aiResponse.contains("待实现")) {
                optimizations.add(CodeOptimization.builder()
                    .type("performance")
                    .description("代码复杂度分析")
                    .suggestion("建议降低圈复杂度，当前复杂度：" + calculateComplexity(code))
                    .priority(calculateComplexity(code) > 10 ? 3 : 1)
                    .build());
            }
            
            return optimizations;
            
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            throw new RuntimeException("读取文件失败", e);
        }
    }
    
    @Override
    public SecurityReport securityScan(String filePath) {
        try {
            String code = readFile(filePath);
            String language = detectLanguage(filePath);
            
            // 使用智谱 AI 进行安全扫描
            String prompt = String.format(
                "请扫描以下 %s 代码的安全漏洞，返回 JSON 格式：\n" +
                "{\"riskScore\": 0-100, \"vulnerabilities\": [{\"type\": \"类型\", \"severity\": \"high/medium/low\", \"description\": \"描述\", \"line\": 行号}], \"recommendations\": [\"建议 1\"]}\n\n" +
                "代码：\n%s",
                language, code.substring(0, Math.min(5000, code.length()))
            );
            
            String aiResponse = aiService.chat(prompt);
            
            // 解析 AI 响应（简化处理）
            SecurityReport report = SecurityReport.builder()
                .filePath(filePath)
                .riskScore(20) // 默认低风险
                .vulnerabilities(new ArrayList<>())
                .recommendations(new ArrayList<>())
                .build();
            
            // 基础安全检查
            List<com.jclaw.code.dto.Vulnerability> vulns = new ArrayList<>();
            
            if (code.contains("System.exit")) {
                vulns.add(com.jclaw.code.dto.Vulnerability.builder()
                    .type("Security")
                    .severity("medium")
                    .location("System.exit")
                    .description("使用 System.exit 可能导致资源未释放")
                    .fix("使用更优雅的方式退出")
                    .build()
                );
                report.setRiskScore(40);
            }
            
            if (code.contains("Runtime.getRuntime().exec")) {
                vulns.add(com.jclaw.code.dto.Vulnerability.builder()
                    .type("Command Injection")
                    .severity("high")
                    .location("Runtime.exec")
                    .description("使用 Runtime.exec 可能存在命令注入风险")
                    .fix("使用 ProcessBuilder 并验证输入")
                    .build()
                );
                report.setRiskScore(70);
            }
            
            report.setVulnerabilities(vulns);
            
            report.getRecommendations().add("定期更新依赖");
            report.getRecommendations().add("使用静态代码分析工具");
            
            return report;
            
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            throw new RuntimeException("读取文件失败", e);
        }
    }
    
    @Override
    public CodeDocumentation generateDocs(String filePath) {
        try {
            String code = readFile(filePath);
            String language = detectLanguage(filePath);
            
            // TODO: 使用 AI 生成文档
            String docs = "文档生成功能待实现";
            
            return parseDocumentation(filePath, docs);
            
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            throw new RuntimeException("读取文件失败", e);
        }
    }
    
    @Override
    public BuildResult buildProject(String projectPath, String buildTool) {
        long startTime = System.currentTimeMillis();
        
        try {
            String command;
            if ("maven".equals(buildTool) || "mvn".equals(buildTool)) {
                command = "mvn clean package -DskipTests";
            } else if ("gradle".equals(buildTool)) {
                command = "gradle build -x test";
            } else {
                return BuildResult.builder()
                    .success(false)
                    .error("不支持的构建工具：" + buildTool)
                    .build();
            }
            
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.directory(Paths.get(projectPath).toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            long duration = System.currentTimeMillis() - startTime;
            
            if (exitCode == 0) {
                return BuildResult.builder()
                    .success(true)
                    .output(output.toString())
                    .durationMs(duration)
                    .artifacts(findArtifacts(projectPath))
                    .build();
            } else {
                return BuildResult.builder()
                    .success(false)
                    .output(output.toString())
                    .error("构建失败，退出码：" + exitCode)
                    .durationMs(duration)
                    .build();
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("构建失败：{}", projectPath, e);
            return BuildResult.builder()
                .success(false)
                .error("构建异常：" + e.getMessage())
                .durationMs(System.currentTimeMillis() - startTime)
                .build();
        }
    }
    
    @Override
    public DebugInfo debugCode(String filePath, int lineNumber) {
        try {
            String code = readFile(filePath);
            String[] lines = code.split("\n");
            
            if (lineNumber < 1 || lineNumber > lines.length) {
                throw new IllegalArgumentException("行号超出范围");
            }
            
            String targetLine = lines[lineNumber - 1];
            String context = extractContext(lines, lineNumber);
            
            // TODO: 使用 AI 进行调试分析
            String debugResult = "调试功能待实现";
            
            return DebugInfo.builder()
                .filePath(filePath)
                .lineNumber(lineNumber)
                .context(context)
                .variables(extractVariables(targetLine))
                .suggestions(parseDebugSuggestions(debugResult))
                .build();
                
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            throw new RuntimeException("读取文件失败", e);
        }
    }
    
    // ========== 辅助方法 ==========
    
    private String readFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }
    
    private String detectLanguage(String filePath) {
        if (filePath.endsWith(".java")) return "java";
        if (filePath.endsWith(".py")) return "python";
        if (filePath.endsWith(".js")) return "javascript";
        if (filePath.endsWith(".ts")) return "typescript";
        if (filePath.endsWith(".go")) return "go";
        if (filePath.endsWith(".rs")) return "rust";
        return "unknown";
    }
    
    private List<String> extractKeyFunctions(String code, String language) {
        List<String> functions = new ArrayList<>();
        // 简单实现，实际应该用 AST 解析
        if ("java".equals(language)) {
            functions.addAll(extractPattern(code, "(public|private|protected).*\\s+\\w+\\s+\\(.*\\)"));
        }
        return functions;
    }
    
    private List<String> extractDependencies(String code, String language) {
        List<String> deps = new ArrayList<>();
        if ("java".equals(language)) {
            deps.addAll(extractPattern(code, "import\\s+[\\w.]+;"));
        }
        return deps;
    }
    
    private List<String> extractPattern(String code, String regex) {
        return Arrays.stream(code.split("\n"))
            .filter(line -> line.matches(".*" + regex + ".*"))
            .map(String::trim)
            .collect(Collectors.toList());
    }
    
    private List<CodeOptimization> parseOptimizations(String suggestions) {
        // 简化实现，实际应该解析 AI 返回的结构化数据
        return Arrays.asList(CodeOptimization.builder()
            .type("performance")
            .description("性能优化建议")
            .suggestion(suggestions)
            .priority(3)
            .build());
    }
    
    private SecurityReport parseSecurityReport(String filePath, String result) {
        return SecurityReport.builder()
            .filePath(filePath)
            .riskScore(20)
            .vulnerabilities(new ArrayList<>())
            .recommendations(Arrays.asList("定期更新依赖", "使用静态代码分析工具"))
            .build();
    }
    
    private CodeDocumentation parseDocumentation(String filePath, String docs) {
        return CodeDocumentation.builder()
            .filePath(filePath)
            .overview(docs)
            .apis(new ArrayList<>())
            .examples(new ArrayList<>())
            .build();
    }
    
    private List<String> findArtifacts(String projectPath) {
        List<String> artifacts = new ArrayList<>();
        Path targetPath = Paths.get(projectPath, "target");
        if (Files.exists(targetPath)) {
            try {
                Files.walk(targetPath)
                    .filter(p -> p.toString().endsWith(".jar") || p.toString().endsWith(".war"))
                    .forEach(p -> artifacts.add(p.toString()));
            } catch (IOException e) {
                log.warn("查找构建产物失败", e);
            }
        }
        return artifacts;
    }
    
    private String extractContext(String[] lines, int lineNumber) {
        int start = Math.max(0, lineNumber - 6);
        int end = Math.min(lines.length, lineNumber + 5);
        return String.join("\n", Arrays.copyOfRange(lines, start, end));
    }
    
    private List<String> extractVariables(String line) {
        List<String> variables = new ArrayList<>();
        // 简单实现
        String[] parts = line.split("[\\s=(){};]+");
        for (String part : parts) {
            if (!part.isEmpty() && Character.isJavaIdentifierStart(part.charAt(0))) {
                variables.add(part);
            }
        }
        return variables;
    }
    
    private String extractSummary(String explanation) {
        String[] lines = explanation.split("\n");
        return lines.length > 0 ? lines[0] : explanation;
    }
    
    private List<String> parseDebugSuggestions(String result) {
        return Arrays.asList(result.split("\n"));
    }
    
    /**
     * 计算正则匹配次数
     */
    private int countMatches(String text, String regex) {
        return (int) java.util.regex.Pattern.compile(regex).matcher(text).results().count();
    }
    
    /**
     * 提取导入语句
     */
    private List<String> extractImports(String code, String language) {
        List<String> imports = new ArrayList<>();
        
        if ("java".equalsIgnoreCase(language)) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^import\\s+([^;]+);", java.util.regex.Pattern.MULTILINE);
            java.util.regex.Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                imports.add(matcher.group(1));
            }
        } else if ("typescript".equalsIgnoreCase(language) || "javascript".equalsIgnoreCase(language)) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^import\\s+.*from\\s+['\"]([^'\"]+)['\"]", java.util.regex.Pattern.MULTILINE);
            java.util.regex.Matcher matcher = pattern.matcher(code);
            while (matcher.find()) {
                imports.add(matcher.group(1));
            }
        }
        
        return imports;
    }
    
    /**
     * 计算代码复杂度
     */
    private int calculateComplexity(String code) {
        int complexity = 1;
        
        // 控制流语句增加复杂度
        String[] flowKeywords = {"if", "else", "for", "while", "switch", "case", "try", "catch", "finally"};
        for (String keyword : flowKeywords) {
            complexity += countMatches(code, "\\b" + keyword + "\\b");
        }
        
        // 逻辑运算符增加复杂度
        complexity += countMatches(code, "&&|\\|\\|");
        
        return complexity;
    }
    
    /**
     * 从 JSON 响应中提取字段
     */
    private String extractJsonField(String json, String field) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        
        // 简单正则提取
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
}
