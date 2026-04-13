package com.jclaw.skills.feishu;

import com.jclaw.skills.Skill;
import com.jclaw.skills.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 飞书日程管理技能
 */
@Slf4j
@Service
public class FeishuCalendarSkill implements Skill {
    
    // 内存存储日程
    private final List<CalendarEvent> events = new ArrayList<>();
    
    @Override
    public String getName() {
        return "feishu_calendar";
    }
    
    @Override
    public String getDescription() {
        return "管理飞书日程（创建/查询/删除）";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String action = (String) params.get("action");
            
            if (action == null) {
                return SkillResult.error("缺少参数：action (create/list/delete)");
            }
            
            switch (action.toLowerCase()) {
                case "create":
                    return createEvent(params);
                case "list":
                    return listEvents(params);
                case "delete":
                    return deleteEvent(params);
                default:
                    return SkillResult.error("未知操作：" + action);
            }
            
        } catch (Exception e) {
            log.error("日程操作失败", e);
            return SkillResult.error("操作失败：" + e.getMessage());
        }
    }
    
    private SkillResult createEvent(Map<String, Object> params) {
        String summary = (String) params.get("summary");
        String startTime = (String) params.get("start_time");
        String endTime = (String) params.get("end_time");
        String description = (String) params.get("description");
        
        if (summary == null || summary.isEmpty()) {
            return SkillResult.error("缺少参数：summary");
        }
        
        CalendarEvent event = new CalendarEvent();
        event.setId(UUID.randomUUID().toString().substring(0, 8));
        event.setSummary(summary);
        event.setStartTime(startTime != null ? startTime : LocalDateTime.now().toString());
        event.setEndTime(endTime != null ? endTime : LocalDateTime.now().plusHours(1).toString());
        event.setDescription(description);
        event.setCreatedAt(LocalDateTime.now());
        
        events.add(event);
        
        log.info("创建日程：{} - {}", event.getId(), summary);
        
        return SkillResult.success("日程创建成功", Map.of(
            "id", event.getId(),
            "summary", summary,
            "start_time", event.getStartTime()
        ));
    }
    
    private SkillResult listEvents(Map<String, Object> params) {
        String date = (String) params.get("date");
        
        List<CalendarEvent> filtered = events;
        if (date != null) {
            filtered = events.stream()
                .filter(e -> e.getStartTime().startsWith(date))
                .toList();
        }
        
        if (filtered.isEmpty()) {
            return SkillResult.success("暂无日程", Map.of("count", 0));
        }
        
        StringBuilder sb = new StringBuilder("日程列表:\n\n");
        for (CalendarEvent event : filtered) {
            sb.append("📅 ").append(event.getSummary())
              .append("\n   时间：").append(formatTime(event.getStartTime()))
              .append(" - ").append(formatTime(event.getEndTime()))
              .append("\n");
            if (event.getDescription() != null) {
                sb.append("   描述：").append(event.getDescription()).append("\n");
            }
            sb.append("\n");
        }
        
        return SkillResult.success(sb.toString(), Map.of(
            "count", filtered.size(),
            "events", filtered
        ));
    }
    
    private SkillResult deleteEvent(Map<String, Object> params) {
        String eventId = (String) params.get("event_id");
        
        if (eventId == null) {
            return SkillResult.error("缺少参数：event_id");
        }
        
        boolean removed = events.removeIf(e -> e.getId().equals(eventId));
        
        if (removed) {
            log.info("删除日程：{}", eventId);
            return SkillResult.success("日程已删除");
        }
        
        return SkillResult.error("未找到日程：" + eventId);
    }
    
    private String formatTime(String timeStr) {
        try {
            LocalDateTime time = LocalDateTime.parse(timeStr);
            return time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        } catch (Exception e) {
            return timeStr;
        }
    }
    
    private static class CalendarEvent {
        private String id;
        private String summary;
        private String startTime;
        private String endTime;
        private String description;
        private LocalDateTime createdAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
