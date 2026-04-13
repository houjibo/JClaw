package com.jclaw.channels.feishu;

import com.jclaw.channels.ChannelManager;
import com.jclaw.channels.ChannelMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 飞书回调控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/channels/feishu")
@RequiredArgsConstructor
public class FeishuCallbackController {
    
    private final ChannelManager channelManager;
    
    /**
     * 飞书消息回调
     */
    @PostMapping("/callback")
    public ResponseEntity<FeishuCallbackResponse> onMessage(@RequestBody FeishuCallbackRequest request) {
        log.info("收到飞书回调：header_type={}, challenge={}", 
            request.getHeader() != null ? request.getHeader().getEventType() : "null",
            request.getChallenge());
        
        // 处理 URL 验证
        if (request.getChallenge() != null) {
            log.info("飞书 URL 验证，返回 challenge");
            FeishuCallbackResponse response = new FeishuCallbackResponse();
            response.setChallenge(request.getChallenge());
            return ResponseEntity.ok(response);
        }
        
        // 处理消息
        if (request.getHeader() != null && "im.message".equals(request.getHeader().getEventType())) {
            log.info("收到飞书消息：{}", request);
            
            ChannelMessage message = new ChannelMessage();
            message.setMessageId(request.getEvent().getMessageId());
            message.setSenderId(request.getEvent().getSenderId().getOpenId());
            message.setReceiverId(request.getEvent().getChatId());
            message.setContent(extractContent(request.getEvent().getMessage()));
            message.setMessageType("text");
            message.setTimestamp(request.getEvent().getCreateTime() / 1000000); // 纳秒转毫秒
            message.setGroup(true);
            
            // 交给飞书通道处理
            var feishuChannel = channelManager.getFeishuChannel();
            if (feishuChannel != null) {
                feishuChannel.onMessage(message);
            }
        }
        
        return ResponseEntity.ok(new FeishuCallbackResponse());
    }
    
    /**
     * 提取消息内容
     */
    private String extractContent(Object messageObj) {
        if (messageObj instanceof java.util.Map) {
            java.util.Map<?, ?> message = (java.util.Map<?, ?>) messageObj;
            Object content = message.get("content");
            if (content instanceof String) {
                try {
                    com.fasterxml.jackson.databind.JsonNode json = 
                        new com.fasterxml.jackson.databind.ObjectMapper().readTree((String) content);
                    return json.path("text").asText();
                } catch (Exception e) {
                    log.warn("解析消息内容失败", e);
                    return content.toString();
                }
            }
        }
        return "";
    }
    
    @Data
    public static class FeishuCallbackRequest {
        private String challenge;
        private Header header;
        private Event event;
        
        @Data
        public static class Header {
            private String eventType;
            private String appId;
        }
        
        @Data
        public static class Event {
            private String messageId;
            private String chatId;
            private Long createTime;
            private SenderId senderId;
            private Object message;
            
            @Data
            public static class SenderId {
                private String openId;
                private String userId;
            }
        }
    }
    
    @Data
    public static class FeishuCallbackResponse {
        private String challenge;
    }
}
