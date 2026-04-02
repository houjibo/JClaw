package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

/**
 */
@Component
public class FileReadTool extends Tool {
    
    public FileReadTool() {
        this.name = "file_read";
        this.description = "读取文件内容，支持指定行范围和编码";
        this.category = ToolCategory.FILE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        Number startLine = (Number) params.get("startLine");
        Number endLine = (Number) params.get("endLine");
        String encoding = (String) params.getOrDefault("encoding", "UTF-8");
        
        if (path == null || path.isBlank()) {
            return ToolResult.error("path 参数不能为空");
        }
        
        try {
            Path filePath = context.getWorkingDirectory().resolve(path);
            
            if (!Files.exists(filePath)) {
                return ToolResult.error("文件不存在：" + path);
            }
            
            if (!Files.isRegularFile(filePath)) {
                return ToolResult.error("不是普通文件：" + path);
            }
            
            // 检查文件大小
            long size = Files.size(filePath);
            if (size > context.getMaxReadSize()) {
                return ToolResult.error(String.format("文件过大 (%.2f MB)，最大支持 %.2f MB", 
                        size / 1024.0 / 1024.0, context.getMaxReadSize() / 1024.0 / 1024.0));
            }
            
            String content = Files.readString(filePath, java.nio.charset.Charset.forName(encoding));
            
            // 如果指定了行范围，进行截取
            if (startLine != null || endLine != null) {
                String[] lines = content.split("\n");
                int start = startLine != null ? Math.max(0, startLine.intValue() - 1) : 0;
                int end = endLine != null ? Math.min(lines.length, endLine.intValue()) : lines.length;
                content = String.join("\n", java.util.Arrays.copyOfRange(lines, start, end));
            }
            
            System.out.println("[FileReadTool] 读取文件：" + path + " (" + content.length() + " bytes)");
            return ToolResult.success("文件读取成功", content);
            
        } catch (IOException e) {
            System.err.println("[FileReadTool] 读取文件失败：" + path + " - " + e.getMessage());
            return ToolResult.error("读取失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("path");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path      - 文件路径 (必填)
                 startLine - 起始行号 (可选)
                 endLine   - 结束行号 (可选)
                 encoding  - 文件编码，默认 UTF-8 (可选)
               示例:
                 file_read path="src/main.java"
                 file_read path="config.json" startLine=1 endLine=50
               """.formatted(name, description);
    }
}
