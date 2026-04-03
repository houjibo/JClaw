package com.jclaw.impact.service.impl;

import com.jclaw.trace.service.ImpactAnalysis;
import com.jclaw.trace.service.TraceService;
import com.jclaw.impact.service.ImpactAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 影响分析服务实现
 */
@Service
public class ImpactAnalysisServiceImpl implements ImpactAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ImpactAnalysisServiceImpl.class);
    @Autowired
    private TraceService traceService;

    @Override
    public ImpactAnalysis analyzeChange(String filePath) {
        log.info("分析代码变更影响：{}", filePath);
        // TODO: 检测代码变更
        return traceService.analyzeImpact("temp_id");
    }

    @Override
    public ImpactAnalysis analyzeImpact(String codeUnitId) {
        return traceService.analyzeImpact(codeUnitId);
    }

    @Override
    public double calculateRisk(String codeUnitId) {
        ImpactAnalysis analysis = traceService.analyzeImpact(codeUnitId);
        return analysis.getRiskScore();
    }
}
