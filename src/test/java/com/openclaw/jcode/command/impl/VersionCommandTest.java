package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * VersionCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("VersionCommand 测试")
class VersionCommandTest {
    
    private VersionCommand versionCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        versionCommand = new VersionCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("version", versionCommand.getName());
        assertEquals("显示版本信息", versionCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, versionCommand.getCategory());
        assertTrue(versionCommand.getAliases().contains("ver"));
        assertTrue(versionCommand.getAliases().contains("v"));
    }
    
    @Test
    @DisplayName("显示版本信息")
    void testExecute() {
        CommandResult result = versionCommand.execute(null, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("JClaw"));
        assertTrue(result.getDisplayText().contains("版本"));
    }
    
    @Test
    @DisplayName("版本信息包含版本号")
    void testVersionInfo() {
        CommandResult result = versionCommand.execute(null, context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("version"));
        assertNotNull(result.getData().get("version"));
    }
    
    @Test
    @DisplayName("版本信息包含构建日期")
    void testBuildDate() {
        CommandResult result = versionCommand.execute(null, context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("buildDate"));
    }
    
    @Test
    @DisplayName("版本信息包含 Java 版本")
    void testJavaVersion() {
        CommandResult result = versionCommand.execute(null, context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("javaVersion"));
    }
    
    @Test
    @DisplayName("版本信息包含工具数量")
    void testToolsCount() {
        CommandResult result = versionCommand.execute(null, context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("tools"));
        assertEquals(45, result.getData().get("tools"));
    }
    
    @Test
    @DisplayName("版本信息包含命令数量")
    void testCommandsCount() {
        CommandResult result = versionCommand.execute(null, context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("commands"));
        assertTrue((Integer) result.getData().get("commands") > 0);
    }
    
    @Test
    @DisplayName("显示文本包含技术栈")
    void testDisplayTextContainsStack() {
        CommandResult result = versionCommand.execute(null, context);
        
        String display = result.getDisplayText();
        assertTrue(display.contains("Spring Boot"));
        assertTrue(display.contains("Java"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = versionCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("version"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(versionCommand.getAliases().contains("ver"));
        assertTrue(versionCommand.getAliases().contains("v"));
        assertTrue(versionCommand.getAliases().contains("-v"));
        assertTrue(versionCommand.getAliases().contains("--version"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
