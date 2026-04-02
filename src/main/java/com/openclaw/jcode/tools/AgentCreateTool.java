package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Agent 创建工具
 */
@Component
public class AgentCreateTool extends Tool {
    
    private static final List<Map<String, String>> AGENTS = new ArrayList<>();
    
    public AgentCreateTool() {
        this.name = "agent_create";
        this.description = "创建新的 Agent";
        this.category = ToolCategory.AGENT;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String name = (String) params.get("name");
        String role = (String) params.get("role");
        String model = (String) params.getOrDefault("model", "default");
        
        if (name == null) return ToolResult.error("name 参数不能为空");
        
        Map<String, String> agent = new HashMap<>();
        agent.put("id", "agent_" + (AGENTS.size() + 1));
        agent.put("name", name);
        agent.put("role", role != null ? role : "assistant");
        agent.put("model", model);
        agent.put("status", "active");
        agent.put("createdAt", new Date().toString());
        
        AGENTS.add(agent);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Agent 创建成功\n\n");
        sb.append("名称：").append(name).append("\n");
        sb.append("ID: ").append(agent.get("id")).append("\n");
        sb.append("角色：").append(agent.get("role")).append("\n");
        sb.append("模型：").append(agent.get("model")).append("\n");
        sb.append("状态：active");
        
        return ToolResult.success("Agent 创建成功", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("name"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 name - Agent 名称 (必填)
                 role - 角色：assistant, reviewer, coder (可选)
                 model - 模型：default, qwen, kimi (可选)
               示例:
                 agent_create name="代码审查员" role="reviewer"
               """.formatted(name, description);
    }
}
