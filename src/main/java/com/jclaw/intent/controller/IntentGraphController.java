package com.jclaw.intent.controller;

import com.jclaw.intent.service.IntentGraphService;
import com.jclaw.intent.service.IntentGraphService.GraphQueryResult;
import com.jclaw.intent.service.IntentGraphService.IntentNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意图图谱 REST API 控制器
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/intent/graph")
@RequiredArgsConstructor
public class IntentGraphController {
    
    private final IntentGraphService intentGraphService;
    
    /**
     * 创建意图节点
     * POST /api/intent/graph/nodes/intent
     */
    @PostMapping("/nodes/intent")
    public ResponseEntity<Map<String, Object>> createIntentNode(
            @RequestParam String id,
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        
        boolean success = intentGraphService.createIntentNode(id, type, name, description);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("nodeId", id);
        response.put("nodeType", "Intent");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建需求节点
     * POST /api/intent/graph/nodes/requirement
     */
    @PostMapping("/nodes/requirement")
    public ResponseEntity<Map<String, Object>> createRequirementNode(
            @RequestParam String id,
            @RequestParam String description,
            @RequestParam(required = false, defaultValue = "P0") String priority) {
        
        boolean success = intentGraphService.createRequirementNode(id, description, priority);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("nodeId", id);
        response.put("nodeType", "Requirement");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建代码单元节点
     * POST /api/intent/graph/nodes/codeunit
     */
    @PostMapping("/nodes/codeunit")
    public ResponseEntity<Map<String, Object>> createCodeUnitNode(
            @RequestParam String id,
            @RequestParam String filePath,
            @RequestParam String unitType,
            @RequestParam String unitName) {
        
        boolean success = intentGraphService.createCodeUnitNode(id, filePath, unitType, unitName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("nodeId", id);
        response.put("nodeType", "CodeUnit");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建关系
     * POST /api/intent/graph/relations
     */
    @PostMapping("/relations")
    public ResponseEntity<Map<String, Object>> createRelation(
            @RequestParam String fromId,
            @RequestParam String toId,
            @RequestParam String type) {
        
        boolean success = intentGraphService.createRelation(fromId, toId, type);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("from", fromId);
        response.put("to", toId);
        response.put("relationType", type);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询意图链路
     * GET /api/intent/graph/query/intent/{intentId}
     */
    @GetMapping("/query/intent/{intentId}")
    public ResponseEntity<GraphQueryResult> queryIntentChain(@PathVariable String intentId) {
        GraphQueryResult result = intentGraphService.queryIntentChain(intentId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 查询需求实现链路
     * GET /api/intent/graph/query/requirement/{requirementId}
     */
    @GetMapping("/query/requirement/{requirementId}")
    public ResponseEntity<GraphQueryResult> queryRequirementImplementation(@PathVariable String requirementId) {
        GraphQueryResult result = intentGraphService.queryRequirementImplementation(requirementId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 变更影响分析
     * GET /api/intent/graph/impact/{codeUnitId}
     */
    @GetMapping("/impact/{codeUnitId}")
    public ResponseEntity<GraphQueryResult> analyzeImpact(@PathVariable String codeUnitId) {
        GraphQueryResult result = intentGraphService.analyzeImpact(codeUnitId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 查找关联意图
     * GET /api/intent/graph/related/{intentId}?depth=3
     */
    @GetMapping("/related/{intentId}")
    public ResponseEntity<List<IntentNode>> findRelatedIntents(
            @PathVariable String intentId,
            @RequestParam(defaultValue = "3") int depth) {
        
        List<IntentNode> nodes = intentGraphService.findRelatedIntents(intentId, depth);
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 查找未实现的意图
     * GET /api/intent/graph/unimplemented
     */
    @GetMapping("/unimplemented")
    public ResponseEntity<List<IntentNode>> findUnimplementedIntents() {
        List<IntentNode> nodes = intentGraphService.findUnimplementedIntents();
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 获取图谱统计
     * GET /api/intent/graph/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getGraphStats() {
        Map<String, Long> stats = intentGraphService.getGraphStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 导出图谱 JSON (用于 3D 可视化)
     * GET /api/intent/graph/export
     */
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportGraphJson() {
        Map<String, Object> graphJson = intentGraphService.exportGraphJson();
        return ResponseEntity.ok(graphJson);
    }
}
