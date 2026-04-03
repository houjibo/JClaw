package com.jclaw.test.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.test.service.TestRecommenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试推荐 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestRecommenderController {

    @Autowired
    private TestRecommenderService testService;

    /**
     * 推荐测试
     */
    @PostMapping("/recommend")
    public Result<List<Map<String, Object>>> recommendTests(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        List<Map<String, Object>> recommendations = testService.recommendTests(filePath);
        return Result.success(recommendations);
    }

    /**
     * 分析覆盖率
     */
    @GetMapping("/coverage")
    public Result<Map<String, Object>> analyzeCoverage(
        @RequestParam String filePath
    ) {
        Map<String, Object> coverage = testService.analyzeCoverage(filePath);
        return Result.success(coverage);
    }

    /**
     * 生成测试建议
     */
    @GetMapping("/suggestions")
    public Result<List<Map<String, Object>>> generateTestSuggestions(
        @RequestParam String codeUnitId
    ) {
        List<Map<String, Object>> suggestions = testService.generateTestSuggestions(codeUnitId);
        return Result.success(suggestions);
    }
}
