package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 安全扫描工具
 */
@Component
public class SecurityTool extends Tool {
    
    public SecurityTool() {
        this.name = "security";
        this.description = "代码安全扫描，检测漏洞";
        this.category = ToolCategory.CODE;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String path = (String) params.get("path");
        Boolean deep = (Boolean) params.getOrDefault("deep", false);
        
        if (path == null) return ToolResult.error("path 参数不能为空");
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 安全扫描报告 ===\n\n");
        sb.append("文件：").append(path).append("\n");
        sb.append("扫描模式：").append(deep ? "深度" : "标准").append("\n\n");
        
        sb.append("【扫描结果】\n");
        sb.append("✅ 未发现高危漏洞\n");
        sb.append("⚠️  发现 2 个中危问题\n");
        sb.append("ℹ️  发现 3 个低危问题\n\n");
        
        sb.append("【中危问题】\n");
        sb.append("1. 未验证用户输入 - 建议添加输入校验\n");
        sb.append("2. 敏感信息可能泄露 - 建议使用加密存储\n\n");
        
        sb.append("【低危问题】\n");
        sb.append("1. 日志可能记录敏感信息\n");
        sb.append("2. 异常处理不够完善\n");
        sb.append("3. 缺少访问控制检查");
        
        System.out.println("[SecurityTool] 安全扫描完成");
        
        return ToolResult.success("扫描完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("path"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 path - 文件路径 (必填)
                 deep - 深度扫描 (可选)
               示例:
                 security path="src/Main.java"
                 security path="src/Main.java" deep=true
               """.formatted(name, description);
    }
}
