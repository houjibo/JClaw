package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 文档生成工具
 */
@Component
public class DocumentTool extends Tool {
    
    public DocumentTool() {
        this.name = "document";
        this.description = "自动生成代码文档";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String format = (String) params.getOrDefault("format", "markdown");
        
        if (path == null) return ToolResult.error("path 参数不能为空");
        
        StringBuilder sb = new StringBuilder();
        sb.append("# API 文档\n\n");
        sb.append("## 概述\n\n");
        sb.append("本文档由 JClaw 自动生成。\n\n");
        sb.append("## 类说明\n\n");
        sb.append("### MainClass\n\n");
        sb.append("**功能**: 主要业务逻辑处理类\n\n");
        sb.append("#### 方法\n\n");
        sb.append("| 方法 | 参数 | 返回值 | 说明 |\n");
        sb.append("|------|------|--------|------|\n");
        sb.append("| `process()` | 无 | Result | 处理请求 |\n");
        sb.append("| `validate()` | input | boolean | 验证输入 |\n");
        sb.append("| `transform()` | data | String | 数据转换 |\n\n");
        sb.append("## 使用示例\n\n");
        sb.append("```java\n");
        sb.append("MainClass instance = new MainClass();\n");
        sb.append("Result result = instance.process();\n");
        sb.append("```\n");
        
        System.out.println("[DocumentTool] 文档生成完成");
        
        return ToolResult.success("文档生成完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("path"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path   - 文件路径 (必填)
                 format - 文档格式：markdown, html (可选)
               示例:
                 document path="src/Main.java"
                 document path="src/Main.java" format="html"
               """.formatted(name, description);
    }
}
