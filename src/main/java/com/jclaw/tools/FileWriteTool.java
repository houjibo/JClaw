package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

/**
 */
@Component
public class FileWriteTool extends Tool {
    
    public FileWriteTool() {
        this.name = "file_write";
        this.description = "写入文件内容，如果文件存在则覆盖";
        this.category = ToolCategory.FILE;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String content = (String) params.get("content");
        String encoding = (String) params.getOrDefault("encoding", "UTF-8");
        Boolean createDirs = (Boolean) params.getOrDefault("createDirs", true);
        
        if (path == null || path.isBlank()) {
            return ToolResult.error("path 参数不能为空");
        }
        
        if (content == null) {
            return ToolResult.error("content 参数不能为空");
        }
        
        if (!context.isAllowWrite()) {
            return ToolResult.error("写操作未被允许");
        }
        
        try {
            Path filePath = context.getWorkingDirectory().resolve(path);
            
            // 创建父目录
            if (createDirs) {
                Path parent = filePath.getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                    System.out.println("[FileWriteTool] 创建目录：" + parent);
                }
            }
            
            // 写入文件
            Files.writeString(filePath, content, java.nio.charset.Charset.forName(encoding));
            
            System.out.println("[FileWriteTool] 写入文件：" + path + " (" + content.length() + " bytes)");
            return ToolResult.success("文件写入成功：" + path);
            
        } catch (IOException e) {
            System.err.println("[FileWriteTool] 写入文件失败：" + path + " - " + e.getMessage());
            return ToolResult.error("写入失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("path") && params.containsKey("content");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path       - 文件路径 (必填)
                 content    - 文件内容 (必填)
                 encoding   - 文件编码，默认 UTF-8 (可选)
                 createDirs - 是否自动创建父目录，默认 true (可选)
               示例:
                 file_write path="src/Test.java" content="public class Test {}"
               """.formatted(name, description);
    }
}
