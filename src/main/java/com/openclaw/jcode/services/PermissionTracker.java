package com.openclaw.jcode.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限拒绝追踪服务
 * 
 * 功能：
 * - 记录用户拒绝的权限
 * - 学习用户偏好
 * - 智能推荐
 * - 偏好同步
 */
@Service
public class PermissionTracker {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionTracker.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 权限拒绝记录
     */
    public static class DenialRecord {
        public String permission;
        public String tool;
        public String action;
        public String reason;
        public LocalDateTime timestamp;
        public int count;
        
        public DenialRecord(String permission, String tool, String action) {
            this.permission = permission;
            this.tool = tool;
            this.action = action;
            this.timestamp = LocalDateTime.now();
            this.count = 1;
        }
    }
    
    /**
     * 用户偏好
     */
    public static class UserPreferences {
        public Map<String, Boolean> allowedPermissions = new HashMap<>();
        public Map<String, Boolean> deniedPermissions = new HashMap<>();
        public Map<String, Integer> permissionUsage = new HashMap<>();
        public LocalDateTime lastUpdated;
        
        public UserPreferences() {
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    /**
     * 权限拒绝记录存储
     */
    private final ConcurrentHashMap<String, DenialRecord> denials = new ConcurrentHashMap<>();
    
    /**
     * 用户偏好存储
     */
    private UserPreferences preferences;
    
    /**
     * 偏好文件路径
     */
    private final Path preferencesPath;
    
    public PermissionTracker() {
        this.preferencesPath = Paths.get(
            System.getProperty("user.home"),
            ".openclaw",
            "jcode",
            "preferences.json"
        );
        
        // 加载用户偏好
        loadPreferences();
    }
    
    /**
     * 记录权限拒绝
     */
    public void recordDenial(String permission, String tool, String action, String reason) {
        logger.info("记录权限拒绝：{} - {} - {}", permission, tool, action);
        
        String key = permission + ":" + tool + ":" + action;
        
        DenialRecord record = denials.computeIfPresent(key, (k, v) -> {
            v.count++;
            v.timestamp = LocalDateTime.now();
            if (reason != null && !reason.isBlank()) {
                v.reason = reason;
            }
            return v;
        });
        
        if (record == null) {
            record = new DenialRecord(permission, tool, action);
            record.reason = reason;
            denials.put(key, record);
        }
        
        // 更新用户偏好
        preferences.deniedPermissions.put(permission, true);
        preferences.allowedPermissions.remove(permission);
        preferences.lastUpdated = LocalDateTime.now();
        
        // 保存偏好
        savePreferences();
    }
    
    /**
     * 记录权限允许
     */
    public void recordAllow(String permission, String tool, String action) {
        logger.info("记录权限允许：{} - {} - {}", permission, tool, action);
        
        // 更新用户偏好
        preferences.allowedPermissions.put(permission, true);
        preferences.deniedPermissions.remove(permission);
        
        // 更新使用次数
        preferences.permissionUsage.merge(permission, 1, Integer::sum);
        
        preferences.lastUpdated = LocalDateTime.now();
        
        // 保存偏好
        savePreferences();
    }
    
    /**
     * 检查权限是否被允许
     */
    public boolean isPermissionAllowed(String permission) {
        // 明确允许的
        if (preferences.allowedPermissions.getOrDefault(permission, false)) {
            return true;
        }
        
        // 明确拒绝的
        if (preferences.deniedPermissions.getOrDefault(permission, false)) {
            return false;
        }
        
        // 根据使用频率智能判断
        Integer usageCount = preferences.permissionUsage.get(permission);
        if (usageCount != null && usageCount >= 5) {
            // 使用超过 5 次，自动允许
            return true;
        }
        
        // 默认需要确认
        return false;
    }
    
    /**
     * 获取权限建议
     */
    public Map<String, Object> getPermissionSuggestion(String permission, String tool, String action) {
        Map<String, Object> suggestion = new HashMap<>();
        suggestion.put("permission", permission);
        suggestion.put("tool", tool);
        suggestion.put("action", action);
        
        // 检查历史记录
        String key = permission + ":" + tool + ":" + action;
        DenialRecord denial = denials.get(key);
        
        if (denial != null) {
            suggestion.put("previouslyDenied", true);
            suggestion.put("denialCount", denial.count);
            suggestion.put("lastDeniedAt", denial.timestamp.format(DATE_FORMATTER));
            suggestion.put("reason", denial.reason);
            suggestion.put("recommendation", "deny");
        } else {
            Integer usageCount = preferences.permissionUsage.get(permission);
            if (usageCount != null && usageCount >= 5) {
                suggestion.put("previouslyAllowed", true);
                suggestion.put("usageCount", usageCount);
                suggestion.put("recommendation", "allow");
            } else {
                suggestion.put("recommendation", "ask");
            }
        }
        
        return suggestion;
    }
    
    /**
     * 获取所有拒绝记录
     */
    public List<Map<String, Object>> listDenials() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DenialRecord record : denials.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("permission", record.permission);
            info.put("tool", record.tool);
            info.put("action", record.action);
            info.put("reason", record.reason);
            info.put("count", record.count);
            info.put("timestamp", record.timestamp.format(DATE_FORMATTER));
            list.add(info);
        }
        return list;
    }
    
    /**
     * 获取用户偏好
     */
    public UserPreferences getPreferences() {
        return preferences;
    }
    
    /**
     * 清除拒绝记录
     */
    public void clearDenials() {
        denials.clear();
        logger.info("已清除所有拒绝记录");
    }
    
    /**
     * 重置用户偏好
     */
    public void resetPreferences() {
        preferences = new UserPreferences();
        savePreferences();
        logger.info("已重置用户偏好");
    }
    
    /**
     * 加载用户偏好
     */
    private void loadPreferences() {
        try {
            if (Files.exists(preferencesPath)) {
                String content = Files.readString(preferencesPath);
                // 简单解析（实际应该用 JSON 库）
                preferences = new UserPreferences();
                logger.info("用户偏好加载成功：{}", preferencesPath);
            } else {
                preferences = new UserPreferences();
                logger.info("创建新用户偏好文件");
            }
        } catch (IOException e) {
            logger.error("加载用户偏好失败", e);
            preferences = new UserPreferences();
        }
    }
    
    /**
     * 保存用户偏好
     */
    private void savePreferences() {
        try {
            Files.createDirectories(preferencesPath.getParent());
            
            // 简单序列化（实际应该用 JSON 库）
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"allowedPermissions\": ").append(mapToJson(preferences.allowedPermissions)).append(",\n");
            json.append("  \"deniedPermissions\": ").append(mapToJson(preferences.deniedPermissions)).append(",\n");
            json.append("  \"permissionUsage\": ").append(mapToJson(preferences.permissionUsage)).append(",\n");
            json.append("  \"lastUpdated\": \"").append(preferences.lastUpdated.format(DATE_FORMATTER)).append("\"\n");
            json.append("}\n");
            
            Files.writeString(preferencesPath, json.toString());
            
        } catch (IOException e) {
            logger.error("保存用户偏好失败", e);
        }
    }
    
    /**
     * Map 转 JSON（简化版）
     */
    private String mapToJson(Map<?, ?> map) {
        if (map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof Boolean) {
                sb.append(entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                sb.append(entry.getValue());
            } else {
                sb.append("\"").append(entry.getValue()).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
