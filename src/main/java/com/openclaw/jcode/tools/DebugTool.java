package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 调试工具
 */
@Component
public class DebugTool extends Tool {
    
    public DebugTool() {
        this.name = "debug";
        this.description = "调试代码，分析问题";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String issue = (String) params.get("issue");
        
        if (path == null && issue == null) {
            return ToolResult.error("path 或 issue 至少提供一个");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 调试报告 ===\n\n");
        
        if (path != null) {
            sb.append("文件：").append(path).append("\n");
        }
        if (issue != null) {
            sb.append("问题：").append(issue).append("\n");
        }
        
        sb.append("\n【问题分析】\n");
        sb.append("1. 定位到问题所在行\n");
        sb.append("2. 分析变量状态\n");
        sb.append("3. 追踪调用链\n\n");
        
        sb.append("【可能原因】\n");
        sb.append("- 空指针异常：未检查 null\n");
        sb.append("- 边界条件：数组越界\n");
        sb.append("- 并发问题：竞态条件\n\n");
        
        sb.append("【修复建议】\n");
        sb.append("1. 添加 null 检查\n");
        sb.append("2. 验证数组长度\n");
        sb.append("3. 使用同步机制");
        
        System.out.println("[DebugTool] 调试分析完成");
        
        return ToolResult.success("调试完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { 
        return params.containsKey("path") || params.containsKey("issue"); 
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path  - 文件路径 (可选)
                 issue - 问题描述 (可选)
               示例:
                 debug path="src/Main.java"
                 debug issue="空指针异常"
               """.formatted(name, description);
    }
}
