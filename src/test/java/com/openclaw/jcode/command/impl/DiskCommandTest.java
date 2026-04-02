package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DiskCommand 单元测试
 */
@DisplayName("DiskCommand 测试")
class DiskCommandTest {
    
    private DiskCommand diskCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        diskCommand = new DiskCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("disk", diskCommand.getName());
        assertEquals("磁盘使用分析", diskCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, diskCommand.getCategory());
        assertTrue(diskCommand.getAliases().contains("df"));
        assertTrue(diskCommand.getAliases().contains("du"));
    }
    
    @Test
    @DisplayName("显示磁盘使用")
    void testShowDiskUsage() {
        CommandResult result = diskCommand.execute(".", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("磁盘使用"));
    }
    
    @Test
    @DisplayName("显示磁盘空间")
    void testShowDiskFree() {
        CommandResult result = diskCommand.execute("free", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("磁盘空间"));
    }
    
    @Test
    @DisplayName("错误处理 - 路径不存在")
    void testPathNotFound() {
        CommandResult result = diskCommand.execute("usage /nonexistent/path", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = diskCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("disk"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
