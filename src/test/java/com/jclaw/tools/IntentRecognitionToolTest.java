package com.jclaw.tools;

import com.jclaw.core.Tool;
import com.jclaw.core.ToolCategory;
import com.jclaw.core.ToolContext;
import com.jclaw.core.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * IntentRecognitionTool 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("IntentRecognitionTool 测试")
class IntentRecognitionToolTest {
    
    private IntentRecognitionTool tool;
    private ToolContext context;
    
    @BeforeEach
    void setUp() {
        tool = new IntentRecognitionTool();
        context = ToolContext.defaultContext();
    }
    
    @Test
    @DisplayName("工具基本信息")
    void testToolInfo() {
        assertEquals("intent_recognition", tool.getName());
        assertTrue(tool.getDescription().contains("意图识别"));
        assertEquals(ToolCategory.SYSTEM, tool.getCategory());
    }
    
    @Test
    @DisplayName("识别代码生成意图")
    void testCodeGenerationIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("创建一个新的用户登录功能");
        
        assertEquals(IntentRecognitionTool.IntentType.CODE_GENERATION, result.getType());
        assertTrue(result.getConfidence() > 0);
        assertTrue(result.getDescription().contains("代码生成"));
    }
    
    @Test
    @DisplayName("识别 Bug 修复意图")
    void testBugFixIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("修复登录页面的 bug");
        
        assertEquals(IntentRecognitionTool.IntentType.BUG_FIX, result.getType());
        assertTrue(result.getConfidence() > 0);
    }
    
    @Test
    @DisplayName("识别代码审查意图")
    void testCodeReviewIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("帮我审查这段代码");
        
        assertEquals(IntentRecognitionTool.IntentType.CODE_REVIEW, result.getType());
        assertTrue(result.getConfidence() > 0);
    }
    
    @Test
    @DisplayName("识别测试生成意图")
    void testTestGenerationIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("为这个函数编写单元测试代码");
        
        assertEquals(IntentRecognitionTool.IntentType.TEST_GENERATION, result.getType());
        assertTrue(result.getConfidence() > 0);
    }
    
    @Test
    @DisplayName("识别重构意图")
    void testRefactoringIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("重构这个模块的代码结构");
        
        assertEquals(IntentRecognitionTool.IntentType.REFACTORING, result.getType());
        assertTrue(result.getConfidence() > 0);
    }
    
    @Test
    @DisplayName("识别性能优化意图")
    void testOptimizationIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("优化这段代码的性能");
        
        assertEquals(IntentRecognitionTool.IntentType.OPTIMIZATION, result.getType());
        assertTrue(result.getConfidence() > 0);
    }
    
    @Test
    @DisplayName("识别文档意图")
    void testDocumentationIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("为这个类编写文档注释");
        
        assertEquals(IntentRecognitionTool.IntentType.DOCUMENTATION, result.getType());
        assertTrue(result.getConfidence() > 0);
    }
    
    @Test
    @DisplayName("未知意图")
    void testUnknownIntent() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("今天天气怎么样");
        
        assertEquals(IntentRecognitionTool.IntentType.UNKNOWN, result.getType());
    }
    
    @Test
    @DisplayName("IntentResult toMap")
    void testToMap() {
        IntentRecognitionTool.IntentResult result = tool.recognizeIntent("创建新功能");
        
        java.util.Map<String, Object> map = result.toMap();
        
        assertTrue(map.containsKey("type"));
        assertTrue(map.containsKey("typeDisplay"));
        assertTrue(map.containsKey("confidence"));
        assertTrue(map.containsKey("description"));
        assertTrue(map.containsKey("keywords"));
    }
    
    @Test
    @DisplayName("执行工具")
    void testExecute() {
        Map<String, Object> params = new HashMap<>();
        params.put("input", "修复这个 bug");
        
        ToolResult result = tool.execute(params, context);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    @DisplayName("执行工具（空输入）")
    void testExecuteEmptyInput() {
        Map<String, Object> params = new HashMap<>();
        params.put("input", "");
        
        ToolResult result = tool.execute(params, context);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }
}
