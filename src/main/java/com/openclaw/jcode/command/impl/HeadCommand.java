package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Head 命令 - 查看文件头部
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class HeadCommand extends Command {
    
    public HeadCommand() {
        this.name = "head";
        this.description = "查看文件头部";
        this.aliases = Arrays.asList();
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        int lines = 10;
        String file = null;
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if ("-n".equals(part) || "--lines".equals(part)) {
                if (i + 1 < parts.length) {
                    try {
                        lines = Integer.parseInt(parts[++i]);
                    } catch (NumberFormatException e) {
                        // 忽略
                    }
                }
            } else if (part.startsWith("-")) {
                try {
                    lines = Math.abs(Integer.parseInt(part.substring(1)));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            } else {
                file = part;
            }
        }
        
        if (file == null) {
            return CommandResult.error("请指定文件路径");
        }
        
        return head(file, lines);
    }
    
    private CommandResult head(String file, int lines) {
        Path filePath = Paths.get(file);
        
        if (!Files.exists(filePath)) {
            return CommandResult.error("文件不存在：" + file);
        }
        
        if (Files.isDirectory(filePath)) {
            return CommandResult.error("是目录：" + file);
        }
        
        try {
            List<String> allLines = Files.readAllLines(filePath);
            int end = Math.min(lines, allLines.size());
            
            StringBuilder sb = new StringBuilder();
            sb.append("==> ").append(file).append(" (前 ").append(end).append(" 行) <==\n\n");
            
            for (int i = 0; i < end; i++) {
                sb.append(allLines.get(i)).append("\n");
            }
            
            if (allLines.size() > lines) {
                sb.append("\n... 还有 ").append(allLines.size() - lines).append(" 行未显示");
            }
            
            return CommandResult.success("文件头部")
                    .withDisplayText(sb.toString());
                    
        } catch (IOException e) {
            return CommandResult.error("读取文件失败：" + e.getMessage());
        }
    }
    
    @Override
    public String getHelp() {
        return """
            命令：head
            描述：查看文件头部
            
            用法：
              head <文件>             # 查看前 10 行
              head -n 20 <文件>       # 查看前 20 行
              head -5 <文件>          # 查看前 5 行
            
            示例：
              head README.md
              head -n 20 src/main.java
            """;
    }
}
