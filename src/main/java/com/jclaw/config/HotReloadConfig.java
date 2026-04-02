package com.jclaw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置热重载服务
 * 
 * 功能：
 * - 配置存储
 * - 配置更新
 * - 配置历史（用于回滚）
 */
@Component
public class HotReloadConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(HotReloadConfig.class);
    
    /**
     * 配置存储
     */
    private final ConcurrentHashMap<String, Object> configStore = new ConcurrentHashMap<>();
    
    /**
     * 配置历史（用于回滚）
     */
    private final ConcurrentHashMap<String, Map<String, Object>> configHistory = new ConcurrentHashMap<>();
    
    @Value("${jclaw.workspace:${user.home}/.openclaw/workspace}")
    private String workspace;
    
    /**
     * 初始化配置服务
     */
    public HotReloadConfig() {
        logger.info("初始化配置服务");
        initDefaultConfig();
    }
    
    /**
     * 初始化默认配置
     */
    private void initDefaultConfig() {
        configStore.put("workspace", workspace != null ? workspace : System.getProperty("user.home") + "/.openclaw/workspace");
        configStore.put("maxReadSize", 10485760);
        configStore.put("allowWrite", true);
        configStore.put("allowExec", true);
        configStore.put("streaming.enabled", true);
        configStore.put("streaming.timeout", 30000L);
        logger.info("默认配置初始化完成");
    }
    
    /**
     * 保存配置历史
     */
    private void saveHistory() {
        if (!configStore.isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            configHistory.put(timestamp, new ConcurrentHashMap<>(configStore));
            
            // 只保留最近 10 个历史版本
            if (configHistory.size() > 10) {
                String oldest = configHistory.keySet().stream()
                    .min(String::compareTo)
                    .orElse(null);
                if (oldest != null) {
                    configHistory.remove(oldest);
                }
            }
        }
    }
    
    /**
     * 重新加载配置
     */
    public synchronized void reloadConfig() {
        logger.info("重新加载配置...");
        
        try {
            // 保存当前配置（用于回滚）
            Map<String, Object> oldConfig = new ConcurrentHashMap<>(configStore);
            
            // 重新初始化
            initDefaultConfig();
            
            logger.info("配置热重载成功");
            
        } catch (Exception e) {
            logger.error("配置热重载失败", e);
        }
    }
    
    /**
     * 获取配置值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object value = configStore.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            logger.error("配置类型转换失败：{}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * 更新配置
     */
    public void set(String key, Object value) {
        logger.info("更新配置：{} = {}", key, value);
        
        // 保存历史
        saveHistory();
        
        configStore.put(key, value);
    }
    
    /**
     * 回滚配置到指定版本
     */
    public boolean rollback(String timestamp) {
        Map<String, Object> oldConfig = configHistory.get(timestamp);
        if (oldConfig == null) {
            logger.error("找不到历史配置：{}", timestamp);
            return false;
        }
        
        logger.info("回滚配置到：{}", timestamp);
        configStore.clear();
        configStore.putAll(oldConfig);
        
        return true;
    }
    
    /**
     * 列出历史版本
     */
    public java.util.List<String> listHistory() {
        return new java.util.ArrayList<>(configHistory.keySet());
    }
    
    /**
     * 获取当前配置
     */
    public Map<String, Object> getAllConfig() {
        return new ConcurrentHashMap<>(configStore);
    }
}
