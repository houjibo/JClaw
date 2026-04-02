package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * 日志查看命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class LogCommand extends Command {
    
    public LogCommand() {
        this.name = "log";
        this.description = "日志查看";
        this.aliases = Arrays.asList("logs");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return CommandResult.error("请指定日志文件路径");
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "view", "show" -> viewLog(parts.length > 1 ? parts[1] : null, parts.length > 2 ? Integer.parseInt(parts[2]) : 50);
            case "tail" -> tailLog(parts.length > 1 ? parts[1] : null, parts.length > 2 ? Integer.parseInt(parts[2]) : 20);
            case "search" -> searchLog(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            case "recent" -> showRecentLogs(parts.length > 1 ? parts[1] : null);
            default -> viewLog(parts[0], 50);
        };
    }
    
    private CommandResult viewLog(String file, int lines) {
        if (file == null) {
            return CommandResult.error("请指定日志文件路径");
        }
        
        Path filePath = Paths.get(file);
        
        if (!Files.exists(filePath)) {
            return CommandResult.error("文件不存在：" + file);
        }
        
        try {
            List<String> allLines = Files.readAllLines(filePath);
            int start = Math.max(0, allLines.size() - lines);
            int actualLines = allLines.size() - start;
            
            StringBuilder sb = new StringBuilder();
            sb.append("## 日志文件：").append(file).append("\n\n");
            sb.append(String.format("显示后 %d 行（共 %d 行）\n\n", actualLines, allLines.size()));
            sb.append("```\n");
            
            for (int i = start; i < allLines.size(); i++) {
                sb.append(allLines.get(i)).append("\n");
            }
            
            sb.append("```");
            
            return CommandResult.success("日志查看")
                    .withDisplayText(sb.toString());
                    
        } catch (IOException e) {
            return CommandResult.error("读取日志失败：" + e.getMessage());
        }
    }
    
    private CommandResult tailLog(String file, int lines) {
        return viewLog(file, lines);
    }
    
    private CommandResult searchLog(String file, String pattern) {
        if (file == null) {
            return CommandResult.error("请指定日志文件路径");
        }
        
        if (pattern == null) {
            return CommandResult.error("请指定搜索关键词");
        }
        
        Path filePath = Paths.get(file);
        
        if (!Files.exists(filePath)) {
            return CommandResult.error("文件不存在：" + file);
        }
        
        try {
            List<String> allLines = Files.readAllLines(filePath);
            List<String> matchedLines = new ArrayList<>();
            
            for (String line : allLines) {
                if (line.toLowerCase().contains(pattern.toLowerCase())) {
                    matchedLines.add(line);
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("## 日志搜索：").append(file).append("\n\n");
            sb.append(String.format("**关键词**: %s\n", pattern));
            sb.append(String.format("**匹配**: %d / %d 行\n\n", matchedLines.size(), allLines.size()));
            
            if (matchedLines.isEmpty()) {
                sb.append("未找到匹配的行\n");
            } else {
                sb.append("```\n");
                for (String line : matchedLines) {
                    sb.append(line).append("\n");
                }
                sb.append("```");
            }
            
            return CommandResult.success("日志搜索")
                    .withDisplayText(sb.toString());
                    
        } catch (IOException e) {
            return CommandResult.error("搜索日志失败：" + e.getMessage());
        }
    }
    
    private CommandResult showRecentLogs(String dir) {
        Path logDir = dir != null ? Paths.get(dir) : Paths.get(".");
        
        if (!Files.exists(logDir)) {
            return CommandResult.error("目录不存在：" + dir);
        }
        
        List<Path> logFiles = new ArrayList<>();
        
        try (Stream<Path> walk = Files.walk(logDir, 3)) {
            walk.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".log") || p.toString().endsWith(".txt"))
                .forEach(logFiles::add);
        } catch (IOException e) {
            return CommandResult.error("扫描目录失败：" + e.getMessage());
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 最近的日志文件\n\n");
        sb.append(String.format("**目录**: %s\n\n", dir != null ? dir : "."));
        
        if (logFiles.isEmpty()) {
            sb.append("未找到日志文件\n");
        } else {
            sb.append("| 文件 | 大小 | 修改时间 |\n");
            sb.append("|------|------|----------|\n");
            
            for (Path logFile : logFiles.subList(0, Math.min(20, logFiles.size()))) {
                try {
                    long size = Files.size(logFile);
                    String modified = Files.getLastModifiedTime(logFile).toString().substring(0, 16);
                    
                    sb.append(String.format("| %s | %s | %s |\n",
                            logFile.getFileName(),
                            formatSize(size),
                            modified));
                } catch (IOException e) {
                    // 忽略
                }
            }
            
            if (logFiles.size() > 20) {
                sb.append(String.format("\n... 还有 %d 个文件\n", logFiles.size() - 20));
            }
        }
        
        return CommandResult.success("最近日志")
                .withDisplayText(sb.toString());
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
    
    @Override
    public String getHelp() {
        return """
            命令：log
            别名：logs
            描述：日志查看
            
            用法：
              log view <文件> [行数]     # 查看日志
              log tail <文件> [行数]     # 查看尾部
              log search <文件> <关键词> # 搜索日志
              log recent [目录]          # 最近日志
            
            示例：
              log view /var/log/app.log
              log tail app.log 100
              log search app.log ERROR
              log recent ./logs
            """;
    }
}
