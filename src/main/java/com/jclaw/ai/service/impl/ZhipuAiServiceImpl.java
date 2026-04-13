package com.jclaw.ai.service.impl;

import com.jclaw.ai.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * 智谱 AI 服务实现
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Slf4j
@Service
public class ZhipuAiServiceImpl implements AiService {
    
    @Value("${jclaw.ai.zhipu.api-key:#{null}}")
    private String apiKey;
    
    @Value("${jclaw.ai.zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String baseUrl;
    
    @Value("${jclaw.ai.model:glm-4-flash}")
    private String model;
    
    private final WebClient webClient;
    
    public ZhipuAiServiceImpl() {
        this.webClient = WebClient.builder()
            .baseUrl("https://open.bigmodel.cn/api/paas/v4")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("智谱 AI API Key 未配置，返回模拟响应");
            return "[模拟响应] 智谱 AI API Key 未配置，请在 application.yml 中配置 jclaw.ai.zhipu.api-key";
        }
        
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            Map<String, Object> respMap = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            if (respMap != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                    return message != null ? message.get("content") : "";
                }
            }
            
            return "";
            
        } catch (Exception e) {
            log.error("智谱 AI 调用失败", e);
            return "[错误] AI 调用失败：" + e.getMessage();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String chatWithContext(List<Message> messages) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "[模拟响应] API Key 未配置";
        }
        
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> apiMessages = new ArrayList<>();
            for (Message msg : messages) {
                Map<String, String> apiMsg = new HashMap<>();
                apiMsg.put("role", msg.getRole());
                apiMsg.put("content", msg.getContent());
                apiMessages.add(apiMsg);
            }
            
            requestBody.put("messages", apiMessages);
            
            Map<String, Object> respMap = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            if (respMap != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                    return message != null ? message.get("content") : "";
                }
            }
            
            return "";
            
        } catch (Exception e) {
            log.error("智谱 AI 上下文对话失败", e);
            return "[错误] AI 调用失败：" + e.getMessage();
        }
    }
    
    @Override
    public void streamChat(String prompt, StreamCallback callback) {
        // TODO: 实现流式调用
        // 目前先同步调用，然后回调
        try {
            String response = chat(prompt);
            // 模拟流式：按字符回调
            for (char c : response.toCharArray()) {
                callback.onToken(String.valueOf(c));
                Thread.sleep(10); // 模拟流式延迟
            }
            callback.onComplete();
        } catch (Exception e) {
            callback.onError(e);
        }
    }
}
