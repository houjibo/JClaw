package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CopyCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CopyCommand 测试")
class CopyCommandTest {
    
    private CopyCommand copyCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        copyCommand = new CopyCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("copy", copyCommand.getName());
        assertEquals("复制内容到剪贴板", copyCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, copyCommand.getCategory());
        assertTrue(copyCommand.getAliases().contains("cp"));
        assertTrue(copyCommand.getAliases().contains("clipboard"));
    }
    
    @Test
    @DisplayName("显示剪贴板")
    void testShowClipboard() {
        CommandResult result = copyCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("剪贴板"));
    }
    
    @Test
    @DisplayName("复制内容")
    void testCopyContent() {
        CommandResult result = copyCommand.execute("Hello World", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("复制"));
    }
    
    @Test
    @DisplayName("复制内容包含统计")
    void testCopyContentWithStats() {
        CommandResult result = copyCommand.execute("Hello World", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("length"));
    }
    
    @Test
    @DisplayName("清空剪贴板")
    void testClearClipboard() {
        CommandResult result = copyCommand.execute("clear", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("清空"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = copyCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("copy"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(copyCommand.getAliases().contains("cp"));
        assertTrue(copyCommand.getAliases().contains("clipboard"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
