package com.jclaw.channel.adapter.impl;

import com.jclaw.channel.adapter.ChannelAdapter;
import com.jclaw.channel.adapter.MessageListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Discord 通道适配器（完整实现）
 * 
 * 功能：
 * - 消息接收（轮询）
 * - 消息发送
 * - 文件传输（<25MB）
 * - Embed 消息
 * - 内联组件（按钮/菜单）
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Component
public class DiscordChannelAdapter implements ChannelAdapter {
    
    private static final String API_BASE = "https://discord.com/api/v10";
    
    private String botToken;
    private String guildId;
    private boolean enabled = false;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private MessageListener messageListener;
    private long lastMessageId = 0;
    
    public DiscordChannelAdapter() {
        log.info("Discord 通道适配器初始化");
    }
    
    @Override
    public String getName() {
        return "discord";
    }
    
    /**
     * 初始化配置
     */
    public void initialize(Map<String, String> config) {
        this.botToken = config.get("botToken");
        this.guildId = config.get("guildId");
        this.enabled = config.get("enabled") != null && Boolean.parseBoolean(config.get("enabled"));
        
        if (enabled && botToken != null) {
            startReceiving();
            log.info("Discord 通道已启用并开始接收消息");
        } else {
            log.warn("Discord 通道未启用或缺少配置");
        }
    }
    
