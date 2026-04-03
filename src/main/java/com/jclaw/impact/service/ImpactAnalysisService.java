package com.jclaw.impact.service;

import com.jclaw.trace.service.ImpactAnalysis;

/**
 * 影响分析服务接口
 */
public interface ImpactAnalysisService {
    
    /**
     * 分析代码变更影响
     */
    ImpactAnalysis analyzeChange(String filePath);
    
    /**
     * 分析代码单元影响
     */
    ImpactAnalysis analyzeImpact(String codeUnitId);
    
    /**
     * 计算风险评分
     */
    double calculateRisk(String codeUnitId);
}
