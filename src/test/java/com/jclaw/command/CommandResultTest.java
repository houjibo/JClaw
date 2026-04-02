package com.jclaw.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CommandResult 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CommandResult 测试")
class CommandResultTest {
    
    @BeforeEach
    void setUp() {
        // 每个测试前重置
    }
    
    @Test
    @DisplayName("创建成功结果")
    void testSuccess() {
        CommandResult result = CommandResult.success("测试成功");
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertEquals("测试成功", result.getMessage());
        assertTrue(result.getData().isEmpty());
    }
    
    @Test
    @DisplayName("创建成功结果（带数据）")
    void testSuccessWithData() {
        CommandResult result = CommandResult.success("测试成功");
        result.withData("key", "value");
        result.withData("count", 42);
        
        assertEquals(2, result.getData().size());
        assertEquals("value", result.getData().get("key"));
        assertEquals(42, result.getData().get("count"));
    }
    
    @Test
    @DisplayName("创建成功结果（批量数据）")
    void testSuccessWithMapData() {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("name", "test");
        data.put("value", 100);
        
        CommandResult result = CommandResult.success("测试成功");
        result.withData(data);
        
        assertEquals(2, result.getData().size());
        assertEquals("test", result.getData().get("name"));
        assertEquals(100, result.getData().get("value"));
    }
    
    @Test
    @DisplayName("创建错误结果")
    void testError() {
        CommandResult result = CommandResult.error("测试错误");
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertEquals("测试错误", result.getMessage());
    }
    
    @Test
    @DisplayName("创建跳过结果")
    void testSkip() {
        CommandResult result = CommandResult.skip();
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SKIP, result.getType());
        assertEquals("命令已跳过", result.getMessage());
    }
    
    @Test
    @DisplayName("创建需要确认的结果")
    void testConfirmation() {
        CommandResult result = CommandResult.confirmation("确认执行此操作？");
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.CONFIRMATION, result.getType());
        assertEquals("需要确认", result.getMessage());
        assertEquals("确认执行此操作？", result.getConfirmationPrompt());
    }
    
    @Test
    @DisplayName("设置显示文本")
    void testWithDisplayText() {
        String markdown = "## 测试\n\n内容";
        CommandResult result = CommandResult.success("测试");
        result.withDisplayText(markdown);
        
        assertEquals(markdown, result.getDisplayText());
    }
    
    @Test
    @DisplayName("链式调用")
    void testChaining() {
        CommandResult result = CommandResult.success("测试")
                .withData("key", "value")
                .withData("count", 42)
                .withDisplayText("## 显示");
        
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertEquals(2, result.getData().size());
        assertNotNull(result.getDisplayText());
    }
    
    @Test
    @DisplayName("toString 方法")
    void testToString() {
        CommandResult result = CommandResult.success("测试");
        String str = result.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("SUCCESS"));
        assertTrue(str.contains("测试"));
    }
}
