package com.jclaw.controller;

import com.jclaw.services.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * SSE 流式输出控制器
 * 
 * 提供以下端点：
 * - GET /api/stream/connect - 建立 SSE 连接
 * - POST /api/stream/send - 发送消息
 * - GET /api/stream/status - 获取连接状态
 */
@RestController
@RequestMapping("/api/stream")
public class StreamController {
    
    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);
    
    private final SseService sseService;
    
    public StreamController(SseService sseService) {
        this.sseService = sseService;
    }
    
    /**
     * 建立 SSE 连接
     * 
     * @param clientId 客户端 ID（可选，自动生成）
     * @return SSE Emitter
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam(required = false) String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            clientId = "client-" + System.currentTimeMillis();
        }
        
        logger.info("客户端连接：{}", clientId);
        return sseService.createConnection(clientId);
    }
    
    /**
     * 发送消息到指定客户端
     * 
     * @param clientId 客户端 ID
     * @param message 消息内容
     * @return 响应
     */
    @PostMapping("/send")
    public Map<String, Object> send(
            @RequestParam String clientId,
            @RequestBody Map<String, Object> message) {
        
        logger.info("发送消息到客户端：{}", clientId);
        sseService.send(clientId, message);
        
        return Map.of(
            "success", true,
            "clientId", clientId,
            "message", message
        );
    }
    
    /**
     * 流式发送文本
     * 
     * @param clientId 客户端 ID
     * @param content 文本内容
     * @param complete 是否完成
     * @return 响应
     */
    @PostMapping("/stream")
    public Map<String, Object> stream(
            @RequestParam String clientId,
            @RequestParam String content,
            @RequestParam(defaultValue = "false") boolean complete) {
        
        logger.info("流式发送到客户端：{}, complete={}", clientId, complete);
        sseService.streamText(clientId, content, complete);
        
        return Map.of(
            "success", true,
            "clientId", clientId,
            "complete", complete
        );
    }
    
    /**
     * 发送进度更新
     * 
     * @param clientId 客户端 ID
     * @param message 消息
     * @param total 总量
     * @param current 当前进度
     * @return 响应
     */
    @PostMapping("/progress")
    public Map<String, Object> progress(
            @RequestParam String clientId,
            @RequestParam String message,
            @RequestParam int total,
            @RequestParam int current) {
        
        logger.info("发送进度到客户端：{} - {}/{}", clientId, current, total);
        sseService.sendProgress(clientId, message, total, current);
        
        return Map.of(
            "success", true,
            "clientId", clientId,
            "progress", current,
            "total", total
        );
    }
    
    /**
     * 获取连接状态
     * 
     * @return 状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
            "activeConnections", sseService.getActiveConnections(),
            "status", "running"
        );
    }
    
    /**
     * 关闭指定客户端连接
     * 
     * @param clientId 客户端 ID
     * @return 响应
     */
    @PostMapping("/disconnect")
    public Map<String, Object> disconnect(@RequestParam String clientId) {
        logger.info("关闭客户端连接：{}", clientId);
        sseService.closeConnection(clientId);
        
        return Map.of(
            "success", true,
            "clientId", clientId
        );
    }
}
