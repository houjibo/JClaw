package com.jclaw.channels.impl;

import com.jclaw.channels.ChannelMessage;
import com.jclaw.channels.MessageChannel;
import com.jclaw.skills.SkillEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 飞书消息通道实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuChannel implements MessageChannel {
    
    private final SkillEngine skillEngine;
    
    @Value("${jclaw.channels.feishu.enabled:false}")
    private boolean enabled;
    
    @Value("${jclaw.channels.feishu.app-id:}")
    private String appId;
    
    @Value("${jclaw.channels.feishu.app-secret:}")
    private String appSecret;
    
    // 消息处理器
    private Consumer<ChannelMessage> messageHandler;
    
    @Override
    public String getName() {
        return "feishu";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && appId != null && !appId.isEmpty();
    }
    
    @Override
    public boolean send(String receiverId, String content) {
        return send(receiverId, content, "text");
    }
    
    @Override
    public boolean send(String receiverId, String content, String messageType) {
        if (!isEnabled()) {
            log.warn("飞书通道未启用，无法发送消息");
            return false;
        }
        
        try {
            // TODO: 集成飞书 API 发送消息
            // 目前先调用技能引擎发送
            String apiPath = "oc_" + receiverId; // 飞书群聊 ID 格式
            
            log.info("发送飞书消息到：{} 内容：{}", apiPath, content);
            
            // 使用飞书 IM 工具发送（需要配置飞书插件）
            // 这里先记录日志，实际发送需要飞书 API
            
            return true;
            
        } catch (Exception e) {
            log.error("发送飞书消息失败", e);
            return false;
        }
    }
    
    /**
     * 接收消息（回调入口）
     */
    public void onMessage(ChannelMessage message) {
        log.info("收到飞书消息：{} - {}", message.getSenderId(), message.getContent());
        
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
        
        // 处理消息
        handleMessage(message);
    }
    
    /**
     * 处理消息
     */
    private void handleMessage(ChannelMessage message) {
        String content = message.getContent().trim();
        
        // 命令解析
        if (content.startsWith("/")) {
            handleCommand(message, content);
        } else {
            // 普通对话，调用 AI
            handleChat(message, content);
        }
    }
    
    /**
     * 处理命令
     */
    private void handleCommand(ChannelMessage message, String command) {
        String[] parts = command.substring(1).split("\\s+", 2);
        String cmd = parts[0];
        String args = parts.length > 1 ? parts[1] : "";
        
        log.info("执行命令：{} 参数：{}", cmd, args);
        
        switch (cmd) {
            case "help":
                send(message.getSenderId(), getHelpMessage());
                break;
                
            case "skill":
            case "skills":
                listSkills(message);
                break;
                
            case "bash":
                executeBash(message, args);
                break;
                
            case "search":
                webSearch(message, args);
                break;
                
            case "todo":
                manageTodo(message, args);
                break;
                
            case "git":
                executeGit(message, args);
                break;
                
            default:
                send(message.getSenderId(), "未知命令：" + cmd + "\n输入 /help 查看帮助");
        }
    }
    
    /**
     * 处理普通对话
     */
    private void handleChat(ChannelMessage message, String content) {
        log.info("AI 对话：{}", content);
        
        // 调用 AI 服务
        var result = skillEngine.execute("ai_chat", Map.of("prompt", content));
        
        if (result.isSuccess()) {
            send(message.getSenderId(), result.getContent());
        } else {
            send(message.getSenderId(), "AI 响应失败：" + result.getError());
        }
    }
    
    /**
     * 列出可用技能
     */
    private void listSkills(ChannelMessage message) {
        var skills = skillEngine.listSkills();
        StringBuilder sb = new StringBuilder("可用技能:\n\n");
        for (var skill : skills) {
            sb.append("• ").append(skill.getName())
              .append(" - ").append(skill.getDescription()).append("\n");
        }
        send(message.getSenderId(), sb.toString());
    }
    
    /**
     * 执行 Bash 命令
     */
    private void executeBash(ChannelMessage message, String command) {
        if (command.isEmpty()) {
            send(message.getSenderId(), "用法：/bash <命令>");
            return;
        }
        
        var result = skillEngine.execute("bash", Map.of("command", command));
        send(message.getSenderId(), result.getContent());
    }
    
    /**
     * 网络搜索
     */
    private void webSearch(ChannelMessage message, String query) {
        if (query.isEmpty()) {
            send(message.getSenderId(), "用法：/search <搜索词>");
            return;
        }
        
        var result = skillEngine.execute("web_search", Map.of("query", query));
        send(message.getSenderId(), result.getContent());
    }
    
    /**
     * 管理 TODO
     */
    private void manageTodo(ChannelMessage message, String args) {
        // 简单解析：/todo create 买可乐
        String[] parts = args.split("\\s+", 2);
        if (parts.length < 2) {
            send(message.getSenderId(), "用法：/todo create <内容>");
            return;
        }
        
        var result = skillEngine.execute("todo_write", Map.of(
            "action", parts[0],
            "content", parts[1]
        ));
        send(message.getSenderId(), result.getContent());
    }
    
    /**
     * Git 操作
     */
    private void executeGit(ChannelMessage message, String args) {
        if (args.isEmpty()) {
            send(message.getSenderId(), "用法：/git <command> [repoPath]");
            return;
        }
        
        String[] parts = args.split("\\s+");
        var result = skillEngine.execute("git", Map.of(
            "command", parts[0],
            "repoPath", parts.length > 1 ? parts[1] : "."
        ));
        send(message.getSenderId(), result.getContent());
    }
    
    /**
     * 帮助消息
     */
    private String getHelpMessage() {
        return """
            JClaw 助手命令:
            
            /help - 显示帮助
            /skills - 列出可用技能
            /bash <命令> - 执行 Bash 命令
            /search <关键词> - 网络搜索
            /todo create <内容> - 创建 TODO
            /git <命令> [路径] - Git 操作
            
            直接输入文字可与 AI 对话
            """;
    }
    
    /**
     * 设置消息处理器
     */
    public void setMessageHandler(Consumer<ChannelMessage> handler) {
        this.messageHandler = handler;
    }
}
