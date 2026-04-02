package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Tail 命令 - 查看文件尾部
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class TailCommand extends Command {
    
    public TailCommand() {
        this.name = "tail";
        this.description = "查看文件尾部";
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
        boolean follow = false;
        
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
            } else if ("-f".equals(part) || "--follow".equals(part)) {
                follow = true;
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
        
        return tail(file, lines, follow);
    }
    
    private CommandResult tail(String file, int lines, boolean follow) {
        Path filePath = Paths.get(file);
        
        if (!Files.exists(filePath)) {
            return CommandResult.error("文件不存在：" + file);
        }
        
        if (Files.isDirectory(filePath)) {
            return CommandResult.error("是目录：" + file);
        }
        
        try {
            List<String> allLines = Files.readAllLines(filePath);
            int start = Math.max(0, allLines.size() - lines);
            int actualLines = allLines.size() - start;
            
            StringBuilder sb = new StringBuilder();
            sb.append("==> ").append(file).append(" (后 ").append(actualLines).append(" 行) <==\n\n");
            
            for (int i = start; i < allLines.size(); i++) {
                sb.append(allLines.get(i)).append("\n");
            }
            
            if (follow) {
                sb.append("\n[正在追踪文件变化... 按 Ctrl+C 停止]");
            }
            
            if (allLines.size() > lines) {
                sb.insert(0, "... 前面有 " + start + " 行未显示\n");
            }
            
            return CommandResult.success("文件尾部")
                    .withDisplayText(sb.toString());
                    
        } catch (IOException e) {
            return CommandResult.error("读取文件失败：" + e.getMessage());
        }
    }
    
    @Override
    public String getHelp() {
        return """
            命令：tail
            描述：查看文件尾部
            
            用法：
              tail <文件>             # 查看后 10 行
              tail -n 20 <文件>       # 查看后 20 行
              tail -f <文件>          # 追踪文件变化
              tail -5 <文件>          # 查看后 5 行
            
            示例：
              tail README.md
              tail -f /var/log/app.log
              tail -n 50 src/main.java
            """;
    }
}
