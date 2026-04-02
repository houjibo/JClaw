package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 消息发送工具
 */
@Component
public class SendMessageTool extends Tool {
    
    private static final List<Map<String, String>> MESSAGES = new ArrayList<>();
    
    public SendMessageTool() {
        this.name = "send_message";
        this.description = "发送消息给用户或其他 Agent";
        this.category = ToolCategory.COMM;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String to = (String) params.get("to");
        String message = (String) params.get("message");
        String type = (String) params.getOrDefault("type", "text");
        
        if (message == null) return ToolResult.error("message 参数不能为空");
        
        Map<String, String> msg = new HashMap<>();
        msg.put("to", to != null ? to : "user");
        msg.put("message", message);
        msg.put("type", type);
        msg.put("timestamp", new Date().toString());
        
        MESSAGES.add(msg);
        
        return ToolResult.success("消息已发送", 
            String.format("发送给：%s\n内容：%s", msg.get("to"), message));
    }
    
    @Override
    public boolean validate(Map<String, Object> params) { return params.containsKey("message"); }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 to      - 接收者 (可选，默认 user)
                 message - 消息内容 (必填)
                 type    - 消息类型：text, markdown (可选)
               示例:
                 send_message message="任务完成"
                 send_message to="agent_1" message="请处理"
               """.formatted(name, description);
    }
}
