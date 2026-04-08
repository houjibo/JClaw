package com.jclaw.memory.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 记忆实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Memory {
    
    /** 记忆 ID */
    private String id;
    
    /** 记忆类型：long_term, daily_log, knowledge */
    private String type;
    
    /** 记忆标题 */
    private String title;
    
    /** 记忆内容（JSON 字符串） */
    private String content;
    
    /** 标签 */
    private String[] tags;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
