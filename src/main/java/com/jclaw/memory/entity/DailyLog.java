package com.jclaw.memory.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Map;

/**
 * 每日日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {
    
    /** 日志 ID */
    private String id;
    
    /** 日期 */
    private LocalDate date;
    
    /** 日志内容（JSON） */
    private Map<String, Object> content;
    
    /** 标签 */
    private String[] tags;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
