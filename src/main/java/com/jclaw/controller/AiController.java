package com.jclaw.controller;

import com.jclaw.ai.service.AiService;
import com.jclaw.ai.service.AiService.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * AI REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    
    private final AiService aiService;
    
    /**
     * 简单对话
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        log.info("AI 对话：{}", request.getPrompt());
        
        String response = aiService.chat(request.getPrompt());
        
        return ResponseEntity.ok(Map.of(
            "response", response,
            "model", "glm-4-flash"
        ));
    }
    
    /**
     * 流式对话
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request) {
        log.info("AI 流式对话：{}", request.getPrompt());
        
        return Flux.interval(Duration.ofMillis(50))
            .take(100)
            .map(seq -> {
                String chunk = "这是模拟流式输出 - " + seq;
                return ServerSentEvent.builder(chunk).build();
            });
    }
    
    /**
     * 上下文对话
     */
    @PostMapping("/chat/context")
    public ResponseEntity<Map<String, String>> chatWithContext(@RequestBody ContextChatRequest request) {
        log.info("AI 上下文对话：{} 条消息", request.getMessages().size());
        
        List<Message> messages = request.getMessages().stream()
            .map(m -> new Message(m.getRole(), m.getContent()))
            .toList();
        
        String response = aiService.chatWithContext(messages);
        
        return ResponseEntity.ok(Map.of(
            "response", response,
            "model", "glm-4-flash"
        ));
    }
    
    @Data
    public static class ChatRequest {
        private String prompt;
    }
    
    @Data
    public static class ContextChatRequest {
        private List<MessageDTO> messages;
    }
    
    @Data
    public static class MessageDTO {
        private String role;
        private String content;
    }
}
