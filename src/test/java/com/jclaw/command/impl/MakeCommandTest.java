package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MakeCommand 单元测试
 */
@DisplayName("MakeCommand 测试")
class MakeCommandTest {
    
    private MakeCommand makeCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        makeCommand = new MakeCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("make", makeCommand.getName());
        assertEquals("Make 构建", makeCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, makeCommand.getCategory());
        assertTrue(makeCommand.getAliases().contains("makefile"));
        assertTrue(makeCommand.getAliases().contains("cmake"));
    }
    
    @Test
    @DisplayName("默认构建")
    void testMakeDefault() {
        CommandResult result = makeCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Make"));
    }
    
    @Test
    @DisplayName("Clean 命令")
    void testMakeClean() {
        CommandResult result = makeCommand.execute("clean", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Clean"));
    }
    
    @Test
    @DisplayName("All 命令")
    void testMakeAll() {
        CommandResult result = makeCommand.execute("all", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("All"));
    }
    
    @Test
    @DisplayName("Install 命令")
    void testMakeInstall() {
        CommandResult result = makeCommand.execute("install", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Install"));
    }
    
    @Test
    @DisplayName("Test 命令")
    void testMakeTest() {
        CommandResult result = makeCommand.execute("test", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Test"));
    }
    
    @Test
    @DisplayName("自定义目标")
    void testMakeTarget() {
        CommandResult result = makeCommand.execute("debug", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("debug"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = makeCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("make"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
