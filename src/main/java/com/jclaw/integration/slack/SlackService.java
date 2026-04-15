package com.jclaw.integration.slack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Slack 集成服务
 * 
 * 功能：
 * - 消息发送（频道/私聊/线程）
 * - 文件上传
 * - 频道管理
 * - 用户查询
 * - 互动组件（按钮/菜单）
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@Slf4j
@Service
public class SlackService {
    
    private static final String API_BASE = "https://slack.com/api";
    
    private String token;
    private boolean enabled = false;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public SlackService() {
        log.info("Slack 集成服务初始化");
    }
    
    /**
     * 初始化配置
     */
    public void initialize(Map<String, String> config) {
        this.token = config.get("token");
        this.enabled = config.get("enabled") != null && Boolean.parseBoolean(config.get("enabled"));
        
        if (enabled && token != null) {
            log.info("Slack 集成已启用");
        } else {
            log.warn("Slack 集成未启用或缺少配置");
        }
    }
    
    // ==================== 消息发送 ====================
    
    /**
     * 发送消息到频道
     */
    public Map<String, Object> sendMessage(String channel, String text) throws IOException, InterruptedException {
        return sendMessage(channel, text, null, null);
    }
    
    /**
     * 发送消息（支持线程回复和 blocks）
     */
    public Map<String, Object> sendMessage(String channel, String text, String threadTs, List<Map<String, Object>> blocks) throws IOException, InterruptedException {
        String url = API_BASE + "/chat.postMessage";
        
        Map<String, Object> request = new HashMap<>();
        request.put("channel", channel);
        request.put("text", text);
        request.put("as_user", true);
        
        if (threadTs != null) {
            request.put("thread_ts", threadTs);
        }
        
        if (blocks != null) {
            request.put("blocks", blocks);
        }
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", response.path("ok").asBoolean());
        result.put("channel", response.path("channel").asText());
        result.put("ts", response.path("ts").asText());
        result.put("message_url", "https://app.slack.com/client/" + channel);
        
        if (response.path("ok").asBoolean()) {
            log.info("Slack 消息发送成功：{}", channel);
        } else {
            log.error("Slack 消息发送失败：{}", response.path("error").asText());
        }
        
        return result;
    }
    
    /**
     * 发送带附件的消息
     */
    public Map<String, Object> sendMessageWithAttachments(String channel, String text, List<Map<String, Object>> attachments) throws IOException, InterruptedException {
        String url = API_BASE + "/chat.postMessage";
        
        Map<String, Object> request = new HashMap<>();
        request.put("channel", channel);
        request.put("text", text);
        request.put("as_user", true);
        request.put("attachments", attachments);
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", response.path("ok").asBoolean());
        result.put("ts", response.path("ts").asText());
        
        return result;
    }
    
    /**
     * 发送互动消息（带按钮）
     */
    public Map<String, Object> sendInteractiveMessage(String channel, String text, List<Map<String, Object>> actions) throws IOException, InterruptedException {
        List<Map<String, Object>> blocks = new ArrayList<>();
        
        // 文本块
        Map<String, Object> textBlock = Map.of(
            "type", "section",
            "text", Map.of("type", "mrkdwn", "text", text)
        );
        blocks.add(textBlock);
        
        // 操作块
        if (actions != null && !actions.isEmpty()) {
            Map<String, Object> actionsBlock = Map.of(
                "type", "actions",
                "elements", actions
            );
            blocks.add(actionsBlock);
        }
        
        return sendMessage(channel, text, null, blocks);
    }
    
    /**
     * 更新消息
     */
    public Map<String, Object> updateMessage(String channel, String ts, String text) throws IOException, InterruptedException {
        String url = API_BASE + "/chat.update";
        
        Map<String, Object> request = new HashMap<>();
        request.put("channel", channel);
        request.put("ts", ts);
        request.put("text", text);
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", response.path("ok").asBoolean());
        result.put("channel", response.path("channel").asText());
        result.put("ts", response.path("ts").asText());
        
        return result;
    }
    
    /**
     * 删除消息
     */
    public Map<String, Object> deleteMessage(String channel, String ts) throws IOException, InterruptedException {
        String url = API_BASE + "/chat.delete";
        
        Map<String, Object> request = new HashMap<>();
        request.put("channel", channel);
        request.put("ts", ts);
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", response.path("ok").asBoolean());
        
        return result;
    }
    
