package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

/**
 * 日程管理技能
 */
@Slf4j
@Service
public class ScheduleSkill implements Skill {
    
    // 内存存储日程
    private final List<ScheduleItem> schedules = new ArrayList<>();
    
    @Override
    public String getName() {
        return "schedule";
    }
    
    @Override
    public String getDescription() {
        return "管理日程安排";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String action = (String) params.get("action");
            
            if (action == null) {
                return SkillResult.error("缺少参数：action (add/list/remove)");
            }
            
            switch (action.toLowerCase()) {
                case "add":
                    return addSchedule(params);
                case "list":
                    return listSchedules(params);
                case "remove":
                    return removeSchedule(params);
                case "today":
                    return showToday();
                default:
                    return SkillResult.error("未知操作：" + action);
            }
            
        } catch (Exception e) {
            log.error("日程操作失败", e);
            return SkillResult.error("操作失败：" + e.getMessage());
        }
    }
    
    private SkillResult addSchedule(Map<String, Object> params) {
        String title = (String) params.get("title");
        String date = (String) params.get("date");
        String time = (String) params.getOrDefault("time", "09:00");
        
        if (title == null || title.isEmpty()) {
            return SkillResult.error("缺少参数：title");
        }
        
        ScheduleItem item = new ScheduleItem();
        item.setId(UUID.randomUUID().toString().substring(0, 6));
        item.setTitle(title);
        item.setDate(date != null ? date : LocalDate.now().toString());
        item.setTime(time);
        item.setCreatedAt(LocalDateTime.now());
        
        schedules.add(item);
        
        log.info("添加日程：{} - {}", item.getDate(), item.getTitle());
        
        return SkillResult.success("日程已添加", Map.of(
            "id", item.getId(),
            "title", title,
            "date", item.getDate(),
            "time", item.getTime()
        ));
    }
    
    private SkillResult listSchedules(Map<String, Object> params) {
        String date = (String) params.get("date");
        
        List<ScheduleItem> filtered = schedules;
        if (date != null) {
            filtered = schedules.stream()
                .filter(s -> s.getDate().equals(date))
                .toList();
        }
        
        if (filtered.isEmpty()) {
            return SkillResult.success("暂无日程", Map.of("data", "[]"));
        }
        
        StringBuilder sb = new StringBuilder("日程列表:\n\n");
        for (ScheduleItem item : filtered) {
            sb.append(String.format("📅 %s %s - %s\n", 
                item.getDate(), item.getTime(), item.getTitle()));
        }
        
        return SkillResult.success(sb.toString(), Map.of(
            "count", filtered.size()
        ));
    }
    
    private SkillResult removeSchedule(Map<String, Object> params) {
        String id = (String) params.get("id");
        
        if (id == null) {
            return SkillResult.error("缺少参数：id");
        }
        
        boolean removed = schedules.removeIf(s -> s.getId().equals(id));
        
        if (removed) {
            log.info("删除日程：{}", id);
            return SkillResult.success("日程已删除");
        }
        
        return SkillResult.error("未找到日程：" + id);
    }
    
    private SkillResult showToday() {
        String today = LocalDate.now().toString();
        String dayOfWeek = LocalDate.now().getDayOfWeek()
            .getDisplayName(TextStyle.FULL, Locale.CHINA);
        
        List<ScheduleItem> todaySchedules = schedules.stream()
            .filter(s -> s.getDate().equals(today))
            .toList();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📅 今天 (").append(today).append(" ").append(dayOfWeek).append(")\n\n");
        
        if (todaySchedules.isEmpty()) {
            sb.append("暂无日程安排");
        } else {
            for (ScheduleItem item : todaySchedules) {
                sb.append(String.format("⏰ %s - %s\n", item.getTime(), item.getTitle()));
            }
        }
        
        return SkillResult.success(sb.toString(), Map.of(
            "date", today,
            "count", todaySchedules.size()
        ));
    }
    
    /**
     * 日程项
     */
    public static class ScheduleItem {
        private String id;
        private String title;
        private String date;
        private String time;
        private LocalDateTime createdAt;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
