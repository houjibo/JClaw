package com.jclaw.controller;

import com.jclaw.channels.ChannelManager;
import com.jclaw.channels.ChannelMessage;
import com.jclaw.channels.impl.FeishuChannel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息通道 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelManager channelManager;
    
    /**
     * 列出所有通道
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listChannels() {
        List<Map<String, Object>> channels = new java.util.ArrayList<>();
        for (var channel : channelManager.listChannels()) {
            Map<String, Object> info = new java.util.HashMap<>();
            info.put("name", channel.getName());
            info.put("enabled", channel.isEnabled());
            channels.add(info);
        }
        return ResponseEntity.ok(channels);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody SendRequest request) {
        log.info("发送消息到 {}: {}", request.getChannel(), request.getReceiverId());
        
        boolean success = channelManager.send(
            request.getChannel(),
            request.getReceiverId(),
            request.getContent()
        );
        
        if (success) {
            return ResponseEntity.ok(Map.of("status", "success"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "failed"));
        }
    }
    
    /**
     * 飞书回调（接收消息）
     */
    @PostMapping("/feishu/callback")
    public ResponseEntity<Void> feishuCallback(@RequestBody FeishuCallbackRequest request) {
        log.info("收到飞书回调：{}", request);
        
        FeishuChannel feishuChannel = channelManager.getFeishuChannel();
        if (feishuChannel != null && feishuChannel.isEnabled()) {
            ChannelMessage message = new ChannelMessage();
            message.setMessageId(request.getOpen_id());
            message.setSenderId(request.getOpen_id());
            message.setContent(request.getText().getContent());
            message.setMessageType("text");
            message.setTimestamp(System.currentTimeMillis() / 1000);
            
            feishuChannel.onMessage(message);
        }
        
        return ResponseEntity.ok().build();
    }
    
    @Data
    public static class SendRequest {
        private String channel;
        private String receiverId;
        private String content;
        private String messageType;
    }
    
    @Data
    public static class FeishuCallbackRequest {
        private String open_id;
        private TextContent text;
        
        @Data
        public static class TextContent {
            private String content;
        }
    }
}
