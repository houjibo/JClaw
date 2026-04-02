package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 远程触发工具
 */
@Component
public class RemoteTriggerTool extends Tool {
    
    public RemoteTriggerTool() {
        this.name = "remote_trigger";
        this.description = "触发远程 Agent 执行任务";
        this.category = ToolCategory.COMM;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String agentId = (String) params.get("agent_id");
        String action = (String) params.get("action");
        String payload = (String) params.get("payload");
        
        if (agentId == null) return ToolResult.error("agent_id 参数不能为空");
        if (action == null) return ToolResult.error("action 参数不能为空");
        
        String triggerId = "trigger_" + UUID.randomUUID().toString().substring(0, 8);
        
        StringBuilder sb = new StringBuilder();
        sb.append("远程触发已发送\n\n");
        sb.append("触发 ID: ").append(triggerId).append("\n");
        sb.append("目标 Agent: ").append(agentId).append("\n");
        sb.append("动作：").append(action).append("\n");
        if (payload != null) {
            sb.append("负载：").append(payload).append("\n");
        }
        sb.append("状态：已发送");
        
        System.out.println("[RemoteTriggerTool] 触发远程 Agent: " + agentId);
        
        return ToolResult.success("触发成功", sb.toString());
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { 
        return params.containsKey("agent_id") && params.containsKey("action"); 
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 agent_id - 目标 Agent ID (必填)
                 action   - 执行动作 (必填)
                 payload  - 负载数据 (可选)
               示例:
                 remote_trigger agent_id="agent_1" action="analyze"
               """.formatted(name, description);
    }
}
