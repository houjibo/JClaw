package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 文件浏览/管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class FilesCommand extends Command {
    
    public FilesCommand() {
        this.name = "files";
        this.description = "文件浏览/管理";
        this.aliases = Arrays.asList("file", "fs");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listFiles(".");
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> listFiles(parts.length > 1 ? parts[1] : ".");
            case "create", "touch" -> createFile(parts.length > 1 ? parts[1] : null);
            case "mkdir" -> createDirectory(parts.length > 1 ? parts[1] : null);
            case "delete", "rm" -> deleteFile(parts.length > 1 ? parts[1] : null);
            case "move", "mv" -> moveFile(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            case "copy", "cp" -> copyFile(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            case "info" -> fileInfo(parts.length > 1 ? parts[1] : null);
            case "tree" -> treeView(parts.length > 1 ? parts[1] : ".");
            default -> listFiles(".");
        };
    }
    
    private CommandResult listFiles(String path) {
        Path dir = Paths.get(path);
        
        if (!Files.exists(dir)) {
            return CommandResult.error("目录不存在：" + path);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 文件列表\n\n");
        sb.append(String.format("**路径**: %s\n\n", dir.toAbsolutePath()));
        sb.append("| 类型 | 名称 | 大小 | 修改时间 |\n");
        sb.append("|------|------|------|----------|\n");
        
        try {
            Files.list(dir).sorted().forEach(p -> {
                try {
                    String type = Files.isDirectory(p) ? "📁" : "📄";
                    String name = p.getFileName().toString();
                    String size = Files.isDirectory(p) ? "-" : formatSize(Files.size(p));
                    String modified = Files.getLastModifiedTime(p).toString().substring(0, 16);
                    
                    sb.append(String.format("| %s | %s | %s | %s |\n", type, name, size, modified));
                } catch (IOException e) {
                    // 忽略
                }
            });
        } catch (IOException e) {
            return CommandResult.error("读取目录失败：" + e.getMessage());
        }
        
        return CommandResult.success("文件列表")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult createFile(String path) {
        if (path == null) {
            return CommandResult.error("请指定文件路径");
        }
        
        try {
            Path filePath = Paths.get(path);
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            
            return CommandResult.success("文件已创建：" + path)
                    .withDisplayText("✅ 文件 `" + path + "` 已创建");
        } catch (IOException e) {
            return CommandResult.error("创建文件失败：" + e.getMessage());
        }
    }
    
    private CommandResult createDirectory(String path) {
        if (path == null) {
            return CommandResult.error("请指定目录路径");
        }
        
        try {
            Files.createDirectories(Paths.get(path));
            
            return CommandResult.success("目录已创建：" + path)
                    .withDisplayText("✅ 目录 `" + path + "` 已创建");
        } catch (IOException e) {
            return CommandResult.error("创建目录失败：" + e.getMessage());
        }
    }
    
    private CommandResult deleteFile(String path) {
        if (path == null) {
            return CommandResult.error("请指定文件路径");
        }
        
        try {
            Files.deleteIfExists(Paths.get(path));
            
            return CommandResult.success("文件已删除：" + path)
                    .withDisplayText("✅ 文件 `" + path + "` 已删除");
        } catch (IOException e) {
            return CommandResult.error("删除文件失败：" + e.getMessage());
        }
    }
    
    private CommandResult moveFile(String source, String target) {
        if (source == null || target == null) {
            return CommandResult.error("用法：files move <源路径> <目标路径>");
        }
        
        try {
            Files.move(Paths.get(source), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            
            return CommandResult.success("文件已移动：" + source + " -> " + target)
                    .withDisplayText("✅ 文件已移动");
        } catch (IOException e) {
            return CommandResult.error("移动文件失败：" + e.getMessage());
        }
    }
    
    private CommandResult copyFile(String source, String target) {
        if (source == null || target == null) {
            return CommandResult.error("用法：files copy <源路径> <目标路径>");
        }
        
        try {
            Files.copy(Paths.get(source), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            
            return CommandResult.success("文件已复制：" + source + " -> " + target)
                    .withDisplayText("✅ 文件已复制");
        } catch (IOException e) {
            return CommandResult.error("复制文件失败：" + e.getMessage());
        }
    }
    
    private CommandResult fileInfo(String path) {
        if (path == null) {
            return CommandResult.error("请指定文件路径");
        }
        
        Path filePath = Paths.get(path);
        
        if (!Files.exists(filePath)) {
            return CommandResult.error("文件不存在：" + path);
        }
        
        try {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            
            String report = String.format("""
                ## 文件详情：%s
                
                ### 基本信息
                
                | 属性 | 值 |
                |------|------|
                | 类型 | %s |
                | 大小 | %s |
                | 绝对路径 | %s |
                
                ### 时间信息
                
                | 属性 | 值 |
                |------|------|
                | 创建时间 | %s |
                | 修改时间 | %s |
                | 访问时间 | %s |
                
                ### 属性
                
                | 属性 | 值 |
                |------|------|
                | 目录 | %s |
                | 常规文件 | %s |
                | 符号链接 | %s |
                """,
                    path,
                    attrs.isDirectory() ? "目录" : "文件",
                    formatSize(attrs.size()),
                    filePath.toAbsolutePath(),
                    attrs.creationTime(),
                    attrs.lastModifiedTime(),
                    attrs.lastAccessTime(),
                    attrs.isDirectory(),
                    attrs.isRegularFile(),
                    attrs.isSymbolicLink());
            
            return CommandResult.success("文件详情")
                    .withDisplayText(report);
        } catch (IOException e) {
            return CommandResult.error("读取文件信息失败：" + e.getMessage());
        }
    }
    
    private CommandResult treeView(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 目录树\n\n");
        sb.append(String.format("**根目录**: %s\n\n", path));
        sb.append("```\n");
        
        try {
            Path root = Paths.get(path);
            int prefixLen = root.toString().length() + 1;
            
            Files.walk(root).forEach(p -> {
                if (p.equals(root)) {
                    sb.append(p.getFileName()).append("/\n");
                } else {
                    String indent = "  ".repeat(p.getNameCount() - root.getNameCount());
                    String suffix = Files.isDirectory(p) ? "/" : "";
                    sb.append(indent).append("├── ").append(p.getFileName()).append(suffix).append("\n");
                }
            });
            
            sb.append("```");
        } catch (IOException e) {
            return CommandResult.error("生成目录树失败：" + e.getMessage());
        }
        
        return CommandResult.success("目录树")
                .withDisplayText(sb.toString());
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
            命令：files
            别名：file, fs
            描述：文件浏览/管理
            
            用法：
              files                   # 列出当前目录
              files list <path>       # 列出目录
              files create <path>     # 创建文件
              files mkdir <path>      # 创建目录
              files delete <path>     # 删除文件
              files move <src> <dst>  # 移动文件
              files copy <src> <dst>  # 复制文件
              files info <path>       # 文件详情
              files tree <path>       # 目录树
            
            示例：
              files
              files list src
              files create test.txt
              files tree .
            """;
    }
}
