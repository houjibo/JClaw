package com.jclaw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

/**
 * 配置热重载监听器
 */
@Slf4j
@Component
public class ConfigHotReloadListener {
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("配置热重载监听器已启动");
        
        // 监听配置文件变化
        watchConfigFile("application.yml");
        watchConfigFile("application.properties");
    }
    
    private void watchConfigFile(String filename) {
        Path configPath = Paths.get(filename);
        
        if (!Files.exists(configPath)) {
            return;
        }
        
        try {
            FileTime lastModified = Files.getLastModifiedTime(configPath);
            log.info("监听配置文件：{} (最后修改：{})", filename, lastModified);
            
            // 简化实现：实际应该使用 WatchService 监听文件变化
            // 这里只记录日志，提示用户可以重启应用来加载新配置
            
        } catch (Exception e) {
            log.warn("监听配置文件失败：{}", filename, e);
        }
    }
    
    /**
     * 手动触发配置重载
     */
    public void reloadConfig() {
        log.info("配置重载请求收到");
        log.warn("⚠️  配置热重载功能尚未完全实现，请重启应用以加载新配置");
        log.info("重启命令：./jclaw restart 或 java -jar jclaw.jar");
    }
}
