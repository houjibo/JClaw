package com.jclaw.tools;

import com.jclaw.core.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Context Manage 工具测试
 */
@DisplayName("Context Manage 工具测试")
class ContextManageToolTest {
    
    private ContextManageTool contextTool;
    
    @BeforeEach
    void setUp() {
        contextTool = new ContextManageTool();
        // 清空上下文
        contextTool.execute(Map.of("action", "clear"), null);
    }
    
    @Test
    @DisplayName("测试设置上下文")
    void testSetContext() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "set");
        params.put("key", "test_key");
        params.put("value", "test_value");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("已设置"));
    }
    
    @Test
    @DisplayName("测试获取上下文")
    void testGetContext() {
        // 先设置
        contextTool.execute(Map.of("action", "set", "key", "test_key", "value", "test_value"), null);
        
        // 再获取
        Map<String, Object> params = new HashMap<>();
        params.put("action", "get");
        params.put("key", "test_key");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
    
    @Test
    @DisplayName("测试获取不存在的上下文")
    void testGetNonExistentContext() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "get");
        params.put("key", "non_existent");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("不存在"));
    }
    
    @Test
    @DisplayName("测试删除上下文")
    void testDeleteContext() {
        // 先设置
        contextTool.execute(Map.of("action", "set", "key", "test_key", "value", "test_value"), null);
        
        // 再删除
        Map<String, Object> params = new HashMap<>();
        params.put("action", "delete");
        params.put("key", "test_key");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertTrue(result.isSuccess());
        
        // 验证已删除
        ToolResult getResult = contextTool.execute(Map.of("action", "get", "key", "test_key"), null);
        assertFalse(getResult.isSuccess());
    }
    
    @Test
    @DisplayName("测试列出上下文")
    void testListContext() {
        // 设置几个变量
        contextTool.execute(Map.of("action", "set", "key", "key1", "value", "value1"), null);
        contextTool.execute(Map.of("action", "set", "key", "key2", "value", "value2"), null);
        
        Map<String, Object> params = new HashMap<>();
        params.put("action", "list");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
    
    @Test
    @DisplayName("测试清空上下文")
    void testClearContext() {
        // 先设置几个变量
        contextTool.execute(Map.of("action", "set", "key", "key1", "value", "value1"), null);
        contextTool.execute(Map.of("action", "set", "key", "key2", "value", "value2"), null);
        
        // 清空
        Map<String, Object> params = new HashMap<>();
        params.put("action", "clear");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("清空"));
    }
    
    @Test
    @DisplayName("测试不支持的操作")
    void testUnsupportedAction() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "unsupported");
        
        ToolResult result = contextTool.execute(params, null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("不支持"));
    }
    
    @Test
    @DisplayName("测试验证")
    void testValidate() {
        Map<String, Object> validParams = new HashMap<>();
        validParams.put("action", "list");
        
        Map<String, Object> invalidParams = new HashMap<>();
        
        assertTrue(contextTool.validate(validParams));
        assertFalse(contextTool.validate(invalidParams));
    }
}
