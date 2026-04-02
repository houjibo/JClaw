package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;

/**
 */
@Component
public class GrepTool extends Tool {
    
    public GrepTool() {
        this.name = "grep";
        this.description = "在代码中搜索文本，支持正则表达式";
        this.category = ToolCategory.SEARCH;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String pattern = (String) params.get("pattern");
        String path = (String) params.get("path");
        Boolean ignoreCase = (Boolean) params.getOrDefault("ignoreCase", false);
        Boolean includeHidden = (Boolean) params.getOrDefault("includeHidden", false);
        Integer maxResults = (Integer) params.getOrDefault("maxResults", 100);
        List<String> include = getListParam(params.get("include"));
        List<String> exclude = getListParam(params.get("exclude"));
        
        if (pattern == null || pattern.isBlank()) {
            return ToolResult.error("pattern 参数不能为空");
        }
        
        try {
            Path searchPath = context.getWorkingDirectory().resolve(path != null ? path : ".");
            
            if (!Files.exists(searchPath)) {
                return ToolResult.error("路径不存在：" + path);
            }
            
            Pattern regex = ignoreCase 
                    ? Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                    : Pattern.compile(pattern);
            
            List<GrepResult> results = new ArrayList<>();
            
            try (Stream<Path> paths = Files.walk(searchPath)) {
                paths.filter(Files::isRegularFile)
                     .filter(p -> !isHidden(p) || includeHidden)
                     .filter(p -> matchesFilters(p, include, exclude))
                     .forEach(p -> {
                         if (results.size() >= maxResults) return;
                         searchInFile(p, regex, results, maxResults);
                     });
            }
            
            String output = formatResults(results);
            System.out.println("[GrepTool] 搜索完成：找到 " + results.size() + " 个匹配");
            return ToolResult.success("找到 " + results.size() + " 个匹配", output);
            
        } catch (IOException e) {
            System.err.println("[GrepTool] 搜索失败：" + e.getMessage());
            return ToolResult.error("搜索失败：" + e.getMessage());
        }
    }
    
    private void searchInFile(Path file, Pattern pattern, List<GrepResult> results, int maxResults) {
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 0; i < lines.size() && results.size() < maxResults; i++) {
                String line = lines.get(i);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    GrepResult result = new GrepResult();
                    result.setFile(file.toString());
                    result.setLine(i + 1);
                    result.setContent(line.trim());
                    results.add(result);
                }
            }
        } catch (IOException e) {
            // 跳过无法读取的文件
        }
    }
    
    private boolean isHidden(Path path) {
        return path.toString().contains("/.") || path.toString().contains("\\.");
    }
    
    private boolean matchesFilters(Path path, List<String> include, List<String> exclude) {
        String pathStr = path.toString().toLowerCase();
        
        if (!include.isEmpty()) {
            boolean matched = include.stream().anyMatch(p -> pathStr.endsWith(p.toLowerCase()));
            if (!matched) return false;
        }
        
        if (!exclude.isEmpty()) {
            boolean excluded = exclude.stream().anyMatch(p -> pathStr.contains(p.toLowerCase()));
            if (excluded) return false;
        }
        
        return true;
    }
    
    private List<String> getListParam(Object param) {
        if (param == null) return new ArrayList<>();
        if (param instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        if (param instanceof String str) {
            return Arrays.asList(str.split(","));
        }
        return new ArrayList<>();
    }
    
    private String formatResults(List<GrepResult> results) {
        if (results.isEmpty()) {
            return "未找到匹配项";
        }
        
        StringBuilder sb = new StringBuilder();
        for (GrepResult result : results) {
            sb.append(String.format("%s:%d: %s\n", result.getFile(), result.getLine(), result.getContent()));
        }
        return sb.toString();
    }
    
    static class GrepResult {
        private String file;
        private int line;
        private String content;
        
        public String getFile() { return file; }
        public void setFile(String file) { this.file = file; }
        public int getLine() { return line; }
        public void setLine(int line) { this.line = line; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("pattern");
    }
}
