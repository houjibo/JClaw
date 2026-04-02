package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Less 命令 - 分页查看文件
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class LessCommand extends Command {
    
    public LessCommand() {
        this.name = "less";
        this.description = "分页查看文件";
        this.aliases = Arrays.asList("more", "pager");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return CommandResult.error("请指定文件路径");
        }
        
        String file = parts[0];
        int lines = 20; // 默认每页显示 20 行
        
        // 解析行数参数
        for (int i = 1; i < parts.length; i++) {
            if ("-n".equals(parts[i]) && i + 1 < parts.length) {
                try {
                    lines = Integer.parseInt(parts[++i]);
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }
        }
        
        return less(file, lines);
    }
    
    private CommandResult less(String file, int lines) {
        Path filePath = Paths.get(file);
        
        if (!Files.exists(filePath)) {
            return CommandResult.error("文件不存在：" + file);
        }
        
        if (Files.isDirectory(filePath)) {
            return CommandResult.error("是目录：" + file);
        }
        
        try {
            List<String> allLines = Files.readAllLines(filePath);
            int totalPages = (int) Math.ceil((double) allLines.size() / lines);
            
            StringBuilder sb = new StringBuilder();
            sb.append("## 分页查看：").append(file).append("\n\n");
            sb.append(String.format("**总行数**: %d  **每页**: %d 行  **总页数**: %d\n\n", 
                    allLines.size(), lines, totalPages));
            
            // 显示第一页
            sb.append("### 第 1 页\n\n");
            sb.append("```\n");
            
            int end = Math.min(lines, allLines.size());
            for (int i = 0; i < end; i++) {
                sb.append(String.format("%4d | %s\n", i + 1, allLines.get(i)));
            }
            
            sb.append("```\n\n");
            
            if (totalPages > 1) {
                sb.append(String.format("*还有 %d 页未显示，使用 `less -n %d %s` 查看指定页数*\n", 
                        totalPages - 1, lines, file));
            }
            
            // 文件统计
            long size = Files.size(filePath);
            sb.append("\n### 文件信息\n\n");
            sb.append("| 属性 | 值 |\n");
            sb.append("|------|------|\n");
            sb.append(String.format("| 大小 | %s |\n", formatSize(size)));
            sb.append(String.format("| 行数 | %d |\n", allLines.size()));
            sb.append(String.format("| 页数 | %d |\n", totalPages));
            
            return CommandResult.success("分页查看：" + file)
                    .withDisplayText(sb.toString());
                    
        } catch (IOException e) {
            return CommandResult.error("读取文件失败：" + e.getMessage());
        }
    }
    
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    @Override
    public String getHelp() {
        return """
            命令：less
            别名：more, pager
            描述：分页查看文件
            
            用法：
              less <文件>             # 查看文件
              less -n 50 <文件>       # 每页 50 行
            
            示例：
              less README.md
              less -n 100 large.log
            
            系统命令：
              less file.txt           # Linux/Mac
              more file.txt           # Windows
            """;
    }
}
