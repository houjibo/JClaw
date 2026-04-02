package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpCommand 单元测试
 */
@DisplayName("HttpCommand 测试")
class HttpCommandTest {
    
    private HttpCommand httpCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        httpCommand = new HttpCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("http", httpCommand.getName());
        assertEquals("HTTP API 测试", httpCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, httpCommand.getCategory());
        assertTrue(httpCommand.getAliases().contains("api"));
        assertTrue(httpCommand.getAliases().contains("request"));
    }
    
    @Test
    @DisplayName("空参数显示帮助")
    void testEmptyArgs() {
        CommandResult result = httpCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("HTTP"));
    }
    
    @Test
    @DisplayName("缺少 URL 报错")
    void testMissingUrl() {
        CommandResult result = httpCommand.execute("-X POST", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertTrue(result.getMessage().contains("URL"));
    }
    
    @Test
    @DisplayName("无效 URL 报错")
    void testInvalidUrl() {
        CommandResult result = httpCommand.execute("not-a-valid-url", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = httpCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("http"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("HTTP"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(httpCommand.getAliases().contains("api"));
        assertTrue(httpCommand.getAliases().contains("request"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
