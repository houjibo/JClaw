package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 磁盘使用分析命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class DiskCommand extends Command {
    
    public DiskCommand() {
        this.name = "disk";
        this.description = "磁盘使用分析";
        this.aliases = Arrays.asList("df", "du");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showDiskUsage();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "usage", "du" -> showDiskUsage(parts.length > 1 ? parts[1] : ".");
            case "free", "df" -> showDiskFree();
            case "largest" -> showLargestFiles(parts.length > 1 ? parts[1] : ".", parts.length > 2 ? Integer.parseInt(parts[2]) : 10);
            default -> showDiskUsage();
        };
    }
    
    private CommandResult showDiskUsage() {
        return showDiskUsage(".");
    }
    
    private CommandResult showDiskUsage(String path) {
        File file = new File(path);
        
        if (!file.exists()) {
            return CommandResult.error("路径不存在：" + path);
        }
        
        long size = calculateSize(file);
        
        String report = String.format("""
            ## 磁盘使用分析
            
            **路径**: %s
            
            ### 大小信息
            
            | 指标 | 值 |
            |------|------|
            | 总大小 | %s |
            | 文件数 | %d |
            | 目录数 | %d |
            
            ### 子目录大小
            
            %s
            """,
            path,
            formatSize(size),
            countFiles(file),
            countDirectories(file),
            getSubdirectorySizes(file));
        
        return CommandResult.success("磁盘使用：" + path)
                .withDisplayText(report);
    }
    
    private CommandResult showDiskFree() {
        File[] roots = File.listRoots();
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 磁盘空间\n\n");
        sb.append("| 文件系统 | 总大小 | 已用 | 可用 | 使用率 |\n");
        sb.append("|---------|--------|------|------|--------|\n");
        
        for (File root : roots) {
            long total = root.getTotalSpace();
            long free = root.getFreeSpace();
            long used = total - free;
            double percent = (used * 100.0) / total;
            
            sb.append(String.format("| %s | %s | %s | %s | %.1f%% |\n",
                    root.getPath(),
                    formatSize(total),
                    formatSize(used),
                    formatSize(free),
                    percent));
        }
        
        return CommandResult.success("磁盘空间")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult showLargestFiles(String path, int count) {
        File file = new File(path);
        
        if (!file.exists()) {
            return CommandResult.error("路径不存在：" + path);
        }
        
        // 简单实现：列出当前目录下的文件和目录大小
        File[] files = file.listFiles();
        if (files == null) {
            return CommandResult.error("无法读取目录：" + path);
        }
        
        List<FileEntry> entries = new ArrayList<>();
        for (File f : files) {
            entries.add(new FileEntry(f.getName(), calculateSize(f), f.isDirectory()));
        }
        
        entries.sort((a, b) -> Long.compare(b.size, a.size));
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 最大文件/目录\n\n");
        sb.append(String.format("**路径**: %s  **Top**: %d\n\n", path, count));
        sb.append("| 名称 | 大小 | 类型 |\n");
        sb.append("|------|------|------|\n");
        
        int limit = Math.min(count, entries.size());
        for (int i = 0; i < limit; i++) {
            FileEntry entry = entries.get(i);
            sb.append(String.format("| %s | %s | %s |\n",
                    entry.name,
                    formatSize(entry.size),
                    entry.isDirectory ? "目录" : "文件"));
        }
        
        return CommandResult.success("最大文件")
                .withDisplayText(sb.toString());
    }
    
    private long calculateSize(File file) {
        long size = 0;
        if (file.isFile()) {
            return file.length();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    size += calculateSize(f);
                }
            }
        }
        return size;
    }
    
    private int countFiles(File file) {
        int count = 0;
        if (file.isFile()) {
            return 1;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    count += countFiles(f);
                }
            }
        }
        return count;
    }
    
    private int countDirectories(File file) {
        int count = 0;
        if (file.isDirectory()) {
            count++;
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    count += countDirectories(f);
                }
            }
        }
        return count;
    }
    
    private String getSubdirectorySizes(File file) {
        if (!file.isDirectory()) {
            return "N/A";
        }
        
        File[] files = file.listFiles();
        if (files == null) {
            return "N/A";
        }
        
        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            if (f.isDirectory()) {
                sb.append(String.format("- **%s**: %s\n", f.getName(), formatSize(calculateSize(f))));
            }
        }
        
        if (sb.length() == 0) {
            return "无子目录";
        }
        
        return sb.toString();
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
            命令：disk
            别名：df, du
            描述：磁盘使用分析
            
            用法：
              disk                    # 显示磁盘使用
              disk usage <路径>       # 查看目录大小
              disk free               # 查看磁盘空间
              disk largest <路径> [N] # 查看最大文件
            
            示例：
              disk
              disk usage .
              disk free
              disk largest . 10
            """;
    }
    
    // 文件条目类
    private static class FileEntry {
        String name;
        long size;
        boolean isDirectory;
        
        FileEntry(String name, long size, boolean isDirectory) {
            this.name = name;
            this.size = size;
            this.isDirectory = isDirectory;
        }
    }
}
