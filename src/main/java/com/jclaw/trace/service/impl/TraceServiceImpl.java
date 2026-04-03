package com.jclaw.trace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.entity.CallRelationship;
import com.jclaw.trace.mapper.CodeUnitMapper;
import com.jclaw.trace.mapper.CallRelationshipMapper;
import com.jclaw.trace.service.TraceService;
import com.jclaw.trace.service.ImpactAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代码追溯服务实现
 */
@Slf4j
@Service
public class TraceServiceImpl implements TraceService {

    @Autowired
    private CodeUnitMapper codeUnitMapper;

    @Autowired
    private CallRelationshipMapper callRelationshipMapper;

    @Override
    public CodeUnit parseCodeFile(String filePath) {
        log.info("解析代码文件：{}", filePath);
        // TODO: 集成 AST 解析器
        return CodeUnit.builder()
            .filePath(filePath)
            .unitType("class")
            .unitName("ExampleClass")
            .build();
    }

    @Override
    public List<CallRelationship> getCallChain(String codeUnitId) {
        // 递归查询调用链
        List<CallRelationship> result = new ArrayList<>();
        findCallChain(codeUnitId, result);
        return result;
    }

    private void findCallChain(String codeUnitId, List<CallRelationship> result) {
        List<CallRelationship> calls = callRelationshipMapper.selectList(
            new QueryWrapper<CallRelationship>().eq("caller_id", codeUnitId)
        );
        
        for (CallRelationship call : calls) {
            result.add(call);
            findCallChain(call.getCalleeId(), result);
        }
    }

    @Override
    public ImpactAnalysis analyzeImpact(String codeUnitId) {
        List<CallRelationship> callChain = getCallChain(codeUnitId);
        
        return ImpactAnalysis.builder()
            .codeUnitId(codeUnitId)
            .affectedNodes(new ArrayList<>())
            .relations(callChain)
            .statistics(Map.of(
                "totalCalls", callChain.size(),
                "riskLevel", callChain.size() > 10 ? "HIGH" : "LOW"
            ))
            .riskScore(calculateRisk(callChain.size()))
            .build();
    }

    @Override
    public CodeUnit getCodeUnit(String id) {
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
