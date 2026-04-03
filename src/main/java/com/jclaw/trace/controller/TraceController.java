package com.jclaw.trace.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.trace.entity.CodeUnit;
import com.jclaw.trace.entity.CallRelationship;
import com.jclaw.trace.service.TraceService;
import com.jclaw.trace.service.ImpactAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代码追溯 REST API 控制器
 */
@RestController
@RequestMapping("/api/trace")
public class TraceController {

    private static final Logger log = LoggerFactory.getLogger(TraceController.class);

    @Autowired
    private TraceService traceService;

    /**
     * 解析代码文件
     */
    @PostMapping("/parse")
    public Result<CodeUnit> parseCodeFile(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        CodeUnit codeUnit = traceService.parseCodeFile(filePath);
        return Result.success(codeUnit);
    }

    /**
     * 查询调用链
     */
    @GetMapping("/callchain/{id}")
    public Result<List<CallRelationship>> getCallChain(@PathVariable String id) {
        List<CallRelationship> chain = traceService.getCallChain(id);
        return Result.success(chain);
    }

    /**
     * 影响分析
     */
    @PostMapping("/impact/{id}")
    public Result<ImpactAnalysis> analyzeImpact(@PathVariable String id) {
        ImpactAnalysis analysis = traceService.analyzeImpact(id);
        return Result.success(analysis);
    }

    /**
     * 获取代码单元详情
     */
    @GetMapping("/code-units/{id}")
    public Result<CodeUnit> getCodeUnit(@PathVariable String id) {
        CodeUnit codeUnit = traceService.getCodeUnit(id);
        if (codeUnit == null) {
            return Result.error("代码单元不存在");
        }
        return Result.success(codeUnit);
    }

    /**
     * 查询代码单元列表
     */
    @GetMapping("/code-units")
    public Result<List<CodeUnit>> listCodeUnits(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<CodeUnit> codeUnits = traceService.listCodeUnits(page, size);
        return Result.success(codeUnits);
    }
}
