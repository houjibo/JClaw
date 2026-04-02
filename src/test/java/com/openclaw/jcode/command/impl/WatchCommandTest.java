package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * WatchCommand 单元测试
 */
@DisplayName("WatchCommand 测试")
class WatchCommandTest {
    
    private WatchCommand watchCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        watchCommand = new WatchCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("watch", watchCommand.getName());
        assertEquals("文件变动监听", watchCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, watchCommand.getCategory());
        assertTrue(watchCommand.getAliases().contains("monitor"));
    }
    
    @Test
    @DisplayName("显示帮助")
    void testShowHelp() {
        CommandResult result = watchCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("帮助"));
    }
    
    @Test
    @DisplayName("启动监听")
    void testStartWatch() {
        CommandResult result = watchCommand.execute("start .", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("启动"));
    }
    
    @Test
    @DisplayName("查看状态")
    void testWatchStatus() {
        CommandResult result = watchCommand.execute("status", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("状态"));
    }
    
    @Test
    @DisplayName("停止监听")
    void testStopWatch() {
        // 先启动
        watchCommand.execute("start .", context);
        
        // 然后停止
        CommandResult result = watchCommand.execute("stop", context);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("停止"));
    }
    
    @Test
    @DisplayName("错误处理 - 路径不存在")
    void testPathNotFound() {
        CommandResult result = watchCommand.execute("start /nonexistent/path", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = watchCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("watch"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