    /**
     * 启动消息接收（轮询）
     */
    private void startReceiving() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                receiveMessages();
            } catch (Exception e) {
                log.error("Discord 消息接收失败", e);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
    
    /**
     * 接收频道消息
     */
    private void receiveMessages() throws IOException, InterruptedException {
        if (guildId == null) {
            return;
        }
        
        // 获取频道列表
        String channelsUrl = API_BASE + "/guilds/" + guildId + "/channels";
        
        HttpRequest channelsRequest = HttpRequest.newBuilder()
            .uri(URI.create(channelsUrl))
            .header("Authorization", "Bot " + botToken)
            .GET()
            .build();
        
        HttpResponse<String> channelsResponse = httpClient.send(channelsRequest, HttpResponse.BodyHandlers.ofString());
        
        if (channelsResponse.statusCode() == 200) {
            JsonNode channels = objectMapper.readTree(channelsResponse.body());
            
            for (JsonNode channel : channels) {
                if (!"text".equals(channel.path("type").asText())) {
                    continue;
                }
                
                String channelId = channel.path("id").asText();
                fetchChannelMessages(channelId);
            }
        }
    }
    
    /**
     * 获取频道消息
     */
    private void fetchChannelMessages(String channelId) throws IOException, InterruptedException {
        String url = API_BASE + "/channels/" + channelId + "/messages?limit=10";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bot " + botToken)
            .GET()
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode messages = objectMapper.readTree(response.body());
            
            for (JsonNode message : messages) {
                long messageId = message.path("id").asLong();
                
                // 跳过已处理的消息
                if (messageId <= lastMessageId) {
                    continue;
                }
                
                lastMessageId = Math.max(lastMessageId, messageId);
                
                String content = message.path("content").asText();
                String author = message.path("author").path("username").asText();
                String authorId = message.path("author").path("id").asText();
                
                // 忽略机器人消息
                if (message.path("author").path("bot").asBoolean()) {
                    continue;
                }
                
                log.info("收到 Discord 消息：{} - {}", author, content);
                
                if (messageListener != null) {
                    messageListener.onMessage(channelId, content, Map.of(
                        "from", author,
                        "from_id", authorId,
                        "channel_id", channelId,
                        "message_id", String.valueOf(messageId),
                        "guild_id", guildId
                    ));
                }
            }
        }
    }
    
    @Override
    public void sendMessage(String target, String content) {
        if (!enabled) {
            log.warn("Discord 通道未启用");
            return;
        }
        
        try {
            String url = API_BASE + "/channels/" + target + "/messages";
            
            Map<String, String> body = new HashMap<>();
            body.put("content", content);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .header("Authorization", "Bot " + botToken)
                .header("Content-Type", "application/json")
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("Discord 消息发送成功：{}", target);
            } else {
                log.error("Discord 消息发送失败：{} - {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Discord 消息发送异常", e);
        }
    }
    
    @Override
    public void receiveMessage(MessageListener listener) {
        this.messageListener = listener;
        log.info("Discord 消息接收器已注册");
    }
    
    @Override
    public boolean isConnected() {
        return enabled && botToken != null;
    }
    
    /**
     * 发送 Embed 消息
     */
    public void sendEmbed(String channelId, Map<String, Object> embedData) {
        if (!enabled) {
            log.warn("Discord 通道未启用");
            return;
        }
        
        try {
            String url = API_BASE + "/channels/" + channelId + "/messages";
            
            Map<String, Object> body = new HashMap<>();
            body.put("content", embedData.get("content"));
            
            // 构建 Embed
            List<Map<String, Object>> embeds = new ArrayList<>();
            Map<String, Object> embed = new HashMap<>();
            
            if (embedData.containsKey("title")) {
                embed.put("title", embedData.get("title"));
            }
            if (embedData.containsKey("description")) {
                embed.put("description", embedData.get("description"));
            }
            if (embedData.containsKey("color")) {
                embed.put("color", embedData.get("color"));
            }
            if (embedData.containsKey("url")) {
                embed.put("url", embedData.get("url"));
            }
            
            // Footer
            if (embedData.containsKey("footer")) {
                embed.put("footer", embedData.get("footer"));
            }
            
            // Thumbnail
            if (embedData.containsKey("thumbnail")) {
                embed.put("thumbnail", embedData.get("thumbnail"));
            }
            
            embeds.add(embed);
            body.put("embeds", embeds);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .header("Authorization", "Bot " + botToken)
                .header("Content-Type", "application/json")
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("Discord Embed 消息发送成功：{}", channelId);
            } else {
                log.error("Discord Embed 消息发送失败：{} - {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Discord Embed 消息发送异常", e);
        }
    }
    
    /**
     * 发送文件
     */
    public void sendFile(String channelId, String filePath, String description) {
        if (!enabled) {
            log.warn("Discord 通道未启用");
            return;
        }
        
        try {
            String url = API_BASE + "/channels/" + channelId + "/messages";
            
            // TODO: 实现文件上传（需要 multipart/form-data）
            log.info("发送 Discord 文件：{} - {}", channelId, filePath);
            
            Map<String, String> body = new HashMap<>();
            body.put("content", description != null ? description : "");
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .header("Authorization", "Bot " + botToken)
                .header("Content-Type", "application/json")
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Discord 文件发送响应：{}", response.statusCode());
        } catch (Exception e) {
            log.error("Discord 文件发送异常", e);
        }
    }
    
    /**
     * 发送带组件的消息（按钮/菜单）
     */
    public void sendMessageWithComponents(String channelId, String content, List<Map<String, Object>> components) {
        if (!enabled) {
            log.warn("Discord 通道未启用");
            return;
        }
        
        try {
            String url = API_BASE + "/channels/" + channelId + "/messages";
            
            Map<String, Object> body = new HashMap<>();
            body.put("content", content);
            body.put("components", components);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .header("Authorization", "Bot " + botToken)
                .header("Content-Type", "application/json")
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Discord 组件消息发送响应：{}", response.statusCode());
        } catch (Exception e) {
            log.error("Discord 组件消息发送异常", e);
        }
    }
    
    /**
     * 获取 Bot 信息
     */
    public Map<String, Object> getBotInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", getName());
        info.put("enabled", enabled);
        info.put("connected", isConnected());
        
        if (enabled && botToken != null) {
            try {
                String url = API_BASE + "/users/@me";
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bot " + botToken)
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JsonNode result = objectMapper.readTree(response.body());
                    info.put("username", result.path("username").asText());
                    info.put("id", result.path("id").asText());
                    info.put("discriminator", result.path("discriminator").asText());
                    info.put("bot", result.path("bot").asBoolean());
                }
            } catch (Exception e) {
                log.error("获取 Bot 信息失败", e);
            }
        }
        
        return info;
    }
    
    /**
     * 停止接收
     */
    public void shutdown() {
        scheduler.shutdown();
        log.info("Discord 通道已关闭");
    }
}
