package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import com.openclaw.jcode.ui.TerminalUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TerminalCommand 单元测试
 */
@DisplayName("TerminalCommand 测试")
class TerminalCommandTest {
    
    private TerminalCommand terminalCommand;
    private TerminalUI terminalUI;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        try {
            terminalUI = new TerminalUI();
        } catch (Exception e) {
            terminalUI = null; // 非交互式环境可能失败
        }
        terminalCommand = new TerminalCommand(terminalUI);
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("terminal", terminalCommand.getName());
        assertEquals("终端 UI 管理", terminalCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, terminalCommand.getCategory());
        assertTrue(terminalCommand.getAliases().contains("ui"));
        assertTrue(terminalCommand.getAliases().contains("term"));
    }
    
    @Test
    @DisplayName("显示终端信息")
    void testShowTerminalInfo() {
        CommandResult result = terminalCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("终端 UI"));
    }
    
    @Test
    @DisplayName("清屏功能")
    void testClearScreen() {
        CommandResult result = terminalCommand.execute("clear", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("清屏"));
    }
    
    @Test
    @DisplayName("测试终端 UI")
    void testTestTerminalUI() {
        CommandResult result = terminalCommand.execute("test", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("测试"));
    }
    
    @Test
    @DisplayName("演示功能")
    void testShowDemo() {
        CommandResult result = terminalCommand.execute("demo", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("演示"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = terminalCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("terminal"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(terminalCommand.getAliases().contains("ui"));
        assertTrue(terminalCommand.getAliases().contains("term"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
