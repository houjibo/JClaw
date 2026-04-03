package com.jclaw.intent.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 意图实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Intent {
    
    /** 意图 ID */
    private String id;
    
    /** 项目 ID */
    private Long projectId;
    
    /** 意图类型 */
    private String type;
    
    /** 意图名称 */
    private String name;
    
    /** 意图描述 */
    private String description;
    
    /** 上下文数据 (JSON) */
    private Map<String, Object> context;
    
    /** 状态 */
    private String status;
    
    /** 优先级 */
    private Integer priority;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
