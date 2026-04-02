package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EnvCommand 单元测试
 */
@DisplayName("EnvCommand 测试")
class EnvCommandTest {
    
    private EnvCommand envCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        envCommand = new EnvCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("env", envCommand.getName());
        assertEquals("环境变量管理", envCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, envCommand.getCategory());
    }
    
    @Test
    @DisplayName("列出环境变量")
    void testListEnv() {
        CommandResult result = envCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("环境变量"));
    }
    
    @Test
    @DisplayName("获取环境变量")
    void testGetEnv() {
        CommandResult result = envCommand.execute("get JAVA_HOME", context);
        
        assertNotNull(result);
        // JAVA_HOME 可能存在也可能不存在
        assertTrue(result.getType() == CommandResult.ResultType.SUCCESS || 
                   result.getType() == CommandResult.ResultType.ERROR);
    }
    
    @Test
    @DisplayName("设置环境变量指导")
    void testSetEnv() {
        CommandResult result = envCommand.execute("set TEST_VAR test_value", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("设置环境变量"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = envCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("env"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
