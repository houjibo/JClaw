package com.jclaw.code.service.impl;

// import com.jclaw.ai.service.AiService; // 暂时注释，避免编译依赖
import com.jclaw.code.dto.*;
import com.jclaw.code.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    // private final AiService aiService;
    
    @Override
    public CodeExplanation explainCode(String filePath, String language) {
        try {
            String code = readFile(filePath);
            
            // TODO: 使用 AI 进行代码解释
            String explanation = "代码解释功能待实现（需要 AI 服务集成）";
            
            return CodeExplanation.builder()
                .filePath(filePath)
                .language(language)
                .summary(extractSummary(explanation))
                .detailedExplanation(explanation)
                .keyFunctions(extractKeyFunctions(code, language))
                .dependencies(extractDependencies(code, language))
                .build();
                
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            throw new RuntimeException("读取文件失败", e);
        }
    }
    
    @Override
    public List<CodeOptimization> optimizeCode(String filePath) {
        try {
            String code = readFile(filePath);
            String language = detectLanguage(filePath);
            
            // TODO: 使用 AI 进行代码优化分析
            String suggestions = "代码优化功能待实现";
            
            return parseOptimizations(suggestions);
            
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
            
            // TODO: 使用 AI 进行安全扫描
            String scanResult = "安全扫描功能待实现";
            
            return parseSecurityReport(filePath, scanResult);
            
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
}
