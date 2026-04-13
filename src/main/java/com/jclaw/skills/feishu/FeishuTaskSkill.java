package com.jclaw.skills.feishu;

import com.jclaw.skills.Skill;
import com.jclaw.skills.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 飞书任务管理技能
 */
@Slf4j
@Service
public class FeishuTaskSkill implements Skill {
    
    // 内存存储任务
    private final List<TaskItem> tasks = new ArrayList<>();
    
    @Override
    public String getName() {
        return "feishu_task";
    }
    
    @Override
    public String getDescription() {
        return "管理飞书任务（创建/查询/更新/完成）";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String action = (String) params.get("action");
            
            if (action == null) {
                return SkillResult.error("缺少参数：action (create/list/update/complete)");
            }
            
            switch (action.toLowerCase()) {
                case "create":
                    return createTask(params);
                case "list":
                    return listTasks(params);
                case "update":
                    return updateTask(params);
                case "complete":
                    return completeTask(params);
                default:
                    return SkillResult.error("未知操作：" + action);
            }
            
        } catch (Exception e) {
            log.error("任务操作失败", e);
            return SkillResult.error("操作失败：" + e.getMessage());
        }
    }
    
    private SkillResult createTask(Map<String, Object> params) {
        String summary = (String) params.get("summary");
        String description = (String) params.get("description");
        String assignee = (String) params.get("assignee");
        
        if (summary == null || summary.isEmpty()) {
            return SkillResult.error("缺少参数：summary");
        }
        
        TaskItem task = new TaskItem();
        task.setId(UUID.randomUUID().toString().substring(0, 6));
        task.setSummary(summary);
        task.setDescription(description);
        task.setAssignee(assignee);
        task.setStatus("pending");
        task.setCreatedAt(new Date());
        
        tasks.add(task);
        
        log.info("创建任务：{} - {}", task.getId(), summary);
        
        return SkillResult.success("任务创建成功", Map.of(
            "id", task.getId(),
            "summary", summary
        ));
    }
    
    private SkillResult listTasks(Map<String, Object> params) {
        String status = (String) params.get("status");
        
        List<TaskItem> filtered = tasks;
        if (status != null) {
            filtered = tasks.stream()
                .filter(t -> t.getStatus().equals(status))
                .toList();
        }
        
        if (filtered.isEmpty()) {
            return SkillResult.success("暂无任务", Map.of("count", 0));
        }
        
        StringBuilder sb = new StringBuilder("任务列表:\n\n");
        for (TaskItem task : filtered) {
            sb.append(task.getStatus().equals("completed") ? "✅" : "⏳")
              .append(" ").append(task.getSummary())
              .append(" (ID: ").append(task.getId()).append(")");
            if (task.getAssignee() != null) {
                sb.append(" - @").append(task.getAssignee());
            }
            sb.append("\n");
        }
        
        return SkillResult.success(sb.toString(), Map.of(
            "count", filtered.size(),
            "tasks", filtered
        ));
    }
    
    private SkillResult updateTask(Map<String, Object> params) {
        String taskId = (String) params.get("task_id");
        
        if (taskId == null) {
            return SkillResult.error("缺少参数：task_id");
        }
        
        for (TaskItem task : tasks) {
            if (task.getId().equals(taskId)) {
                if (params.containsKey("summary")) {
                    task.setSummary((String) params.get("summary"));
                }
                if (params.containsKey("description")) {
                    task.setDescription((String) params.get("description"));
                }
                if (params.containsKey("assignee")) {
                    task.setAssignee((String) params.get("assignee"));
                }
                
                log.info("更新任务：{}", taskId);
                return SkillResult.success("任务已更新");
            }
        }
        
        return SkillResult.error("未找到任务：" + taskId);
    }
    
    private SkillResult completeTask(Map<String, Object> params) {
        String taskId = (String) params.get("task_id");
        
        if (taskId == null) {
            return SkillResult.error("缺少参数：task_id");
        }
        
        for (TaskItem task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setStatus("completed");
                task.setCompletedAt(new Date());
                
                log.info("完成任务：{}", taskId);
                return SkillResult.success("任务已完成");
            }
        }
        
        return SkillResult.error("未找到任务：" + taskId);
    }
    
    private static class TaskItem {
        private String id;
        private String summary;
        private String description;
        private String assignee;
        private String status; // pending, completed
        private Date createdAt;
        private Date completedAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        public Date getCompletedAt() { return completedAt; }
        public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
    }
}
