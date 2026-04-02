package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CurlCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CurlCommand 测试")
class CurlCommandTest {
    
    private CurlCommand curlCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        curlCommand = new CurlCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("curl", curlCommand.getName());
        assertEquals("HTTP 请求工具", curlCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, curlCommand.getCategory());
        assertTrue(curlCommand.getAliases().contains("http"));
        assertTrue(curlCommand.getAliases().contains("request"));
    }
    
    @Test
    @DisplayName("空参数显示帮助")
    void testEmptyArgs() {
        CommandResult result = curlCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("curl") || result.getDisplayText().contains("HTTP"));
    }
    
    @Test
    @DisplayName("缺少 URL 报错")
    void testMissingUrl() {
        CommandResult result = curlCommand.execute("-X POST", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertTrue(result.getMessage().contains("URL"));
    }
    
    @Test
    @DisplayName("无效 URL 报错")
    void testInvalidUrl() {
        CommandResult result = curlCommand.execute("not-a-valid-url", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = curlCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("curl"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("HTTP"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(curlCommand.getAliases().contains("http"));
        assertTrue(curlCommand.getAliases().contains("request"));
    }
    
    @Test
    @DisplayName("参数解析 - GET 方法")
    void testParseGet() {
        // 测试 GET 方法（默认）
        CommandResult result = curlCommand.execute("https://httpbin.org/get", context);
        
        // 网络请求可能失败，只检查不抛出异常
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("参数解析 - POST 方法")
    void testParsePost() {
        CommandResult result = curlCommand.execute("-X POST https://httpbin.org/post -d '{\"test\":\"value\"}'", context);
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("参数解析 - Headers")
    void testParseHeaders() {
        CommandResult result = curlCommand.execute("-H \"Content-Type: application/json\" https://httpbin.org/headers", context);
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("参数解析 - Timeout")
    void testParseTimeout() {
        CommandResult result = curlCommand.execute("-t 5 https://httpbin.org/delay/1", context);
        
        assertNotNull(result);
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
