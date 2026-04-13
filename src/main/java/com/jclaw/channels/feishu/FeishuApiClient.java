package com.jclaw.channels.feishu;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * 飞书 API 客户端
 */
@Slf4j
@Service
public class FeishuApiClient {
    
    @Value("${jclaw.channels.feishu.app-id:}")
    private String appId;
    
    @Value("${jclaw.channels.feishu.app-secret:}")
    private String appSecret;
    
    private final WebClient webClient;
    private String accessToken;
    private long tokenExpiresAt;
    
    public FeishuApiClient() {
        this.webClient = WebClient.builder()
            .baseUrl("https://open.feishu.cn/open-apis")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    /**
     * 获取访问令牌
     */
    public String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return accessToken;
        }
        
        try {
            log.info("获取飞书访问令牌...");
            
            FeishuTokenResponse response = webClient.post()
                .uri("/auth/v3/tenant_access_token/internal")
                .bodyValue(Map.of(
                    "app_id", appId,
                    "app_secret", appSecret
                ))
                .retrieve()
                .bodyToMono(FeishuTokenResponse.class)
                .block();
            
            if (response != null && response.getCode() == 0) {
                accessToken = response.getTenantAccessToken();
                tokenExpiresAt = System.currentTimeMillis() + (response.getExpire() - 300) * 1000;
                log.info("飞书令牌获取成功，有效期：{} 秒", response.getExpire());
                return accessToken;
            } else {
                log.error("飞书令牌获取失败：{}", response != null ? response.getMsg() : "未知错误");
                return null;
            }
            
        } catch (Exception e) {
            log.error("获取飞书令牌异常", e);
            return null;
        }
    }
    
    /**
     * 发送文本消息
     */
    public boolean sendTextMessage(String openId, String content, boolean isChatId) {
        String token = getAccessToken();
        if (token == null) {
            return false;
        }
        
        try {
            log.info("发送飞书消息到：{}", openId);
            
            Map<String, Object> body = Map.of(
                "receive_id", openId,
                "content", buildTextContent(content),
                "msg_type", "text"
            );
            
            String endpoint = isChatId ? "/im/v1/messages?receive_id_type=chat_id" 
                                       : "/im/v1/messages?receive_id_type=open_id";
            
            FeishuMessageResponse response = webClient.post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(FeishuMessageResponse.class)
                .block();
            
            if (response != null && response.getCode() == 0) {
                log.info("飞书消息发送成功：{}", response.getData().getMessageId());
                return true;
            } else {
                log.error("飞书消息发送失败：{}", response != null ? response.getMsg() : "未知错误");
                return false;
            }
            
        } catch (Exception e) {
            log.error("发送飞书消息异常", e);
            return false;
        }
    }
    
    /**
     * 构建文本消息内容
     */
    private String buildTextContent(String text) {
        return "{\"text\":\"" + text.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
    }
    
    @Data
    public static class FeishuTokenResponse {
        private Integer code;
        private String msg;
        private String tenantAccessToken;
        private Integer expire;
    }
    
    @Data
    public static class FeishuMessageResponse {
        private Integer code;
        private String msg;
        private MessageData data;
        
        @Data
        public static class MessageData {
            private String messageId;
        }
    }
}
