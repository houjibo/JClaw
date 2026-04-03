package com.jclaw.impact.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.trace.service.ImpactAnalysis;
import com.jclaw.impact.service.ImpactAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 影响分析 REST API 控制器
 */
@RestController
@RequestMapping("/api/impact")
public class ImpactAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(ImpactAnalysisController.class);
    @Autowired
    private ImpactAnalysisService impactService;

    /**
     * 分析代码变更影响
     */
    @PostMapping("/analyze/{filePath}")
    public Result<ImpactAnalysis> analyzeChange(@PathVariable String filePath) {
        ImpactAnalysis analysis = impactService.analyzeChange(filePath);
        return Result.success(analysis);
    }

    /**
     * 分析代码单元影响
     */
    @PostMapping("/{id}")
    public Result<ImpactAnalysis> analyzeImpact(@PathVariable String id) {
        ImpactAnalysis analysis = impactService.analyzeImpact(id);
        return Result.success(analysis);
    }

    /**
     * 获取风险评分
     */
    @GetMapping("/risk/{id}")
    public Result<Double> getRiskScore(@PathVariable String id) {
        double risk = impactService.calculateRisk(id);
        return Result.success(risk);
    }
}
