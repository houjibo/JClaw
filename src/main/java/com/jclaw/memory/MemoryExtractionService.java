package com.jclaw.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 记忆萃取服务 - 从对话中自动提取记忆
 */
@Slf4j
@Service
public class MemoryExtractionService {
    
    private static final Pattern USER_PREF_PATTERN = Pattern.compile(
        "(?:用户 | 我 | 波哥) (?:喜欢 | 偏好 | 习惯|常用) (.+?)[。.!！]",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern DECISION_PATTERN = Pattern.compile(
        "(?:决定 | 确定|计划) (.+?)[。.!！]",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern LESSON_PATTERN = Pattern.compile(
        "(?:经验 | 教训 | 心得 | 发现) (.+?)[。.!！]",
        Pattern.CASE_INSENSITIVE
    );
    
    private final MemoryService memoryService;
    
    public MemoryExtractionService(MemoryService memoryService) {
        this.memoryService = memoryService;
    }
    
    /**
     * 从对话中提取记忆
     */
    public ExtractionResult extract(String conversation, String memoryPath) {
        ExtractionResult result = new ExtractionResult();
        
        // 提取用户偏好
        List<String> preferences = extractPattern(conversation, USER_PREF_PATTERN);
        result.setPreferences(preferences);
        
        // 提取决策
        List<String> decisions = extractPattern(conversation, DECISION_PATTERN);
        result.setDecisions(decisions);
        
        // 提取经验教训
        List<String> lessons = extractPattern(conversation, LESSON_PATTERN);
        result.setLessons(lessons);
        
        // 保存到记忆
        saveExtractions(result, memoryPath);
        
        log.info("记忆萃取完成：偏好{} 决策{} 教训{}", 
            preferences.size(), decisions.size(), lessons.size());
        
        return result;
    }
    
    /**
     * 从每日日志中萃取记忆
     */
    public void extractFromDailyLog(String memoryPath) {
        try {
            Path path = Paths.get(memoryPath);
            if (!Files.exists(path)) {
                return;
            }
            
            // 读取最近的日志
            String todayFile = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".md";
            Path todayPath = path.resolve(todayFile);
            
            if (!Files.exists(todayPath)) {
                return;
            }
            
            String content = Files.readString(todayPath);
            
            // 提取关键信息
            ExtractionResult result = extract(content, memoryPath);
            
            // 合并到 MEMORY.md
            if (!result.isEmpty()) {
                appendToMemory(result, memoryPath);
            }
            
        } catch (IOException e) {
            log.error("从日志萃取记忆失败", e);
        }
    }
    
    /**
     * 提取正则匹配
     */
    private List<String> extractPattern(String text, Pattern pattern) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            if (matcher.groupCount() >= 1) {
                String match = matcher.group(1).trim();
                if (!match.isEmpty() && match.length() < 200) {
                    matches.add(match);
                }
            }
        }
        
        return matches;
    }
    
    /**
     * 保存萃取结果
     */
    private void saveExtractions(ExtractionResult result, String memoryPath) {
        StringBuilder sb = new StringBuilder();
        
        if (!result.getPreferences().isEmpty()) {
            sb.append("## 用户偏好\n\n");
            for (String pref : result.getPreferences()) {
                sb.append("- ").append(pref).append("\n");
            }
            sb.append("\n");
        }
        
        if (!result.getDecisions().isEmpty()) {
            sb.append("## 重要决策\n\n");
            for (String decision : result.getDecisions()) {
                sb.append("- ").append(decision).append("\n");
            }
            sb.append("\n");
        }
        
        if (!result.getLessons().isEmpty()) {
            sb.append("## 经验教训\n\n");
            for (String lesson : result.getLessons()) {
                sb.append("- ").append(lesson).append("\n");
            }
            sb.append("\n");
        }
        
        if (sb.length() > 0) {
            memoryService.save(sb.toString());
        }
    }
    
    /**
     * 合并到 MEMORY.md
     */
    private void appendToMemory(ExtractionResult result, String memoryPath) {
        try {
            Path memoryFile = Paths.get(memoryPath).resolve("MEMORY.md");
            
            StringBuilder sb = new StringBuilder();
            sb.append("\n## ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
              .append(" 萃取\n\n");
            
            if (!result.getPreferences().isEmpty()) {
                sb.append("### 偏好\n");
                for (String pref : result.getPreferences()) {
                    sb.append("- ").append(pref).append("\n");
                }
            }
            
            if (!result.getDecisions().isEmpty()) {
                sb.append("\n### 决策\n");
                for (String decision : result.getDecisions()) {
                    sb.append("- ").append(decision).append("\n");
                }
            }
            
            Files.writeString(memoryFile, sb.toString(), 
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
            
            log.info("萃取结果已合并到 MEMORY.md");
            
        } catch (IOException e) {
            log.error("合并萃取结果失败", e);
        }
    }
    
    /**
     * 萃取结果
     */
    public static class ExtractionResult {
        private List<String> preferences = new ArrayList<>();
        private List<String> decisions = new ArrayList<>();
        private List<String> lessons = new ArrayList<>();
        
        public List<String> getPreferences() { return preferences; }
        public void setPreferences(List<String> preferences) { this.preferences = preferences; }
        public List<String> getDecisions() { return decisions; }
        public void setDecisions(List<String> decisions) { this.decisions = decisions; }
        public List<String> getLessons() { return lessons; }
        public void setLessons(List<String> lessons) { this.lessons = lessons; }
        
        public boolean isEmpty() {
            return preferences.isEmpty() && decisions.isEmpty() && lessons.isEmpty();
        }
    }
}
