package com.jclaw.memory.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 知识实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Knowledge {
    
    /** 知识 ID */
    private String id;
    
    /** 知识标题 */
    private String title;
    
    /** 分类 */
    private String category;
    
    /** 知识内容 */
    private String content;
    
    /** 元数据 */
    private Map<String, Object> metadata;
    
    /** 标签 */
    private String[] tags;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
