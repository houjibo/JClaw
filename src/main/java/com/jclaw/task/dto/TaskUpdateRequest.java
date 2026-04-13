package com.jclaw.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务更新请求
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 负责人
     */
    private String assignee;
    
    /**
     * 优先级
     */
    private String priority;
}
