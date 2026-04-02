package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * StatusCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("StatusCommand 测试")
class StatusCommandTest {
    
    private StatusCommand statusCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        statusCommand = new StatusCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("status", statusCommand.getName());
        assertEquals("状态查看", statusCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, statusCommand.getCategory());
        assertTrue(statusCommand.getAliases().contains("st"));
        assertTrue(statusCommand.getAliases().contains("state"));
    }
    
    @Test
    @DisplayName("显示系统状态")
    void testSystemStatus() {
        CommandResult result = statusCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("系统状态"));
    }
    
    @Test
    @DisplayName("Git 状态")
    void testGitStatus() {
        CommandResult result = statusCommand.execute("git", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Git 状态"));
    }
    
    @Test
    @DisplayName("工具状态")
    void testToolsStatus() {
        CommandResult result = statusCommand.execute("tools", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("工具状态"));
    }
    
    @Test
    @DisplayName("命令状态")
    void testCommandsStatus() {
        CommandResult result = statusCommand.execute("commands", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("命令状态"));
    }
    
    @Test
    @DisplayName("内存状态")
    void testMemoryStatus() {
        CommandResult result = statusCommand.execute("memory", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("内存状态"));
        assertTrue(result.getDisplayText().contains("JVM"));
    }
    
    @Test
    @DisplayName("健康检查")
    void testHealthStatus() {
        CommandResult result = statusCommand.execute("health", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("健康检查"));
        assertTrue(result.getDisplayText().contains("检查项"));
    }
    
    @Test
    @DisplayName("系统状态包含数据")
    void testSystemStatusContainsData() {
        CommandResult result = statusCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("status"));
        assertTrue(result.getData().containsKey("uptime"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = statusCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("status"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(statusCommand.getAliases().contains("st"));
        assertTrue(statusCommand.getAliases().contains("state"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
