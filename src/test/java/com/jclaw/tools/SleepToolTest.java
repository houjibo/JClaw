package com.jclaw.tools;

import com.jclaw.core.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sleep 工具测试
 */
@DisplayName("Sleep 工具测试")
class SleepToolTest {
    
    private SleepTool sleepTool;
    
    @BeforeEach
    void setUp() {
        sleepTool = new SleepTool();
    }
    
    @Test
    @DisplayName("测试延时 - 毫秒")
    void testSleepMilliseconds() {
        Map<String, Object> params = new HashMap<>();
        params.put("duration", 100);
        params.put("unit", "ms");
        
        long start = System.currentTimeMillis();
        ToolResult result = sleepTool.execute(params, null);
        long elapsed = System.currentTimeMillis() - start;
        
        assertTrue(result.isSuccess());
        assertTrue(elapsed >= 100, "延时时间应该至少 100ms");
    }
    
    @Test
    @DisplayName("测试延时 - 秒")
    void testSleepSeconds() {
        Map<String, Object> params = new HashMap<>();
        params.put("duration", 1);
        params.put("unit", "s");
        
        long start = System.currentTimeMillis();
        ToolResult result = sleepTool.execute(params, null);
        long elapsed = System.currentTimeMillis() - start;
        
        assertTrue(result.isSuccess());
        assertTrue(elapsed >= 1000, "延时时间应该至少 1000ms");
    }
    
    @Test
    @DisplayName("测试延时 - 无效参数")
    void testSleepInvalidDuration() {
        Map<String, Object> params = new HashMap<>();
        params.put("duration", -100);
        
        ToolResult result = sleepTool.execute(params, null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("正整数"));
    }
    
    @Test
    @DisplayName("测试延时 - 超过最大限制")
    void testSleepTooLong() {
        Map<String, Object> params = new HashMap<>();
        params.put("duration", 10);
        params.put("unit", "m"); // 10 分钟，超过 5 分钟限制
        
        ToolResult result = sleepTool.execute(params, null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("5 分钟"));
    }
    
    @Test
    @DisplayName("测试验证")
    void testValidate() {
        Map<String, Object> validParams = new HashMap<>();
        validParams.put("duration", 100);
        
        Map<String, Object> invalidParams = new HashMap<>();
        
        assertTrue(sleepTool.validate(validParams));
        assertFalse(sleepTool.validate(invalidParams));
    }
    
    @Test
    @DisplayName("测试帮助信息")
    void testGetHelp() {
        String help = sleepTool.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("sleep"));
        assertTrue(help.contains("duration"));
    }
}
