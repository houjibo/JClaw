package com.jclaw.agent.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Subagent 实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subagent {
    
    /** Subagent ID */
    private String id;
    
    /** 父 Agent ID */
    private String parentAgentId;
    
    /** 角色类型 */
    private String role; // pm-qa, architect, fullstack, devops, analyst
    
    /** 任务描述 */
    private String task;
    
    /** 状态 */
    private String status; // pending, running, completed, failed
    
    /** 上下文 */
    private Map<String, Object> context;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 完成时间 */
    private Instant completedAt;
    
    /** 结果输出 */
    private String output;
}
