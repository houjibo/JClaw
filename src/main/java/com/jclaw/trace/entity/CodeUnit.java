package com.jclaw.trace.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 代码单元实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeUnit {
    
    /** 代码单元 ID */
    private String id;
    
    /** 文件路径 */
    private String filePath;
    
    /** 单元类型：class, method, function */
    private String unitType;
    
    /** 单元名称 */
    private String unitName;
    
    /** 签名 */
    private String signature;
    
    /** 指标（圈复杂度等） */
    private Map<String, Object> metrics;
    
    /** 创建时间 */
    private Instant createdAt;
    
    /** 更新时间 */
    private Instant updatedAt;
}
