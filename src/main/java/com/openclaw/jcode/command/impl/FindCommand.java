package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.*;

/**
 * 文件查找命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class FindCommand extends Command {
    
    public FindCommand() {
        this.name = "find";
        this.description = "文件查找";
        this.aliases = Arrays.asList("search", "locate");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showHelp();
        }
        
        String[] parts = args.trim().split("\\s+");
        String path = ".";
        String pattern = "*";
        String type = "file";
        
        // 解析参数
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.equals("-t") || part.equals("-type")) {
                if (i + 1 < parts.length) {
                    type = parts[++i];
                }
            } else if (part.startsWith("-")) {
                // 忽略其他参数
            } else if (path.equals(".") && !part.contains("*")) {
                path = part;
            } else {
                pattern = part;
            }
        }
        
        return findFiles(path, pattern, type);
    }
    
    private CommandResult findFiles(String path, String pattern, String type) {
        final List<String> results = new ArrayList<>();
        final Path startPath = Paths.get(path);
        final String finalType = type;
        final String finalPattern = pattern;
        
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if ("file".equals(finalType) || "all".equals(finalType)) {
                        if (matches(file.getFileName().toString(), finalPattern)) {
                            synchronized(results) {
                                results.add(startPath.relativize(file).toString());
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.getFileName().toString().startsWith(".")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if ("directory".equals(finalType) || "all".equals(finalType)) {
                        if (matches(dir.getFileName().toString(), finalPattern)) {
                            synchronized(results) {
                                results.add(startPath.relativize(dir).toString() + "/");
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
        } catch (IOException e) {
            return CommandResult.error("查找失败：" + e.getMessage());
        }
        
        // 限制结果数量
        int total = results.size();
        List<String> displayResults = results.size() > 50 ? results.subList(0, 50) : results;
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 文件查找结果\n\n");
        sb.append(String.format("**路径**: %s  **模式**: %s  **类型**: %s\n\n", path, pattern, type));
        
        if (displayResults.isEmpty()) {
            sb.append("未找到匹配的文件\n");
        } else {
            sb.append("```\n");
            for (String result : displayResults) {
                sb.append(result).append("\n");
            }
            sb.append("```\n\n");
            
            if (total > 50) {
                sb.append(String.format("*显示前 50 个，共 %d 个结果*\n", total));
            } else {
                sb.append(String.format("共 %d 个结果\n", total));
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);
        data.put("pattern", pattern);
        data.put("type", type);
        data.put("results", displayResults);
        data.put("total", total);
        
        return CommandResult.success("查找完成")
                .withData(data)
                .withDisplayText(sb.toString());
    }
    
    private boolean matches(String filename, String pattern) {
        if (pattern.equals("*")) {
            return true;
        }
        
        // 简单通配符匹配
        String regex = pattern.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        return filename.matches(regex);
    }
    
    private CommandResult showHelp() {
        String report = """
            ## 文件查找帮助
            
            ### 用法
            
            ```
            find <路径> <模式> [-t <类型>]
            ```
            
            ### 参数
            
            - 路径：搜索起始目录（默认：当前目录）
            - 模式：文件名模式，支持 * 和 ? 通配符
            - -t, -type: 文件类型（file, directory, all）
            
            ### 示例
            
            ```
            find . "*.java"           # 查找所有 Java 文件
            find src "*.java"         # 在 src 目录查找 Java 文件
            find . "*.md" -t file     # 查找 Markdown 文件
            find . "test*" -t dir     # 查找 test 开头的目录
            ```
            """;
        
        return CommandResult.success("文件查找帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：find
            别名：search, locate
            描述：文件查找
            
            用法：
              find <路径> <模式> [-t <类型>]
            
            参数：
              路径    搜索起始目录（默认：当前目录）
              模式    文件名模式，支持 * 和 ?
              -t      文件类型（file, directory, all）
            
            示例：
              find . "*.java"
              find src "*.java"
              find . "*.md" -t file
            """;
    }
}
