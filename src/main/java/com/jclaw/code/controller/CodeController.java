package com.jclaw.code.controller;

import com.jclaw.code.dto.*;
import com.jclaw.code.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码工具控制器
 * 
 * @author JClaw
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
public class CodeController {
    
    private final CodeService codeService;
    
    /**
     * 代码解释
     */
    @PostMapping("/explain")
    public ResponseEntity<CodeExplanation> explainCode(
            @RequestParam String file,
            @RequestParam(required = false, defaultValue = "java") String language) {
        CodeExplanation result = codeService.explainCode(file, language);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 代码优化建议
     */
    @PostMapping("/optimize")
    public ResponseEntity<List<CodeOptimization>> optimizeCode(@RequestParam String file) {
        List<CodeOptimization> result = codeService.optimizeCode(file);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 安全漏洞扫描
     */
    @PostMapping("/security")
    public ResponseEntity<SecurityReport> securityScan(@RequestParam String file) {
        SecurityReport result = codeService.securityScan(file);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 自动生成文档
     */
    @PostMapping("/docs")
    public ResponseEntity<CodeDocumentation> generateDocs(@RequestParam String file) {
        CodeDocumentation result = codeService.generateDocs(file);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 项目构建
     */
    @PostMapping("/build")
    public ResponseEntity<BuildResult> buildProject(
            @RequestParam String path,
            @RequestParam(required = false, defaultValue = "maven") String tool) {
        BuildResult result = codeService.buildProject(path, tool);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 调试信息提取
     */
    @PostMapping("/debug")
    public ResponseEntity<DebugInfo> debugCode(
            @RequestParam String file,
            @RequestParam int line) {
        DebugInfo result = codeService.debugCode(file, line);
        return ResponseEntity.ok(result);
    }
}
