package com.jclaw.trace.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

/**
 * 调用关系实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRelationship {
    
    /** 关系 ID */
    private String id;
    
    /** 调用方 ID */
    private String callerId;
    
    /** 被调用方 ID */
    private String calleeId;
    
    /** 调用类型 */
    private String callType;
    
    /** 调用次数 */
    private Integer callCount;
    
    /** 创建时间 */
    private Instant createdAt;
}
