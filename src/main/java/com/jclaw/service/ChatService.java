package com.jclaw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.dto.ChatRequest;
import com.jclaw.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天服务
 * 对接大模型 API 提供智能对话能力
 */
@Slf4j
@Service
public class ChatService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储对话历史（内存，后续可改为 Redis）
    private final Map<String, List<ChatResponse>> conversationHistory = new ConcurrentHashMap<>();
    
    // 模型配置
    private static final String MODEL_STUDIO_API = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String DEFAULT_MODEL = "qwen-plus";
    
    /**
     * 发送消息并获取 AI 回复
     */
    public ChatResponse sendMessage(ChatRequest request) {
        String userId = request.getUserId();
        String message = request.getMessage();
        String model = request.getModel() != null ? request.getModel() : DEFAULT_MODEL;
        
        log.info("处理聊天请求：userId={}, model={}", userId, model);
        
        try {
            // 调用大模型 API
            String aiReply = callLLM(message, model);
            
            // 创建响应
            ChatResponse response = ChatResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .role("assistant")
                    .content(aiReply)
                    .timestamp(LocalDateTime.now())
                    .model(model)
                    .streaming(false)
                    .build();
            
            // 保存历史
            saveToHistory(userId, response);
            
            return response;
        } catch (Exception e) {
            log.error("调用大模型失败", e);
            // 返回友好错误
            return ChatResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .role("assistant")
                    .content("抱歉，我现在无法回答你的问题。请稍后再试。")
                    .timestamp(LocalDateTime.now())
                    .model(model)
                    .streaming(false)
                    .build();
        }
    }
    
    /**
     * 流式消息
     */
    public void streamMessage(ChatRequest request, SseEmitter emitter) throws IOException {
        String userId = request.getUserId();
        String message = request.getMessage();
        
        log.info("处理流式聊天请求：userId={}", userId);
        
        // 简化实现：先发送一个消息
        ChatResponse response = sendMessage(request);
        emitter.send(response);
    }
    
    /**
     * 调用大模型 API
     */
    private String callLLM(String message, String model) {
        log.info("调用大模型：model={}, message={}", model, message);
        
        try {
            // 构建请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 注意：实际部署时需要从配置读取 API Key
            // headers.set("Authorization", "Bearer " + apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("stream", false);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 实际调用（演示模式，先返回模拟回复）
            // ResponseEntity<String> response = restTemplate.postForEntity(MODEL_STUDIO_API, entity, String.class);
            // return parseResponse(response.getBody());
            
            // 演示模式：根据关键词返回回复
            return generateDemoReply(message);
            
        } catch (Exception e) {
            log.error("调用大模型 API 失败", e);
            throw new RuntimeException("调用大模型失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 演示模式：根据关键词生成回复
     */
    private String generateDemoReply(String message) {
        String msg = message.toLowerCase();
        
        if (msg.contains("你好") || msg.contains("hello")) {
            return "你好！我是 JClaw 智能助手，有什么可以帮你的吗？我可以帮你管理记忆、分析代码、发送消息等。";
        }
        
        if (msg.contains("代码") || msg.contains("追溯")) {
            return "代码追溯功能可以帮你查看代码单元和调用链。你可以在左侧菜单点击代码追溯，然后上传或选择代码文件进行分析。";
        }
        
        if (msg.contains("影响") || msg.contains("分析")) {
            return "影响分析功能可以分析代码变更的影响范围。请选择代码单元后点击分析按钮，系统会自动分析受影响的模块并给出风险评分。";
        }
        
        if (msg.contains("agent") || msg.contains("智能体")) {
            return "Agent 管理页面可以查看和管理所有智能体的状态。当前有 5 个 Agent 在线运行，分别是：PM-QA、Architect、FullStack、DevOps、Analyst。";
        }
        
        if (msg.contains("通道") || msg.contains("消息")) {
            return "通道管理支持飞书、QQBot 等多个平台。你可以在这里发送消息或广播通知。目前已注册 5 个通道。";
        }
        
        if (msg.contains("记忆") || msg.contains("memory")) {
            return "记忆管理功能可以读写 MEMORY.md 和每日日志文件。你可以查看、编辑记忆，也可以萃取知识到长期记忆。";
        }
        
        if (msg.contains("谢谢") || msg.contains("感谢")) {
            return "不客气！有其他问题随时问我。";
        }
        
        if (msg.contains("再见") || msg.contains("bye")) {
            return "再见！祝你工作顺利！";
        }
        
        // 默认回复
        return "我理解了你的问题。\n\n（演示模式：这是一个自动回复。实际部署后会接入真实的大模型 API，如 Qwen、Kimi 等，提供智能对话能力。）";
    }
    
    /**
     * 保存对话历史
     */
    private void saveToHistory(String userId, ChatResponse response) {
        conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(response);
        
        // 限制历史记录数量
        List<ChatResponse> history = conversationHistory.get(userId);
        if (history.size() > 100) {
            history = history.subList(history.size() - 100, history.size());
            conversationHistory.put(userId, history);
        }
    }
    
    /**
     * 获取对话历史
     */
    public List<ChatResponse> getHistory(String userId, int limit) {
        List<ChatResponse> history = conversationHistory.get(userId);
        if (history == null) {
            return new ArrayList<>();
        }
        
        // 返回最近的记录
        int start = Math.max(0, history.size() - limit);
        return new ArrayList<>(history.subList(start, history.size()));
    }
    
    /**
     * 清空对话历史
     */
    public void clearHistory(String userId) {
        conversationHistory.remove(userId);
        log.info("已清空用户 {} 的对话历史", userId);
    }
}
