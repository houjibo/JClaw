package com.jclaw.trace.service;

import lombok.Data;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 影响分析结果
 */
@Data
@Builder
public class ImpactAnalysis {
    
    /** 代码单元 ID */
    private String codeUnitId;
    
    /** 受影响的节点 */
    private List<Object> affectedNodes;
    
    /** 关系列表 */
    private List<Object> relations;
    
    /** 统计信息 */
    private Map<String, Object> statistics;
    
    /** 风险评分 (0-100) */
    private double riskScore;
}
