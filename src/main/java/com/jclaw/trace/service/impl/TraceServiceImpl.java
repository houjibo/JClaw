package com.jclaw.trace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.entity.CallRelationship;
import com.jclaw.trace.mapper.CodeUnitMapper;
import com.jclaw.trace.mapper.CallRelationshipMapper;
import com.jclaw.trace.service.TraceService;
import com.jclaw.trace.service.ImpactAnalysis;
import com.jclaw.trace.service.impl.AstParserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 代码追溯服务实现
 */
@Service
@Slf4j
public class TraceServiceImpl implements TraceService {

    @Autowired
    private CodeUnitMapper codeUnitMapper;

    @Autowired
    private CallRelationshipMapper callRelationshipMapper;
    
    @Autowired
    private AstParserServiceImpl astParserService;

    @Override
    public CodeUnit parseCodeFile(String filePath) {
        log.info("解析代码文件：{}", filePath);
        
        // 使用 JavaParser AST 解析器
        if (filePath.endsWith(".java")) {
            return astParserService.parseJavaFile(filePath);
        } else {
            log.warn("不支持的文件类型：{}", filePath);
            return null;
        }
    }

    @Override
    @Cacheable(value = "callChains", key = "#codeUnitId", unless = "#result == null or #result.isEmpty()")
    public List<CallRelationship> getCallChain(String codeUnitId) {
        log.debug("查询调用链：{} (从数据库)", codeUnitId);
        // 递归查询调用链
        List<CallRelationship> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        findCallChain(codeUnitId, result, visited);
        return result;
    }

    private void findCallChain(String codeUnitId, List<CallRelationship> result, Set<String> visited) {
        // 防止循环调用
        if (visited.contains(codeUnitId)) {
            return;
        }
        visited.add(codeUnitId);
        
        List<CallRelationship> calls = callRelationshipMapper.selectList(
            new QueryWrapper<CallRelationship>().eq("caller_id", codeUnitId)
        );
        
        for (CallRelationship call : calls) {
            result.add(call);
            findCallChain(call.getCalleeId(), result, visited);
        }
    }

    @Override
    public ImpactAnalysis analyzeImpact(String codeUnitId) {
        List<CallRelationship> callChain = getCallChain(codeUnitId);
        
        return ImpactAnalysis.builder()
            .codeUnitId(codeUnitId)
            .affectedNodes(new ArrayList<>())
            .relations(new ArrayList<>())
            .statistics(Map.of(
                "totalCalls", callChain.size(),
                "riskLevel", callChain.size() > 10 ? "HIGH" : "LOW"
            ))
            .riskScore(calculateRisk(callChain.size()))
            .build();
    }

    @Override
    @Cacheable(value = "codeUnits", key = "#id", unless = "#result == null")
    public CodeUnit getCodeUnit(String id) {
        log.debug("查询代码单元：{} (从数据库)", id);
        return codeUnitMapper.selectById(id);
    }

    @Override
    public List<CodeUnit> listCodeUnits(int page, int size) {
        return codeUnitMapper.selectList(
            new QueryWrapper<CodeUnit>().last("LIMIT " + ((page - 1) * size) + ", " + size)
        );
    }

    private double calculateRisk(int callCount) {
        if (callCount > 20) return 90.0;
        if (callCount > 10) return 70.0;
        if (callCount > 5) return 50.0;
        return 30.0;
    }
}
