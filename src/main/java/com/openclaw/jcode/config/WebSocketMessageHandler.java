package com.openclaw.jcode.config;

import com.openclaw.jcode.core.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import java.util.Map;

/**
 * WebSocket 消息处理器
 */
@Controller
public class WebSocketMessageHandler {
    
    private final ToolRegistry toolRegistry;
    private final SimpMessageSendingOperations messagingTemplate;
    
    public WebSocketMessageHandler(ToolRegistry toolRegistry, SimpMessageSendingOperations messagingTemplate) {
        this.toolRegistry = toolRegistry;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * 处理工具执行请求
     */
    @MessageMapping("/tool.execute")
    @SendTo("/topic/response")
    public Map<String, Object> handleToolExecute(@Payload Map<String, Object> payload) {
        String toolName = (String) payload.get("toolName");
        Map<String, Object> params = (Map<String, Object>) payload.get("params");
        String sessionId = (String) payload.get("sessionId");
        
        System.out.println("[WebSocket] 收到工具执行请求：" + toolName);
        
        ToolContext context = ToolContext.defaultContext();
        if (sessionId != null) {
            context.setSessionId(sessionId);
        }
        
        ToolResult result = toolRegistry.execute(toolName, params != null ? params : Map.of(), context);
        
        Map<String, Object> response = Map.of(
                "toolName", toolName,
                "success", result.isSuccess(),
                "message", result.getMessage(),
                "output", result.getOutput(),
                "error", result.getError(),
                "durationMs", result.getDurationMs(),
                "timestamp", System.currentTimeMillis()
        );
        
        return response;
    }
    
    /**
     * 广播消息
     */
    public void broadcast(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
