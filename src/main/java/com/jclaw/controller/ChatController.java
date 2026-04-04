package com.jclaw.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.dto.ChatRequest;
import com.jclaw.dto.ChatResponse;
import com.jclaw.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天交互控制器
 * 提供 AI 对话能力
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 发送消息并获取 AI 回复
     */
    @PostMapping("/message")
    public Result<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        log.info("收到聊天请求：userId={}, message={}", request.getUserId(), request.getMessage());
        
        try {
            ChatResponse response = chatService.sendMessage(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("聊天失败", e);
            return Result.error("AI 回复失败：" + e.getMessage());
        }
    }

    /**
     * 流式对话（SSE）
     */
    @PostMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter streamMessage(@RequestBody ChatRequest request) {
        log.info("收到流式聊天请求：userId={}", request.getUserId());
        
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L); // 5 分钟超时
        
        CompletableFuture.runAsync(() -> {
            try {
                chatService.streamMessage(request, emitter);
                emitter.complete();
            } catch (IOException e) {
                log.error("流式消息发送失败", e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history")
    public Result<List<ChatResponse>> getHistory(
            @RequestParam String userId,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取聊天历史：userId={}, limit={}", userId, limit);
        
        try {
            List<ChatResponse> history = chatService.getHistory(userId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取历史失败", e);
            return Result.error("获取历史失败：" + e.getMessage());
        }
    }

    /**
     * 清空对话历史
     */
    @DeleteMapping("/history")
    public Result<Void> clearHistory(@RequestParam String userId) {
        log.info("清空聊天历史：userId={}", userId);
        
        try {
            chatService.clearHistory(userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("清空历史失败", e);
            return Result.error("清空历史失败：" + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Chat API is running");
    }
}
