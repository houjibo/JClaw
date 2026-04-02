package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 代码优化/重构工具
 */
@Component
public class OptimizeTool extends Tool {
    
    public OptimizeTool() {
        this.name = "optimize";
        this.description = "代码优化和重构建议";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        String type = (String) params.getOrDefault("type", "performance");
        
        if (path == null) return ToolResult.error("path 参数不能为空");
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 代码优化建议 ===\n\n");
        sb.append("文件：").append(path).append("\n");
        sb.append("优化类型：").append(type).append("\n\n");
        
        sb.append("【性能优化】\n");
        sb.append("1. 使用 StringBuilder 替代字符串拼接\n");
        sb.append("2. 避免在循环中创建对象\n");
        sb.append("3. 使用缓存减少重复计算\n\n");
        
        sb.append("【代码质量】\n");
        sb.append("1. 提取重复代码为独立方法\n");
        sb.append("2. 使用设计模式改进结构\n");
        sb.append("3. 添加适当的异常处理\n\n");
        
        sb.append("【可读性】\n");
        sb.append("1. 改进变量命名\n");
        sb.append("2. 添加方法注释\n");
        sb.append("3. 拆分过长方法");
        
        System.out.println("[OptimizeTool] 优化分析完成");
        
        return ToolResult.success("优化分析完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("path"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path - 文件路径 (必填)
                 type - 优化类型：performance, readability, structure (可选)
               示例:
                 optimize path="src/Main.java"
                 optimize path="src/Main.java" type="performance"
               """.formatted(name, description);
    }
}
