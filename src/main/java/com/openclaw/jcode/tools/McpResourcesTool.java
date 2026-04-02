package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * MCP 资源列表工具
 */
@Component
public class McpResourcesTool extends Tool {
    
    private static final List<Map<String, String>> RESOURCES = Arrays.asList(
        Map.of("uri", "file://workspace", "name", "工作区文件", "type", "filesystem"),
        Map.of("uri", "git://repo", "name", "Git 仓库", "type", "git"),
        Map.of("uri", "http://api", "name", "API 端点", "type", "http")
    );
    
    public McpResourcesTool() {
        this.name = "mcp_resources";
        this.description = "列出可用的 MCP 资源";
        this.category = ToolCategory.MCP;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String type = (String) params.get("type");
        
        StringBuilder sb = new StringBuilder();
        sb.append("MCP 资源列表:\n\n");
        
        for (Map<String, String> resource : RESOURCES) {
            if (type == null || type.equals(resource.get("type"))) {
                sb.append(String.format("- %s (%s)\n  URI: %s\n\n", 
                    resource.get("name"), resource.get("type"), resource.get("uri")));
            }
        }
        
        return ToolResult.success("找到 " + RESOURCES.size() + " 个资源", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return true; }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 type - 按类型过滤：filesystem, git, http (可选)
               示例:
                 mcp_resources
                 mcp_resources type="filesystem"
               """.formatted(name, description);
    }
}
