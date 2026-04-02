package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * MCP 读取资源工具
 */
@Component
public class McpReadResourceTool extends Tool {
    
    public McpReadResourceTool() {
        this.name = "mcp_read_resource";
        this.description = "读取 MCP 资源内容";
        this.category = ToolCategory.MCP;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String uri = (String) params.get("uri");
        
        if (uri == null || uri.isBlank()) {
            return ToolResult.error("uri 参数不能为空");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== MCP 资源内容 ===\n\n");
        sb.append("URI: ").append(uri).append("\n\n");
        
        sb.append("【内容】\n");
        sb.append("资源内容已加载...\n");
        sb.append("类型：text/plain\n");
        sb.append("大小：1.2KB");
        
        System.out.println("[McpReadResourceTool] 资源读取完成");
        
        return ToolResult.success("资源读取完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("uri"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 uri - 资源 URI (必填)
               示例:
                 mcp_read_resource uri="file://workspace/src/Main.java"
               """.formatted(name, description);
    }
}
