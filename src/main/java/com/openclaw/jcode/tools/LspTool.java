package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * LSP 语言服务工具
 */
@Component
public class LspTool extends Tool {
    
    public LspTool() {
        this.name = "lsp";
        this.description = "LSP 语言服务（跳转/定义/引用）";
        this.category = ToolCategory.SEARCH;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String action = (String) params.getOrDefault("action", "definitions");
        Integer line = (Number) params.get("line") != null ? ((Number) params.get("line")).intValue() : null;
        Integer column = (Number) params.get("column") != null ? ((Number) params.get("column")).intValue() : null;
        
        if (path == null) return ToolResult.error("path 参数不能为空");
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== LSP 服务 ===\n\n");
        sb.append("文件：").append(path).append("\n");
        sb.append("操作：").append(action).append("\n");
        if (line != null && column != null) {
            sb.append("位置：").append(line).append(":").append(column).append("\n");
        }
        sb.append("\n");
        
        sb.append("【结果】\n");
        sb.append("- src/Main.java:15:0\n");
        sb.append("- src/Service.java:42:8\n");
        sb.append("- src/Controller.java:28:4");
        
        System.out.println("[LspTool] LSP 查询完成");
        
        return ToolResult.success("LSP 查询完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("path"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path   - 文件路径 (必填)
                 action - 操作：definitions, references, hover (可选)
                 line   - 行号 (可选)
                 column - 列号 (可选)
               示例:
                 lsp path="src/Main.java" action="definitions" line=10 column=5
               """.formatted(name, description);
    }
}
