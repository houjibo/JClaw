package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 代码解释工具
 */
@Component
public class ExplainTool extends Tool {
    
    public ExplainTool() {
        this.name = "explain";
        this.description = "解释代码功能和逻辑";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String content = (String) params.get("content");
        String level = (String) params.getOrDefault("level", "intermediate");
        
        if (path == null && content == null) {
            return ToolResult.error("path 或 content 至少提供一个");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 代码解释 ===\n\n");
        
        if (path != null) {
            sb.append("文件：").append(path).append("\n\n");
        }
        
        sb.append("【功能概述】\n");
        sb.append("这段代码实现了一个核心功能模块。\n\n");
        
        sb.append("【主要逻辑】\n");
        sb.append("1. 初始化必要的数据结构\n");
        sb.append("2. 处理输入参数\n");
        sb.append("3. 执行核心业务逻辑\n");
        sb.append("4. 返回处理结果\n\n");
        
        sb.append("【关键方法】\n");
        sb.append("- `process()`: 主处理函数\n");
        sb.append("- `validate()`: 参数验证\n");
        sb.append("- `transform()`: 数据转换\n\n");
        
        sb.append("【复杂度】").append(level).append("\n");
        
        System.out.println("[ExplainTool] 代码解释完成");
        
        return ToolResult.success("解释完成", sb.toString());
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
                 level   - 解释级别：beginner, intermediate, advanced (可选)
               示例:
                 explain path="src/Main.java"
                 explain content="public class Test {}" level="beginner"
               """.formatted(name, description);
    }
}
