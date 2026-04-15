package com.jclaw.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.http.HttpRequest;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

/**
 * 文件上传工具类
 * 
 * 功能：
 * - Multipart 文件处理
 * - 文件类型检测
 * - 文件大小限制
 * - 临时文件管理
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Component
public class FileUploadUtil {
    
    /**
     * 默认最大文件大小 (50MB)
     */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    
    /**
     * 临时文件目录
     */
    private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "jclaw-uploads");
    
    static {
        try {
            Files.createDirectories(TEMP_DIR);
        } catch (IOException e) {
            log.error("创建临时目录失败", e);
        }
    }
    
    /**
     * 保存上传的文件
     */
    public FileUploadResult saveFile(MultipartFile file) throws IOException {
        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("文件大小超过限制 (50MB): " + file.getSize());
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (!isValidContentType(contentType)) {
            throw new IOException("不支持的文件类型：" + contentType);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;
        
        // 保存到临时目录
        Path filePath = TEMP_DIR.resolve(filename);
        file.transferTo(filePath);
        
        log.info("文件上传成功：{} ({} bytes)", filename, file.getSize());
        
        return new FileUploadResult(
            filename,
            originalFilename,
            contentType,
            file.getSize(),
            filePath.toString()
        );
    }
    
    /**
     * 读取文件内容为字节数组
     */
    public byte[] readFileBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
    
    /**
     * 读取文件内容为 Base64
     */
    public String readFileBase64(String filePath) throws IOException {
        byte[] bytes = readFileBytes(filePath);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * 删除临时文件
     */
    public boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            log.error("删除文件失败：{}", filePath, e);
            return false;
        }
    }
    
    /**
     * 清理过期的临时文件
     */
    public void cleanupOldFiles(long maxAgeHours) {
        long cutoffTime = System.currentTimeMillis() - (maxAgeHours * 3600 * 1000);
        
        try {
            Files.list(TEMP_DIR)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        log.info("清理过期文件：{}", path.getFileName());
                    } catch (IOException e) {
                        log.error("清理文件失败：{}", path, e);
                    }
                });
        } catch (IOException e) {
            log.error("列出临时文件失败", e);
        }
    }
    
    /**
     * 验证文件类型
     */
    private boolean isValidContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        
        // 允许的类型
        String[] allowedTypes = {
            "image/", "text/", "application/pdf",
            "application/zip", "application/json",
            "application/xml", "video/", "audio/"
        };
        
        for (String allowed : allowedTypes) {
            if (contentType.startsWith(allowed)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0) {
            return "";
        }
        
        return filename.substring(lastDot).toLowerCase();
    }
    
    /**
     * 文件上传结果
     */
    public static class FileUploadResult {
        private final String filename;
        private final String originalFilename;
        private final String contentType;
        private final long size;
        private final String filePath;
        
        public FileUploadResult(String filename, String originalFilename, 
                               String contentType, long size, String filePath) {
            this.filename = filename;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.size = size;
            this.filePath = filePath;
        }
        
        public String getFilename() { return filename; }
        public String getOriginalFilename() { return originalFilename; }
        public String getContentType() { return contentType; }
        public long getSize() { return size; }
        public String getFilePath() { return filePath; }
    }
}
