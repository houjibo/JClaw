package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GradleCommand 单元测试
 */
@DisplayName("GradleCommand 测试")
class GradleCommandTest {
    
    private GradleCommand gradleCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        gradleCommand = new GradleCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("gradle", gradleCommand.getName());
        assertEquals("Gradle 构建管理", gradleCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, gradleCommand.getCategory());
        assertTrue(gradleCommand.getAliases().contains("gradlew"));
    }
    
    @Test
    @DisplayName("显示 Gradle 信息")
    void testShowGradleInfo() {
        CommandResult result = gradleCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Gradle"));
    }
    
    @Test
    @DisplayName("Clean 命令")
    void testGradleClean() {
        CommandResult result = gradleCommand.execute("clean", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Clean"));
    }
    
    @Test
    @DisplayName("Build 命令")
    void testGradleBuild() {
        CommandResult result = gradleCommand.execute("build", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Build"));
    }
    
    @Test
    @DisplayName("Test 命令")
    void testGradleTest() {
        CommandResult result = gradleCommand.execute("test", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Test"));
    }
    
    @Test
    @DisplayName("Tasks 命令")
    void testGradleTasks() {
        CommandResult result = gradleCommand.execute("tasks", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Tasks"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = gradleCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("gradle"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
