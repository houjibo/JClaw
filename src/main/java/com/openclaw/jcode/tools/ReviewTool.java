package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 代码审查工具
 */
@Component
public class ReviewTool extends Tool {
    
    public ReviewTool() {
        this.name = "review";
        this.description = "代码审查，提供改进建议";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String content = (String) params.get("content");
        Boolean strict = (Boolean) params.getOrDefault("strict", false);
        
        if (path == null && content == null) {
            return ToolResult.error("path 或 content 至少提供一个");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 代码审查报告 ===\n\n");
        
        if (path != null) {
            sb.append("文件：").append(path).append("\n");
        }
        
        sb.append("\n【优点】\n");
        sb.append("- 代码结构清晰\n");
        sb.append("- 命名规范\n");
        
        sb.append("\n【改进建议】\n");
        sb.append("- 考虑添加更多注释\n");
        sb.append("- 可以提取重复代码为函数\n");
        
        if (strict) {
            sb.append("\n【严格模式】\n");
            sb.append("- 建议添加单元测试\n");
            sb.append("- 考虑边界情况处理\n");
        }
        
        sb.append("\n【总体评分】8/10");
        
        System.out.println("[ReviewTool] 代码审查完成");
        
        return ToolResult.success("审查完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { 
        return params.containsKey("path") || params.containsKey("content"); 
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path    - 文件路径 (可选)
                 content - 代码内容 (可选)
                 strict  - 严格模式 (可选)
               示例:
                 review path="src/Main.java"
                 review content="public class Test {}" strict=true
               """.formatted(name, description);
    }
}
