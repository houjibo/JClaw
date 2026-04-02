package com.openclaw.jcode.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 配置变更检测器
 * 监听配置文件变化，自动重新加载
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ConfigChangeDetector {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigChangeDetector.class);
    
    /**
     * 配置文件路径
     */
    private final List<Path> watchedPaths = new CopyOnWriteArrayList<>();
    
    /**
     * 文件监听服务
     */
    private WatchService watchService;
    
    /**
     * 监听线程
     */
    private Thread watchThread;
    
    /**
     * 是否正在监听
     */
    private volatile boolean isWatching = false;
    
    /**
     * 事件发布器
     */
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 最后修改时间
     */
    private final java.util.Map<Path, java.time.Instant> lastModified = new java.util.concurrent.ConcurrentHashMap<>();
    
    public ConfigChangeDetector(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * 添加监听路径
     * 
     * @param path 配置文件路径
     */
    public void addWatchPath(Path path) {
        if (!watchedPaths.contains(path)) {
            watchedPaths.add(path);
            log.info("添加配置监听路径：{}", path);
            
            // 如果已经在监听，重新注册
            if (isWatching) {
                registerWatch(path);
            }
        }
    }
    
    /**
     * 添加监听路径（字符串）
     * 
     * @param pathStr 配置文件路径字符串
     */
    public void addWatchPath(String pathStr) {
        addWatchPath(Paths.get(pathStr));
    }
    
    /**
     * 移除监听路径
     * 
     * @param path 配置文件路径
     */
    public void removeWatchPath(Path path) {
        watchedPaths.remove(path);
        log.info("移除配置监听路径：{}", path);
    }
    
    /**
     * 开始监听
     */
    public void startWatching() {
        if (isWatching) {
            log.warn("配置监听已在运行");
            return;
        }
        
        try {
            watchService = FileSystems.getDefault().newWatchService();
            
            // 注册所有路径
            for (Path path : watchedPaths) {
                registerWatch(path);
            }
            
            // 启动监听线程
            isWatching = true;
            watchThread = new Thread(this::watchLoop, "ConfigChangeDetector");
            watchThread.setDaemon(true);
            watchThread.start();
            
            log.info("配置监听已启动，监听 {} 个路径", watchedPaths.size());
            
        } catch (IOException e) {
            log.error("启动配置监听失败", e);
        }
    }
    
    /**
     * 停止监听
     */
    public void stopWatching() {
        isWatching = false;
        
        if (watchThread != null) {
            watchThread.interrupt();
            try {
                watchThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.warn("关闭 WatchService 失败", e);
            }
        }
        
        log.info("配置监听已停止");
    }
    
    /**
     * 注册监听
     */
    private void registerWatch(Path path) {
        if (watchService == null) {
            return;
        }
        
        try {
            Path dir = path.getParent();
            if (dir != null && Files.exists(dir)) {
                dir.register(watchService,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE);
                log.debug("注册目录监听：{}", dir);
            }
        } catch (IOException e) {
            log.warn("注册监听失败：{}", path, e);
        }
    }
    
    /**
     * 监听循环
     */
    private void watchLoop() {
        while (isWatching) {
            try {
                WatchKey key = watchService.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    
                    // 检查是否是监听的文件
                    for (Path watchedPath : watchedPaths) {
                        if (watchedPath.getFileName().equals(fileName)) {
                            handleFileChange(watchedPath);
                            break;
                        }
                    }
                }
                
                key.reset();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("配置监听异常", e);
            }
        }
    }
    
    /**
     * 处理文件变更
     */
    private void handleFileChange(Path path) {
        try {
            // 防抖：检查最后修改时间
            java.time.Instant now = java.time.Instant.now();
            java.time.Instant lastMod = lastModified.getOrDefault(path, java.time.Instant.MIN);
            
            if (java.time.Duration.between(lastMod, now).getSeconds() < 1) {
                // 1 秒内的修改忽略（防抖）
                return;
            }
            
            lastModified.put(path, now);
            
            // 检查文件是否存在
            if (!Files.exists(path)) {
                log.warn("配置文件已被删除：{}", path);
                return;
            }
            
            log.info("检测到配置文件变更：{}", path);
            
            // 发布事件
            eventPublisher.publishEvent(new ConfigChangeEvent(this, path));
            
        } catch (Exception e) {
            log.error("处理文件变更失败：{}", path, e);
        }
    }
    
    /**
     * 获取监听状态
     */
    public boolean isWatching() {
        return isWatching;
    }
    
    /**
     * 获取监听路径列表
     */
    public List<Path> getWatchedPaths() {
        return new CopyOnWriteArrayList<>(watchedPaths);
    }
    
    /**
     * 配置变更事件
     */
    public static class ConfigChangeEvent extends java.util.EventObject {
        
        private final Path configPath;
        
        public ConfigChangeEvent(Object source, Path configPath) {
            super(source);
            this.configPath = configPath;
        }
        
        public Path getConfigPath() {
            return configPath;
        }
        
        @Override
        public String toString() {
            return "ConfigChangeEvent{path=" + configPath + "}";
        }
    }
}
