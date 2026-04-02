package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Agent 列表工具
 */
@Component
public class AgentListTool extends Tool {
    
    private static final List<Map<String, String>> AGENTS = new ArrayList<>();
    
    public AgentListTool() {
        this.name = "agent_list";
        this.description = "列出所有 Agent";
        this.category = ToolCategory.AGENT;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String status = (String) params.get("status");
        
        StringBuilder sb = new StringBuilder();
        sb.append("Agent 列表:\n\n");
        
        for (Map<String, String> agent : AGENTS) {
            if (status == null || status.equals(agent.get("status"))) {
                sb.append(String.format("- %s [%s]\n  角色：%s | 模型：%s\n  状态：%s\n\n", 
                    agent.get("name"), agent.get("id"), 
                    agent.get("role"), agent.get("model"), agent.get("status")));
            }
        }
        
        if (sb.length() == 0) {
            sb.append("暂无 Agent");
        }
        
        return ToolResult.success("列表完成", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return true; }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 status - 按状态过滤：active, inactive (可选)
               示例:
                 agent_list
                 agent_list status="active"
               """.formatted(name, description);
    }
}
