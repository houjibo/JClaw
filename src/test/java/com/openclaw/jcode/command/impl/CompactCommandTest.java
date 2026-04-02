package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CompactCommand 单元测试
 */
@DisplayName("CompactCommand 测试")
class CompactCommandTest {
    
    private CompactCommand compactCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        compactCommand = new CompactCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("compact", compactCommand.getName());
        assertEquals("上下文压缩", compactCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, compactCommand.getCategory());
        assertTrue(compactCommand.getAliases().contains("compress"));
        assertTrue(compactCommand.getAliases().contains("summarize"));
    }
    
    @Test
    @DisplayName("压缩上下文")
    void testCompactContext() {
        CommandResult result = compactCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("压缩"));
    }
    
    @Test
    @DisplayName("压缩会话历史")
    void testCompactHistory() {
        CommandResult result = compactCommand.execute("history session-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("压缩"));
    }
    
    @Test
    @DisplayName("显示压缩信息")
    void testShowCompactInfo() {
        CommandResult result = compactCommand.execute("info", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("压缩"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = compactCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("compact"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
