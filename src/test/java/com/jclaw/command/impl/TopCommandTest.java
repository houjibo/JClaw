package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TopCommand 单元测试
 */
@DisplayName("TopCommand 测试")
class TopCommandTest {
    
    private TopCommand topCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        topCommand = new TopCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("top", topCommand.getName());
        assertEquals("系统资源监控", topCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, topCommand.getCategory());
        assertTrue(topCommand.getAliases().contains("resources"));
    }
    
    @Test
    @DisplayName("显示系统资源")
    void testShowSystemResources() {
        CommandResult result = topCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("系统资源"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = topCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("top"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
