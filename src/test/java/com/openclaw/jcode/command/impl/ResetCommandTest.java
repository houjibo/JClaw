package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResetCommand 单元测试
 */
@DisplayName("ResetCommand 测试")
class ResetCommandTest {
    
    private ResetCommand resetCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        resetCommand = new ResetCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("reset", resetCommand.getName());
        assertEquals("Git 重置操作", resetCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, resetCommand.getCategory());
        assertTrue(resetCommand.getAliases().contains("rst"));
    }
    
    @Test
    @DisplayName("Soft 重置")
    void testSoftReset() {
        CommandResult result = resetCommand.execute("--soft HEAD~1", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("--soft"));
    }
    
    @Test
    @DisplayName("Mixed 重置")
    void testMixedReset() {
        CommandResult result = resetCommand.execute("--mixed HEAD~1", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("Hard 重置")
    void testHardReset() {
        CommandResult result = resetCommand.execute("--hard HEAD~1", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("--hard"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = resetCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("reset"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
