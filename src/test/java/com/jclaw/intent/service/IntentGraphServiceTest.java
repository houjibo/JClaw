package com.jclaw.intent.service;

import com.jclaw.intent.service.IntentGraphService.GraphQueryResult;
import com.jclaw.intent.service.IntentGraphService.IntentNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 意图图谱服务测试
 */
@DisplayName("意图图谱服务测试")
class IntentGraphServiceTest {
    
    private IntentGraphService intentGraphService;
    
    @BeforeEach
    void setUp() {
        intentGraphService = new IntentGraphService();
    }
    
    @Test
    @DisplayName("测试创建意图节点")
    void testCreateIntentNode() {
        boolean success = intentGraphService.createIntentNode(
            "intent-001",
            "feature",
            "用户管理",
            "实现用户注册、登录、权限管理功能"
        );
        
        assertTrue(success);
    }
    
    @Test
    @DisplayName("测试创建需求节点")
    void testCreateRequirementNode() {
        boolean success = intentGraphService.createRequirementNode(
            "req-001",
            "用户登录功能",
            "P0"
        );
        
        assertTrue(success);
    }
    
    @Test
    @DisplayName("测试创建代码单元节点")
    void testCreateCodeUnitNode() {
        boolean success = intentGraphService.createCodeUnitNode(
            "code-001",
            "/src/main/java/com/jclaw/user/UserService.java",
            "class",
            "UserService"
        );
        
        assertTrue(success);
    }
    
    @Test
    @DisplayName("测试创建关系")
    void testCreateRelation() {
        boolean success = intentGraphService.createRelation(
            "intent-001",
            "req-001",
            "DERIVES"
        );
        
        assertTrue(success);
    }
    
    @Test
    @DisplayName("测试查询意图链路")
    void testQueryIntentChain() {
        GraphQueryResult result = intentGraphService.queryIntentChain("intent-001");
        
        assertNotNull(result);
        assertNotNull(result.getNodes());
        assertNotNull(result.getRelations());
        assertTrue(result.getQuery().contains("intent-001"));
    }
    
    @Test
    @DisplayName("测试查询需求实现链路")
    void testQueryRequirementImplementation() {
        GraphQueryResult result = intentGraphService.queryRequirementImplementation("req-001");
        
        assertNotNull(result);
        assertNotNull(result.getNodes());
        assertNotNull(result.getRelations());
        assertTrue(result.getQuery().contains("req-001"));
    }
    
    @Test
    @DisplayName("测试变更影响分析")
    void testAnalyzeImpact() {
        GraphQueryResult result = intentGraphService.analyzeImpact("code-001");
        
        assertNotNull(result);
        assertNotNull(result.getNodes());
        assertNotNull(result.getRelations());
        assertTrue(result.getQuery().contains("code-001"));
    }
    
    @Test
    @DisplayName("测试查找关联意图")
    void testFindRelatedIntents() {
        List<IntentNode> nodes = intentGraphService.findRelatedIntents("intent-001", 3);
        
        assertNotNull(nodes);
        assertTrue(nodes.isEmpty()); // FalkorDB 未配置，返回空列表
    }
    
    @Test
    @DisplayName("测试查找未实现的意图")
    void testFindUnimplementedIntents() {
        List<IntentNode> nodes = intentGraphService.findUnimplementedIntents();
        
        assertNotNull(nodes);
        assertTrue(nodes.isEmpty()); // FalkorDB 未配置，返回空列表
    }
    
    @Test
    @DisplayName("测试获取图谱统计")
    void testGetGraphStats() {
        Map<String, Long> stats = intentGraphService.getGraphStats();
        
        assertNotNull(stats);
        assertEquals(4, stats.size());
        assertEquals(Long.valueOf(0), stats.get("Intent"));
        assertEquals(Long.valueOf(0), stats.get("Requirement"));
    }
    
    @Test
    @DisplayName("测试导出图谱 JSON")
    void testExportGraphJson() {
        Map<String, Object> graphJson = intentGraphService.exportGraphJson();
        
        assertNotNull(graphJson);
        assertTrue(graphJson.containsKey("nodes"));
        assertTrue(graphJson.containsKey("links"));
        assertTrue(graphJson.containsKey("metadata"));
    }
}
