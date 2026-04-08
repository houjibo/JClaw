package com.jclaw.impact.service.impl;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import com.jclaw.trace.service.ImpactAnalysis;
import com.jclaw.trace.service.TraceService;
import com.jclaw.impact.service.ImpactAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 影响分析服务实现
 * 使用 JGit 检测 Git 变更
 */
@Service
@Slf4j
public class ImpactAnalysisServiceImpl implements ImpactAnalysisService {

    @Autowired
    private TraceService traceService;

    @Override
    public ImpactAnalysis analyzeChange(String filePath) {
        log.info("分析代码变更影响：{}", filePath);
        
        // 1. 检测 Git 变更
        List<String> changedCodeUnits = detectGitChanges(filePath);
        
        // 2. 对每个变更代码单元进行影响分析
        List<Object> affectedNodes = new ArrayList<>();
        double totalRisk = 0;
        int count = 0;
        
        for (String codeUnitId : changedCodeUnits) {
            ImpactAnalysis analysis = traceService.analyzeImpact(codeUnitId);
            affectedNodes.addAll(analysis.getAffectedNodes());
            totalRisk += analysis.getRiskScore();
            count++;
        }
        
        // 3. 计算平均风险评分
        double averageRisk = count > 0 ? totalRisk / count : 0;
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("changedFiles", changedCodeUnits.size());
        statistics.put("totalAffectedNodes", affectedNodes.size());
        statistics.put("averageRisk", averageRisk);
        
        ImpactAnalysis combinedAnalysis = ImpactAnalysis.builder()
            .affectedNodes(affectedNodes)
            .riskScore(averageRisk)
            .statistics(statistics)
            .build();
        
        log.info("变更影响分析完成：{} 变更文件，{} 影响节点，平均风险 {}", 
            changedCodeUnits.size(), affectedNodes.size(), averageRisk);
        
        return combinedAnalysis;
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
    
    /**
     * 使用 JGit 检测 Git 变更
     */
    private List<String> detectGitChanges(String repoPath) {
        List<String> changedFiles = new ArrayList<>();
        
        try {
            File gitDir = new File(repoPath, ".git");
            if (!gitDir.exists()) {
                log.warn("不是 Git 仓库：{}", repoPath);
                return changedFiles;
            }
            
            Repository repository = new FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .findGitDir()
                .build();
            
            Git git = new Git(repository);
            Status status = git.status().call();
            
            // 获取所有变更文件
            changedFiles.addAll(status.getChanged());
            changedFiles.addAll(status.getAdded());
            changedFiles.addAll(status.getModified());
            
            log.info("Git 检测到 {} 个变更文件", changedFiles.size());
            
            git.close();
            
        } catch (Exception e) {
            log.error("检测 Git 变更失败：{}", repoPath, e);
        }
        
        return changedFiles;
    }
}
