package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GradleEnhancedCommand 单元测试
 */
@DisplayName("GradleEnhancedCommand 测试")
class GradleEnhancedCommandTest {
    
    private GradleEnhancedCommand gradleEnhancedCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        gradleEnhancedCommand = new GradleEnhancedCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("gradle-enhanced", gradleEnhancedCommand.getName());
        assertEquals("Gradle 增强功能", gradleEnhancedCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, gradleEnhancedCommand.getCategory());
        assertTrue(gradleEnhancedCommand.getAliases().contains("gradle-ext"));
    }
    
    @Test
    @DisplayName("显示 Gradle 增强信息")
    void testShowGradleEnhancedInfo() {
        CommandResult result = gradleEnhancedCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Gradle"));
    }
    
    @Test
    @DisplayName("Gradle Wrapper")
    void testGradleWrapper() {
        CommandResult result = gradleEnhancedCommand.execute("wrapper", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Wrapper"));
    }
    
    @Test
    @DisplayName("Gradle Projects")
    void testGradleProjects() {
        CommandResult result = gradleEnhancedCommand.execute("projects", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("项目"));
    }
    
    @Test
    @DisplayName("Gradle Dependencies")
    void testGradleDependencies() {
        CommandResult result = gradleEnhancedCommand.execute("dependencies", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("依赖"));
    }
    
    @Test
    @DisplayName("Gradle Build Scan")
    void testGradleBuildScan() {
        CommandResult result = gradleEnhancedCommand.execute("buildScan", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Build Scan"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = gradleEnhancedCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("gradle-enhanced"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
