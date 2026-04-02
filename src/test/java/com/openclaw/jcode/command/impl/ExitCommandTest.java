package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ExitCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ExitCommand 测试")
class ExitCommandTest {
    
    private ExitCommand exitCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        exitCommand = new ExitCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("exit", exitCommand.getName());
        assertEquals("退出应用或会话", exitCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, exitCommand.getCategory());
        assertTrue(exitCommand.getAliases().contains("quit"));
        assertTrue(exitCommand.getAliases().contains("bye"));
        assertTrue(exitCommand.getAliases().contains("q"));
    }
    
    @Test
    @DisplayName("退出应用")
    void testExitApplication() {
        CommandResult result = exitCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("感谢"));
    }
    
    @Test
    @DisplayName("退出会话")
    void testExitSession() {
        CommandResult result = exitCommand.execute("session", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("会话已结束"));
    }
    
    @Test
    @DisplayName("退出应用（显式）")
    void testExitApplicationExplicit() {
        CommandResult result = exitCommand.execute("app", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("需要确认")
    void testRequiresConfirmation() {
        assertTrue(exitCommand.isRequiresConfirmation());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = exitCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("exit"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(exitCommand.getAliases().contains("quit"));
        assertTrue(exitCommand.getAliases().contains("bye"));
        assertTrue(exitCommand.getAliases().contains("q"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
