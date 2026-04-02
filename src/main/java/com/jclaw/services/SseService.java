package com.jclaw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * SSE 流式输出服务
 * 
 * 功能：
 * - 创建 SSE 连接
 * - 发送流式数据
 * - 连接管理
 * - 断线重连支持
 */
@Service
public class SseService {
    
    private static final Logger logger = LoggerFactory.getLogger(SseService.class);
    
    /**
     * SSE 连接超时时间（5 分钟）
     */
    private static final Long DEFAULT_TIMEOUT = 5 * 60 * 1000L;
    
    /**
     * 存储活跃的 SSE 连接
     */
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    /**
     * 心跳线程池
     */
    private final ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(2);
    
    /**
     * 创建 SSE 连接
     * 
     * @param clientId 客户端 ID
     * @return SSE Emitter
     */
    public SseEmitter createConnection(String clientId) {
        logger.info("创建 SSE 连接：{}", clientId);
        
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(clientId, emitter);
        
        // 设置完成回调
        emitter.onCompletion(() -> {
            logger.info("SSE 连接完成：{}", clientId);
            emitters.remove(clientId);
        });
        
        // 设置超时回调
        emitter.onTimeout(() -> {
            logger.warn("SSE 连接超时：{}", clientId);
            emitters.remove(clientId);
            try {
                emitter.send(SseEmitter.event()
                    .name("error")
                    .data("Connection timeout"));
            } catch (IOException e) {
                logger.error("发送超时消息失败", e);
            }
        });
        
        // 设置错误回调
        emitter.onError(throwable -> {
            logger.error("SSE 连接错误：{}", clientId, throwable);
            emitters.remove(clientId);
        });
        
        // 启动心跳
        startHeartbeat(clientId, emitter);
        
        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data(Map.of(
                    "clientId", clientId,
                    "timestamp", System.currentTimeMillis()
                )));
        } catch (IOException e) {
            logger.error("发送连接消息失败", e);
        }
        
        return emitter;
    }
    
    /**
     * 发送流式数据
     * 
     * @param clientId 客户端 ID
     * @param data 数据
     */
    public void send(String clientId, Object data) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(data));
            } catch (IOException e) {
                logger.error("发送消息失败：{}", clientId, e);
                emitters.remove(clientId);
            }
        } else {
            logger.warn("未找到 SSE 连接：{}", clientId);
        }
    }
    
    /**
     * 发送流式文本（增量输出）
     * 
     * @param clientId 客户端 ID
     * @param text 文本内容
     * @param isComplete 是否完成
     */
    public void streamText(String clientId, String text, boolean isComplete) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("stream")
                    .data(Map.of(
                        "content", text,
                        "complete", isComplete
                    )));
                
                if (isComplete) {
                    emitter.complete();
                    emitters.remove(clientId);
                }
            } catch (IOException e) {
                logger.error("流式发送失败：{}", clientId, e);
                emitters.remove(clientId);
            }
        }
    }
    
    /**
     * 发送进度更新
     * 
     * @param clientId 客户端 ID
     * @param message 消息
     * @param total 总量
     * @param current 当前进度
     */
    public void sendProgress(String clientId, String message, int total, int current) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("progress")
                    .data(Map.of(
                        "message", message,
                        "total", total,
                        "current", current,
                        "percent", (current * 100) / total
                    )));
            } catch (IOException e) {
                logger.error("发送进度失败：{}", clientId, e);
            }
        }
    }
    
    /**
     * 发送错误消息
     * 
     * @param clientId 客户端 ID
     * @param error 错误信息
     */
    public void sendError(String clientId, String error) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("error")
                    .data(Map.of(
                        "error", error,
                        "timestamp", System.currentTimeMillis()
                    )));
                emitter.complete();
                emitters.remove(clientId);
            } catch (IOException e) {
                logger.error("发送错误失败：{}", clientId, e);
            }
        }
    }
    
    /**
     * 关闭连接
     * 
     * @param clientId 客户端 ID
     */
    public void closeConnection(String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            emitter.complete();
            logger.info("关闭 SSE 连接：{}", clientId);
        }
    }
    
    /**
     * 获取活跃连接数
     */
    public int getActiveConnections() {
        return emitters.size();
    }
    
    /**
     * 启动心跳
     */
    private void startHeartbeat(String clientId, SseEmitter emitter) {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("heartbeat")
                    .data(System.currentTimeMillis()));
            } catch (IOException e) {
                logger.warn("心跳发送失败：{}", clientId, e);
                // 心跳失败，关闭连接
                closeConnection(clientId);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    /**
     * 关闭服务
     */
    public void shutdown() {
        // 关闭所有连接
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                logger.error("关闭连接失败：{}", clientId, e);
            }
        });
        emitters.clear();
        
        // 关闭心跳线程池
        heartbeatExecutor.shutdown();
        try {
            if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            heartbeatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("SSE 服务已关闭");
    }
}
