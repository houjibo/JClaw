package com.jclaw.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务数据传输对象
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    
    /**
     * 任务 ID
     */
    private String id;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务状态（pending, running, completed, stopped, failed）
     */
    private String status;
    
    /**
     * 负责人
     */
    private String assignee;
    
    /**
     * 优先级（low, medium, high, critical）
     */
    private String priority;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