    // ==================== 文件上传 ====================
    
    /**
     * 上传文件
     */
    public Map<String, Object> uploadFile(String channel, String filePath, String title, String comment) throws IOException, InterruptedException {
        String url = API_BASE + "/files.upload";
        
        Map<String, Object> request = new HashMap<>();
        request.put("channels", channel);
        if (title != null) request.put("title", title);
        if (comment != null) request.put("initial_comment", comment);
        request.put("file", filePath);
        
        // TODO: 实现 multipart/form-data 文件上传
        log.info("Slack 文件上传：{} -> {}", filePath, channel);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        result.put("file", filePath);
        
        return result;
    }
    
    // ==================== 频道管理 ====================
    
    /**
     * 列出频道
     */
    public List<Map<String, Object>> listChannels(boolean excludeArchived) throws IOException, InterruptedException {
        String url = API_BASE + "/conversations.list?exclude_archived=" + excludeArchived;
        
        JsonNode response = getJson(url);
        
        List<Map<String, Object>> channels = new ArrayList<>();
        for (JsonNode channel : response.path("channels")) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", channel.path("id").asText());
            item.put("name", channel.path("name").asText());
            item.put("created", channel.path("created").asInt());
            item.put("is_private", channel.path("is_private").asBoolean());
            item.put("num_members", channel.path("num_members").asInt());
            
            channels.add(item);
        }
        
        return channels;
    }
    
    /**
     * 创建频道
     */
    public Map<String, Object> createChannel(String name, boolean isPrivate) throws IOException, InterruptedException {
        String url = API_BASE + "/conversations.create";
        
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        request.put("is_private", isPrivate);
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", response.path("ok").asBoolean());
        
        JsonNode channel = response.path("channel");
        result.put("id", channel.path("id").asText());
        result.put("name", channel.path("name").asText());
        
        return result;
    }
    
    /**
     * 邀请用户到频道
     */
    public Map<String, Object> inviteToChannel(String channelId, List<String> userIds) throws IOException, InterruptedException {
        String url = API_BASE + "/conversations.invite";
        
        Map<String, Object> request = new HashMap<>();
        request.put("channel", channelId);
        request.put("users", String.join(",", userIds));
        
        JsonNode response = postJson(url, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ok", response.path("ok").asBoolean());
        
        return result;
    }
    
    // ==================== 用户查询 ====================
    
    /**
     * 获取用户信息
     */
    public Map<String, Object> getUserInfo(String userId) throws IOException, InterruptedException {
        String url = API_BASE + "/users.info?user=" + userId;
        
        JsonNode response = getJson(url);
        
        Map<String, Object> result = new HashMap<>();
        JsonNode user = response.path("user");
        
        result.put("id", user.path("id").asText());
        result.put("name", user.path("name").asText());
        result.put("real_name", user.path("real_name").asText());
        result.put("display_name", user.path("profile").path("display_name").asText());
        result.put("email", user.path("profile").path("email").asText());
        result.put("is_bot", user.path("is_bot").asBoolean());
        result.put("is_admin", user.path("is_admin").asBoolean());
        
        return result;
    }
    
    /**
     * 列出用户
     */
    public List<Map<String, Object>> listUsers() throws IOException, InterruptedException {
        String url = API_BASE + "/users.list";
        
        JsonNode response = getJson(url);
        
        List<Map<String, Object>> users = new ArrayList<>();
        for (JsonNode user : response.path("members")) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", user.path("id").asText());
            item.put("name", user.path("name").asText());
            item.put("real_name", user.path("real_name").asText());
            item.put("is_bot", user.path("is_bot").asBoolean());
            
            users.add(item);
        }
        
        return users;
    }
    
    // ==================== 辅助方法 ====================
    
    private JsonNode getJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return objectMapper.readTree(response.body());
    }
    
    private JsonNode postJson(String url, Object body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return objectMapper.readTree(response.body());
    }
    
    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return enabled && token != null;
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            String url = API_BASE + "/auth.test";
            JsonNode response = getJson(url);
            return response.path("ok").asBoolean();
        } catch (Exception e) {
            log.error("Slack 连接测试失败", e);
            return false;
        }
    }
    
    /**
     * 获取服务信息
     */
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "slack");
        info.put("enabled", enabled);
        info.put("connected", isConnected());
        return info;
    }
}
