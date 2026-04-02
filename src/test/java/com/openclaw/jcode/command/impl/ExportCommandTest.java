package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ExportCommand 单元测试
 */
@DisplayName("ExportCommand 测试")
class ExportCommandTest {
    
    private ExportCommand exportCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        exportCommand = new ExportCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("export", exportCommand.getName());
        assertEquals("导出会话/配置", exportCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, exportCommand.getCategory());
        assertTrue(exportCommand.getAliases().contains("save"));
        assertTrue(exportCommand.getAliases().contains("backup"));
    }
    
    @Test
    @DisplayName("显示导出功能")
    void testShowExportInfo() {
        CommandResult result = exportCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("导出"));
    }
    
    @Test
    @DisplayName("导出会话")
    void testExportSession() {
        CommandResult result = exportCommand.execute("session test-session", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("导出"));
    }
    
    @Test
    @DisplayName("导出配置")
    void testExportConfig() {
        CommandResult result = exportCommand.execute("config", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("配置"));
    }
    
    @Test
    @DisplayName("导出日志")
    void testExportLogs() {
        CommandResult result = exportCommand.execute("logs", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("日志"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = exportCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("export"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
