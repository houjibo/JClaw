package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Zip 压缩解压命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ZipCommand extends Command {
    
    public ZipCommand() {
        this.name = "zip";
        this.description = "压缩解压文件";
        this.aliases = Arrays.asList("unzip", "tar");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showHelp();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "compress", "c" -> compress(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            case "decompress", "d", "extract" -> decompress(parts.length > 1 ? parts[1] : null);
            case "list", "l" -> listContents(parts.length > 1 ? parts[1] : null);
            case "info" -> showHelp();
            default -> showHelp();
        };
    }
    
    private CommandResult showHelp() {
        return CommandResult.success("帮助")
                .withDisplayText(getHelp());
    }
    
    private CommandResult compress(String source, String output) {
        if (source == null) {
            return CommandResult.error("请指定源文件或目录");
        }
        
        if (output == null) {
            output = source + ".zip";
        }
        
        Path sourcePath = Paths.get(source);
        Path outputPath = Paths.get(output);
        
        if (!Files.exists(sourcePath)) {
            return CommandResult.error("源文件不存在：" + source);
        }
        
        try {
            int[] fileCount = {0};
            long[] totalSize = {0};
            
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath.toFile()))) {
                if (Files.isDirectory(sourcePath)) {
                    Files.walk(sourcePath).forEach(path -> {
                        if (!Files.isDirectory(path)) {
                            try {
                                ZipEntry entry = new ZipEntry(sourcePath.relativize(path).toString());
                                zos.putNextEntry(entry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                                fileCount[0]++;
                                totalSize[0] += Files.size(path);
                            } catch (IOException e) {
                                // 忽略
                            }
                        }
                    });
                } else {
                    ZipEntry entry = new ZipEntry(sourcePath.getFileName().toString());
                    zos.putNextEntry(entry);
                    Files.copy(sourcePath, zos);
                    zos.closeEntry();
                    fileCount[0] = 1;
                    totalSize[0] = Files.size(sourcePath);
                }
            }
            
            long compressedSize = Files.size(outputPath);
            double ratio = (1.0 - (double) compressedSize / totalSize[0]) * 100;
            
            String report = String.format("""
                ## 压缩完成
                
                **源**: %s
                **输出**: %s
                
                ### 统计
                
                | 指标 | 值 |
                |------|------|
                | 文件数 | %d |
                | 原始大小 | %s |
                | 压缩后大小 | %s |
                | 压缩率 | %.1f%% |
                
                ### 解压命令
                
                ```bash
                unzip %s
                ```
                """,
                source, output,
                fileCount[0],
                formatSize(totalSize[0]),
                formatSize(compressedSize),
                ratio,
                output);
            
            return CommandResult.success("压缩完成：" + output)
                    .withDisplayText(report);
                    
        } catch (IOException e) {
            return CommandResult.error("压缩失败：" + e.getMessage());
        }
    }
    
    private CommandResult decompress(String archive) {
        if (archive == null) {
            return CommandResult.error("请指定压缩文件");
        }
        
        Path archivePath = Paths.get(archive);
        
        if (!Files.exists(archivePath)) {
            return CommandResult.error("压缩文件不存在：" + archive);
        }
        
        try {
            int[] fileCount = {0};
            long[] totalSize = {0};
            
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath.toFile()))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path outputPath = Paths.get(entry.getName());
                    
                    if (entry.isDirectory()) {
                        Files.createDirectories(outputPath);
                    } else {
                        Files.createDirectories(outputPath.getParent());
                        Files.copy(zis, outputPath, StandardCopyOption.REPLACE_EXISTING);
                        fileCount[0]++;
                        totalSize[0] += Files.size(outputPath);
                    }
                    zis.closeEntry();
                }
            }
            
            String report = String.format("""
                ## 解压完成
                
                **源**: %s
                
                ### 统计
                
                | 指标 | 值 |
                |------|------|
                | 文件数 | %d |
                | 总大小 | %s |
                
                ### 解压位置
                
                当前目录
                """,
                archive,
                fileCount[0],
                formatSize(totalSize[0]));
            
            return CommandResult.success("解压完成")
                    .withDisplayText(report);
                    
        } catch (IOException e) {
            return CommandResult.error("解压失败：" + e.getMessage());
        }
    }
    
    private CommandResult listContents(String archive) {
        if (archive == null) {
            return CommandResult.error("请指定压缩文件");
        }
        
        Path archivePath = Paths.get(archive);
        
        if (!Files.exists(archivePath)) {
            return CommandResult.error("压缩文件不存在：" + archive);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 压缩文件内容\n\n");
        sb.append(String.format("**文件**: %s\n\n", archive));
        sb.append("| 名称 | 大小 | 压缩比 |\n");
        sb.append("|------|------|--------|\n");
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath.toFile()))) {
            ZipEntry entry;
            int count = 0;
            
            while ((entry = zis.getNextEntry()) != null && count < 50) {
                long compressedSize = entry.getCompressedSize();
                long uncompressedSize = entry.getSize();
                double ratio = uncompressedSize > 0 ? 
                    (1.0 - (double) compressedSize / uncompressedSize) * 100 : 0;
                
                sb.append(String.format("| %s | %s | %.1f%% |\n",
                        entry.getName(),
                        formatSize(uncompressedSize),
                        ratio));
                count++;
                zis.closeEntry();
            }
            
            if (count >= 50) {
                sb.append("| ... | ... | ... |\n");
            }
            
        } catch (IOException e) {
            return CommandResult.error("读取失败：" + e.getMessage());
        }
        
        return CommandResult.success("压缩文件内容")
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
            命令：zip
            别名：unzip, tar
            描述：压缩解压文件
            
            用法：
              zip compress <源> [输出]   # 压缩文件
              zip decompress <压缩包>   # 解压文件
              zip list <压缩包>         # 查看内容
            
            示例：
              zip compress myfolder
              zip decompress archive.zip
              zip list archive.zip
            """;
    }
}
