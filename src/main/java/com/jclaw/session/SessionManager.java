package com.jclaw.session;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理服务
 */
@Slf4j
@Service
public class SessionManager {
    
    // 内存存储会话
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    
    /**
     * 创建会话
     */
    public Session createSession(String userId) {
        String sessionId = UUID.randomUUID().toString().substring(0, 8);
        
        Session session = new Session();
        session.setId(sessionId);
        session.setUserId(userId);
        session.setCreatedAt(LocalDateTime.now());
        session.setMessages(new ArrayList<>());
        session.setContext(new HashMap<>());
        
        sessions.put(sessionId, session);
        
        log.info("创建会话：{} 用户：{}", sessionId, userId);
        return session;
    }
    
    /**
     * 获取会话
     */
    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    /**
     * 添加消息到会话
     */
    public void addMessage(String sessionId, String role, String content) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            log.warn("会话不存在：{}", sessionId);
            return;
        }
        
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        
        session.getMessages().add(message);
        session.setUpdatedAt(LocalDateTime.now());
        
        // 限制会话消息数量（保留最近 50 条）
        if (session.getMessages().size() > 50) {
            session.getMessages().remove(0);
        }
        
        log.debug("会话 {} 添加消息：{} - {}", sessionId, role, content.substring(0, Math.min(50, content.length())));
    }
    
    /**
     * 获取会话历史
     */
    public List<Message> getHistory(String sessionId, int limit) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            return Collections.emptyList();
        }
        
        List<Message> messages = session.getMessages();
        if (limit <= 0 || limit >= messages.size()) {
            return messages;
        }
        
        return messages.subList(messages.size() - limit, messages.size());
    }
    
    /**
     * 设置会话上下文
     */
    public void setContext(String sessionId, String key, Object value) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.getContext().put(key, value);
        }
    }
    
    /**
     * 获取会话上下文
     */
    public Object getContext(String sessionId, String key) {
        Session session = sessions.get(sessionId);
        return session != null ? session.getContext().get(key) : null;
    }
    
    /**
     * 删除会话
     */
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("删除会话：{}", sessionId);
    }
    
    /**
     * 列出所有会话
     */
    public List<Session> listSessions() {
        return new ArrayList<>(sessions.values());
    }
    
    /**
     * 清理过期会话（超过 24 小时未活动）
     */
    public void cleanupExpiredSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        
        sessions.entrySet().removeIf(entry -> {
            Session session = entry.getValue();
            boolean expired = session.getUpdatedAt() != null && 
                session.getUpdatedAt().isBefore(threshold);
            if (expired) {
                log.info("清理过期会话：{}", entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * 会话
     */
    @Data
    @NoArgsConstructor
    public static class Session {
        private String id;
        private String userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Message> messages;
        private Map<String, Object> context;
    }
    
    /**
     * 消息
     */
    @Data
    @NoArgsConstructor
    public static class Message {
        private String role; // user, assistant, system
        private String content;
        private LocalDateTime timestamp;
    }
}
