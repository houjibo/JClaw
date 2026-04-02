package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DebugCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("DebugCommand 测试")
class DebugCommandTest {
    
    private DebugCommand debugCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        debugCommand = new DebugCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("debug", debugCommand.getName());
        assertEquals("调试工具（日志、堆栈、内存、性能）", debugCommand.getDescription());
        assertEquals(Command.CommandCategory.DEBUG, debugCommand.getCategory());
        assertTrue(debugCommand.getAliases().contains("dbg"));
        assertTrue(debugCommand.getAliases().contains("d"));
    }
    
    @Test
    @DisplayName("显示调试菜单")
    void testShowDebugMenu() {
        CommandResult result = debugCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("调试工具菜单"));
    }
    
    @Test
    @DisplayName("查看日志")
    void testShowLogs() {
        CommandResult result = debugCommand.execute("logs", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("系统日志"));
    }
    
    @Test
    @DisplayName("查看日志（debug 级别）")
    void testShowLogsDebug() {
        CommandResult result = debugCommand.execute("logs debug", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("查看堆栈")
    void testShowStackTrace() {
        CommandResult result = debugCommand.execute("stack", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("堆栈追踪"));
    }
    
    @Test
    @DisplayName("查看内存")
    void testShowMemory() {
        CommandResult result = debugCommand.execute("memory", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("内存使用"));
    }
    
    @Test
    @DisplayName("查看性能")
    void testShowPerformance() {
        CommandResult result = debugCommand.execute("performance", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("性能指标"));
    }
    
    @Test
    @DisplayName("查看追踪")
    void testShowTrace() {
        CommandResult result = debugCommand.execute("trace", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("调用追踪"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = debugCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("debug"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(debugCommand.getAliases().contains("dbg"));
        assertTrue(debugCommand.getAliases().contains("d"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
