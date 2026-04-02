package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 工具搜索工具
 */
@Component
public class ToolSearchTool extends Tool {
    
    private final ToolRegistry toolRegistry;
    
    public ToolSearchTool(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
        this.name = "tool_search";
        this.description = "搜索可用的工具";
        this.category = ToolCategory.SEARCH;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String query = (String) params.get("query");
        String category = (String) params.get("category");
        
        List<Tool> allTools = toolRegistry.listTools();
        List<Tool> results = new ArrayList<>();
        
        for (Tool tool : allTools) {
            boolean matches = true;
            
            if (query != null && !query.isBlank()) {
                String q = query.toLowerCase();
                boolean nameMatch = tool.getName().toLowerCase().contains(q);
                boolean descMatch = tool.getDescription().toLowerCase().contains(q);
                if (!nameMatch && !descMatch) matches = false;
            }
            
            if (category != null && !category.isBlank()) {
                if (!tool.getCategory().name().equalsIgnoreCase(category)) matches = false;
            }
            
            if (matches) results.add(tool);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("找到 ").append(results.size()).append(" 个工具:\n\n");
        
        for (Tool tool : results) {
            sb.append(String.format("- %s [%s]\n  %s\n\n", 
                tool.getName(), tool.getCategory(), tool.getDescription()));
        }
        
        return ToolResult.success("搜索完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return true; }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 query    - 搜索关键词 (可选)
                 category - 按分类过滤 (可选)
               示例:
                 tool_search query="file"
                 tool_search category="TASK"
               """.formatted(name, description);
    }
}
