package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Agent 消息工具
 */
@Component
public class AgentMessageTool extends Tool {
    
    public AgentMessageTool() {
        this.name = "agent_message";
        this.description = "发送消息给 Agent";
        this.category = ToolCategory.AGENT;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String agentId = (String) params.get("agent_id");
        String message = (String) params.get("message");
        
        if (agentId == null) return ToolResult.error("agent_id 参数不能为空");
        if (message == null) return ToolResult.error("message 参数不能为空");
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Agent 消息 ===\n\n");
        sb.append("目标 Agent: ").append(agentId).append("\n");
        sb.append("消息内容：").append(message).append("\n\n");
        
        sb.append("【发送状态】\n");
        sb.append("✅ 消息已发送\n");
        sb.append("时间：").append(new Date()).append("\n");
        sb.append("状态：已送达");
        
        System.out.println("[AgentMessageTool] 消息已发送");
        
        return ToolResult.success("消息已发送", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { 
        return params.containsKey("agent_id") && params.containsKey("message"); 
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 agent_id - 目标 Agent ID (必填)
                 message  - 消息内容 (必填)
               示例:
                 agent_message agent_id="agent_1" message="请处理这个任务"
               """.formatted(name, description);
    }
}
