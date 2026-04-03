package com.jclaw.test.service;

import java.util.List;
import java.util.Map;

/**
 * 测试推荐服务接口
 */
public interface TestRecommenderService {
    
    /**
     * 基于代码变更推荐测试
     */
    List<Map<String, Object>> recommendTests(String filePath);
    
    /**
     * 分析代码覆盖率
     */
    Map<String, Object> analyzeCoverage(String filePath);
    
    /**
     * 生成测试用例建议
     */
    List<Map<String, Object>> generateTestSuggestions(String codeUnitId);
}
