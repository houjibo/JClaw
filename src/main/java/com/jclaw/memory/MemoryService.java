package com.jclaw.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 记忆服务 - 管理长期记忆和每日日志
 */
@Slf4j
@Service
public class MemoryService {
    
    @Value("${jclaw.memory.path:./memory}")
    private String memoryPath;
    
    @Value("${jclaw.memory.daily-log-enabled:true}")
    private boolean dailyLogEnabled;
    
    private static final String MEMORY_FILE = "MEMORY.md";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 保存记忆到 MEMORY.md
     */
    public void save(String memory) {
        try {
            Path memoryDir = Paths.get(memoryPath);
            Files.createDirectories(memoryDir);
            
            Path memoryFile = memoryDir.resolve(MEMORY_FILE);
            
            // 追加记忆
            String entry = String.format("## %s\n\n%s\n\n", 
                LocalDate.now().format(DATE_FORMAT), memory);
            
            Files.writeString(memoryFile, entry, java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.APPEND);
            
            log.info("保存记忆：{}", memory.substring(0, Math.min(50, memory.length())));
            
        } catch (IOException e) {
            log.error("保存记忆失败", e);
        }
    }
    
    /**
     * 搜索记忆（简单文本搜索）
     */
    public String search(String query) {
        try {
            Path memoryFile = Paths.get(memoryPath).resolve(MEMORY_FILE);
            if (!Files.exists(memoryFile)) {
                return "暂无记忆";
            }
            
            String content = Files.readString(memoryFile);
            String[] lines = content.split("\n");
            
            List<String> results = new ArrayList<>();
            for (String line : lines) {
                if (line.toLowerCase().contains(query.toLowerCase())) {
                    results.add(line.trim());
                    if (results.size() >= 10) {
                        break;
                    }
                }
            }
            
            if (results.isEmpty()) {
                return "未找到相关记忆";
            }
            
            return String.join("\n", results);
            
        } catch (IOException e) {
            log.error("搜索记忆失败", e);
            return "搜索失败：" + e.getMessage();
        }
    }
    
    /**
     * 获取或创建今日日志
     */
    public String getTodayLog() {
        if (!dailyLogEnabled) {
            return "";
        }
        
        try {
            Path memoryDir = Paths.get(memoryPath);
            Files.createDirectories(memoryDir);
            
            String todayFile = LocalDate.now().format(DATE_FORMAT) + ".md";
            Path logPath = memoryDir.resolve(todayFile);
            
            if (!Files.exists(logPath)) {
                // 创建今日日志
                String header = String.format(
                    "# %s 日志\n\n## 对话记录\n\n## 任务\n\n## 笔记\n\n",
                    LocalDate.now().format(DATE_FORMAT)
                );
                Files.writeString(logPath, header);
                log.info("创建今日日志：{}", todayFile);
            }
            
            return Files.readString(logPath);
            
        } catch (IOException e) {
            log.error("获取今日日志失败", e);
            return "";
        }
    }
    
    /**
     * 追加到今日日志
     */
    public void appendToTodayLog(String section, String content) {
        if (!dailyLogEnabled) {
            return;
        }
        
        try {
            String todayFile = LocalDate.now().format(DATE_FORMAT) + ".md";
            Path logPath = Paths.get(memoryPath).resolve(todayFile);
            
            String entry = String.format("### %s\n\n%s\n\n", section, content);
            Files.writeString(logPath, entry, java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
            
            log.info("追加到今日日志：{}", section);
            
        } catch (IOException e) {
            log.error("追加日志失败", e);
        }
    }
}
