package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Cat 命令 - 查看文件内容
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CatCommand extends Command {
    
    public CatCommand() {
        this.name = "cat";
        this.description = "查看文件内容";
        this.aliases = Arrays.asList("concatenate");
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
        
        boolean showNumbers = false;
        boolean showNonEmpty = false;
        boolean squeezeBlank = false;
        List<String> files = new ArrayList<>();
        
        for (String part : parts) {
            if ("-n".equals(part)) showNumbers = true;
            else if ("-b".equals(part)) showNonEmpty = true;
            else if ("-s".equals(part)) squeezeBlank = true;
            else if (!part.startsWith("-")) files.add(part);
        }
        
        if (files.isEmpty()) {
            return CommandResult.error("请指定文件路径");
        }
        
        return cat(files, showNumbers || showNonEmpty, showNonEmpty, squeezeBlank);
    }
    
    private CommandResult cat(List<String> files, boolean numberLines, boolean numberNonEmpty, boolean squeezeBlank) {
        StringBuilder sb = new StringBuilder();
        
        for (String file : files) {
            Path filePath = Paths.get(file);
            
            if (!Files.exists(filePath)) {
                return CommandResult.error("文件不存在：" + file);
            }
            
            if (Files.isDirectory(filePath)) {
                return CommandResult.error("是目录：" + file);
            }
            
            try {
                List<String> lines = Files.readAllLines(filePath);
                int lineNum = 0;
                int nonEmptyNum = 0;
                int prevBlank = 0;
                
                if (files.size() > 1) {
                    sb.append("==> ").append(file).append(" <==\n");
                }
                
                for (String line : lines) {
                    boolean isBlank = line.trim().isEmpty();
                    
                    if (squeezeBlank && isBlank) {
                        if (prevBlank > 0) continue;
                        prevBlank++;
                    } else {
                        prevBlank = 0;
                    }
                    
                    if (numberNonEmpty && !isBlank) {
                        nonEmptyNum++;
                        sb.append(String.format("%6d\t", nonEmptyNum));
                    } else if (numberLines) {
                        lineNum++;
                        sb.append(String.format("%6d\t", lineNum));
                    }
                    
                    sb.append(line).append("\n");
                }
                
            } catch (IOException e) {
                return CommandResult.error("读取文件失败：" + e.getMessage());
            }
        }
        
        return CommandResult.success("文件内容")
                .withDisplayText(sb.toString());
    }
    
    @Override
    public String getHelp() {
        return """
            命令：cat
            描述：查看文件内容
            
            用法：
              cat <文件>              # 查看文件
              cat -n <文件>           # 显示行号
              cat -b <文件>           # 非空行显示行号
              cat -s <文件>           # 压缩空行
              cat file1 file2         # 查看多个文件
            
            示例：
              cat README.md
              cat -n src/main.java
            """;
    }
}
