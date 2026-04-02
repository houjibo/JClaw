package com.jclaw.security;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限拒绝追踪器
 * 学习用户权限偏好，自动优化权限请求
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
public class PermissionDenialTracker {
    
    /**
     * 拒绝记录
     */
    public static class DenialRecord {
        private final String toolName;
        private final String reason;
        private final WorkingDirectory workingDirectory;
        private final LocalDateTime timestamp;
        private int count;
        
        public DenialRecord(String toolName, String reason, WorkingDirectory workingDirectory) {
            this.toolName = toolName;
            this.reason = reason;
            this.workingDirectory = workingDirectory;
            this.timestamp = LocalDateTime.now();
            this.count = 1;
        }
        
        public void increment() {
            this.count++;
        }
        
        public String getToolName() {
            return toolName;
        }
        
        public String getReason() {
            return reason;
        }
        
        public WorkingDirectory getWorkingDirectory() {
            return workingDirectory;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public int getCount() {
            return count;
        }
        
        @Override
        public String toString() {
            return String.format("DenialRecord{tool=%s, reason=%s, dir=%s, count=%d}", 
                    toolName, reason, workingDirectory, count);
        }
    }
    
    /**
     * 工作目录
     */
    public static class WorkingDirectory {
        private final String path;
        private final boolean isTrusted;
        
        public WorkingDirectory(String path) {
            this(path, false);
        }
        
        public WorkingDirectory(String path, boolean isTrusted) {
            this.path = path;
            this.isTrusted = isTrusted;
        }
        
        public String getPath() {
            return path;
        }
        
        public boolean isTrusted() {
            return isTrusted;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkingDirectory that = (WorkingDirectory) o;
            return Objects.equals(path, that.path);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }
    
    /**
     * 拒绝记录存储
     */
    private final Map<String, DenialRecord> denials = new ConcurrentHashMap<>();
    
    /**
     * 信任的工作目录
     */
    private final Set<String> trustedDirectories = ConcurrentHashMap.newKeySet();
    
    /**
     * 自动拒绝的模式
     */
    private final Map<String, Set<String>> autoDenyPatterns = new ConcurrentHashMap<>();
    
    /**
     * 记录权限拒绝
     * 
     * @param toolName 工具名称
     * @param reason 拒绝原因
     * @param directory 工作目录
     */
    public void recordDenial(String toolName, String reason, String directory) {
        String key = makeKey(toolName, directory);
        
        denials.compute(key, (k, record) -> {
            if (record == null) {
                return new DenialRecord(toolName, reason, new WorkingDirectory(directory));
            } else {
                record.increment();
                return record;
            }
        });
    }
    
    /**
     * 判断是否应该自动拒绝
     * 
     * @param toolName 工具名称
     * @param directory 工作目录
     * @return true 表示应该自动拒绝
     */
    public boolean shouldAutoDeny(String toolName, String directory) {
        // 检查是否在信任目录
        if (isTrustedDirectory(directory)) {
            return false;
        }
        
        // 检查是否有历史拒绝记录
        String key = makeKey(toolName, directory);
        DenialRecord record = denials.get(key);
        
        if (record != null && record.getCount() >= 3) {
            // 同一工具在同一目录被拒绝 3 次以上，自动拒绝
            return true;
        }
        
        // 检查自动拒绝模式
        Set<String> patterns = autoDenyPatterns.get(toolName);
        if (patterns != null) {
            for (String pattern : patterns) {
                if (directory.matches(pattern)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 添加信任目录
     * 
     * @param directory 目录路径
     */
    public void addTrustedDirectory(String directory) {
        trustedDirectories.add(normalizePath(directory));
    }
    
    /**
     * 移除信任目录
     * 
     * @param directory 目录路径
     */
    public void removeTrustedDirectory(String directory) {
        trustedDirectories.remove(normalizePath(directory));
    }
    
    /**
     * 检查是否是信任目录
     * 
     * @param directory 目录路径
     * @return true 表示是信任目录
     */
    public boolean isTrustedDirectory(String directory) {
        String normalized = normalizePath(directory);
        
        // 检查精确匹配
        if (trustedDirectories.contains(normalized)) {
            return true;
        }
        
        // 检查父目录
        for (String trusted : trustedDirectories) {
            if (normalized.startsWith(trusted + "/")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 添加自动拒绝模式
     * 
     * @param toolName 工具名称
     * @param pathPattern 路径正则表达式
     */
    public void addAutoDenyPattern(String toolName, String pathPattern) {
        autoDenyPatterns.computeIfAbsent(toolName, k -> ConcurrentHashMap.newKeySet())
                .add(pathPattern);
    }
    
    /**
     * 获取拒绝历史
     * 
     * @return 拒绝记录列表
     */
    public List<DenialRecord> getHistory() {
        return new ArrayList<>(denials.values());
    }
    
    /**
     * 获取拒绝历史（按工具名称过滤）
     * 
     * @param toolName 工具名称
     * @return 拒绝记录列表
     */
    public List<DenialRecord> getHistoryByTool(String toolName) {
        List<DenialRecord> result = new ArrayList<>();
        for (DenialRecord record : denials.values()) {
            if (record.getToolName().equals(toolName)) {
                result.add(record);
            }
        }
        return result;
    }
    
    /**
     * 清除拒绝历史
     */
    public void clearHistory() {
        denials.clear();
    }
    
    /**
     * 清除拒绝历史（按工具名称）
     * 
     * @param toolName 工具名称
     */
    public void clearHistory(String toolName) {
        denials.entrySet().removeIf(entry -> entry.getValue().getToolName().equals(toolName));
    }
    
    /**
     * 获取统计数据
     * 
     * @return 统计 Map
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDenials", denials.size());
        stats.put("trustedDirectories", trustedDirectories.size());
        stats.put("autoDenyPatterns", autoDenyPatterns.size());
        
        // 按工具统计
        Map<String, Integer> byTool = new HashMap<>();
        for (DenialRecord record : denials.values()) {
            byTool.merge(record.getToolName(), record.getCount(), Integer::sum);
        }
        stats.put("denialsByTool", byTool);
        
        return stats;
    }
    
    /**
     * 生成键
     */
    private String makeKey(String toolName, String directory) {
        return toolName + ":" + normalizePath(directory);
    }
    
    /**
     * 标准化路径
     */
    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        // 移除末尾斜杠
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
