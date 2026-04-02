package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ClearCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ClearCommand 测试")
class ClearCommandTest {
    
    private ClearCommand clearCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        clearCommand = new ClearCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("clear", clearCommand.getName());
        assertEquals("清除上下文和消息", clearCommand.getDescription());
        assertEquals(Command.CommandCategory.SESSION, clearCommand.getCategory());
        assertTrue(clearCommand.getAliases().contains("clean"));
        assertTrue(clearCommand.getAliases().contains("reset"));
    }
    
    @Test
    @DisplayName("清除上下文")
    void testClearContext() {
        CommandResult result = clearCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("清除"));
    }
    
    @Test
    @DisplayName("清除所有")
    void testClearAll() {
        CommandResult result = clearCommand.execute("all", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("所有"));
    }
    
    @Test
    @DisplayName("清除消息")
    void testClearMessages() {
        CommandResult result = clearCommand.execute("messages", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("消息"));
    }
    
    @Test
    @DisplayName("清除历史")
    void testClearHistory() {
        CommandResult result = clearCommand.execute("history", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("历史"));
    }
    
    @Test
    @DisplayName("清除缓存")
    void testClearCache() {
        CommandResult result = clearCommand.execute("cache", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("缓存"));
    }
    
    @Test
    @DisplayName("需要确认")
    void testRequiresConfirmation() {
        assertTrue(clearCommand.isRequiresConfirmation());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = clearCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("clear"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(clearCommand.getAliases().contains("clean"));
        assertTrue(clearCommand.getAliases().contains("reset"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
