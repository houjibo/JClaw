package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MavenCommand 单元测试
 */
@DisplayName("MavenCommand 测试")
class MavenCommandTest {
    
    private MavenCommand mavenCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        mavenCommand = new MavenCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("maven", mavenCommand.getName());
        assertEquals("Maven 构建管理", mavenCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, mavenCommand.getCategory());
        assertTrue(mavenCommand.getAliases().contains("mvn"));
    }
    
    @Test
    @DisplayName("显示 Maven 信息")
    void testShowMavenInfo() {
        CommandResult result = mavenCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Maven"));
    }
    
    @Test
    @DisplayName("Clean 命令")
    void testMavenClean() {
        CommandResult result = mavenCommand.execute("clean", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Clean"));
    }
    
    @Test
    @DisplayName("Compile 命令")
    void testMavenCompile() {
        CommandResult result = mavenCommand.execute("compile", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Compile"));
    }
    
    @Test
    @DisplayName("Test 命令")
    void testMavenTest() {
        CommandResult result = mavenCommand.execute("test", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Test"));
    }
    
    @Test
    @DisplayName("Package 命令")
    void testMavenPackage() {
        CommandResult result = mavenCommand.execute("package", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Package"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = mavenCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("maven"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
