package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

/**
 */
@Component
public class GlobTool extends Tool {
    
    public GlobTool() {
        this.name = "glob";
        this.description = "根据模式匹配文件路径";
        this.category = ToolCategory.SEARCH;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String pattern = (String) params.get("pattern");
        String path = (String) params.get("path");
        Boolean includeHidden = (Boolean) params.getOrDefault("includeHidden", false);
        Integer maxResults = (Integer) params.getOrDefault("maxResults", 100);
        
        if (pattern == null || pattern.isBlank()) {
            return ToolResult.error("pattern 参数不能为空");
        }
        
        try {
            Path searchPath = context.getWorkingDirectory().resolve(path != null ? path : ".");
            
            if (!Files.exists(searchPath)) {
                return ToolResult.error("路径不存在：" + path);
            }
            
            List<String> matches = new ArrayList<>();
            
            try (Stream<Path> paths = Files.walk(searchPath)) {
                paths.filter(Files::isRegularFile)
                     .filter(p -> !isHidden(p) || includeHidden)
                     .filter(p -> matchesPattern(p, searchPath, pattern))
                     .map(Path::toString)
                     .limit(maxResults)
                     .forEach(matches::add);
            }
            
            String output = String.join("\n", matches);
            System.out.println("[GlobTool] 匹配完成：找到 " + matches.size() + " 个文件");
            return ToolResult.success("找到 " + matches.size() + " 个匹配文件", output);
            
        } catch (IOException e) {
            System.err.println("[GlobTool] 匹配失败：" + e.getMessage());
            return ToolResult.error("匹配失败：" + e.getMessage());
        }
    }
    
    private boolean matchesPattern(Path path, Path root, String pattern) {
        String relativePath = root.relativize(path).toString().replace("\\", "/");
        String regex = patternToRegex(pattern);
        return relativePath.matches(regex);
    }
    
    private String patternToRegex(String pattern) {
        // 将 glob 模式转换为正则表达式
        String regex = pattern
                .replace(".", "\\.")
                .replace("**/", "(.*/)?")
                .replace("**", ".*")
                .replace("*", "[^/]*")
                .replace("?", "[^/]");
        return ".*" + regex + "$";
    }
    
    private boolean isHidden(Path path) {
        return path.toString().contains("/.") || path.toString().contains("\\.");
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("pattern");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 pattern       - 文件匹配模式，支持 * 和 ** (必填)
                 path          - 搜索目录，默认当前目录 (可选)
                 includeHidden - 是否包含隐藏文件，默认 false (可选)
                 maxResults    - 最大结果数，默认 100 (可选)
               示例:
                 glob pattern="**/*.java"
                 glob pattern="src/**/*.java"
                 glob pattern="*.md" path="docs"
               """.formatted(name, description);
    }
}
