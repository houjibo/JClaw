package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ContextCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ContextCommand 测试")
class ContextCommandTest {
    
    private ContextCommand contextCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        contextCommand = new ContextCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("context", contextCommand.getName());
        assertEquals("上下文管理", contextCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, contextCommand.getCategory());
        assertTrue(contextCommand.getAliases().contains("ctx"));
        assertTrue(contextCommand.getAliases().contains("cx"));
    }
    
    @Test
    @DisplayName("显示上下文")
    void testShowContext() {
        CommandResult result = contextCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("上下文"));
    }
    
    @Test
    @DisplayName("添加消息")
    void testAddToContext() {
        CommandResult result = contextCommand.execute("add 测试消息", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已添加"));
    }
    
    @Test
    @DisplayName("清空上下文")
    void testClearContext() {
        // 先添加一些消息
        contextCommand.execute("add 测试 1", context);
        contextCommand.execute("add 测试 2", context);
        
        CommandResult result = contextCommand.execute("clear", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已清空"));
    }
    
    @Test
    @DisplayName("上下文信息")
    void testContextInfo() {
        CommandResult result = contextCommand.execute("info", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("上下文信息"));
    }
    
    @Test
    @DisplayName("导出上下文")
    void testExportContext() {
        // 先添加一些消息
        contextCommand.execute("add 测试 1", context);
        contextCommand.execute("add 测试 2", context);
        
        CommandResult result = contextCommand.execute("export", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("导出 JSON 格式")
    void testExportJson() {
        CommandResult result = contextCommand.execute("export json", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("["));
    }
    
    @Test
    @DisplayName("设置上下文大小")
    void testSetContextSize() {
        CommandResult result = contextCommand.execute("size 50", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已设置"));
    }
    
    @Test
    @DisplayName("错误处理 - 参数不足")
    void testInsufficientArgs() {
        CommandResult result = contextCommand.execute("add", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 无效大小")
    void testInvalidSize() {
        CommandResult result = contextCommand.execute("size 9999", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = contextCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("context"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(contextCommand.getAliases().contains("ctx"));
        assertTrue(contextCommand.getAliases().contains("cx"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
