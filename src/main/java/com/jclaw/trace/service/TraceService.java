package com.jclaw.trace.service;

import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.entity.CallRelationship;

import java.util.List;

/**
 * 代码追溯服务接口
 */
public interface TraceService {
    
    /**
     * 解析代码文件
     */
    CodeUnit parseCodeFile(String filePath);
    
    /**
     * 查询调用链
     */
    List<CallRelationship> getCallChain(String codeUnitId);
    
    /**
     * 影响分析
     */
    ImpactAnalysis analyzeImpact(String codeUnitId);
    
    /**
     * 获取代码单元详情
     */
    CodeUnit getCodeUnit(String id);
    
    /**
     * 查询代码单元列表
     */
    List<CodeUnit> listCodeUnits(int page, int size);
}
