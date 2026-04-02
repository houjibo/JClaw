package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RenameCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("RenameCommand 测试")
class RenameCommandTest {
    
    private RenameCommand renameCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        renameCommand = new RenameCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("rename", renameCommand.getName());
        assertEquals("重命名会话或文件", renameCommand.getDescription());
        assertEquals(Command.CommandCategory.SESSION, renameCommand.getCategory());
        assertTrue(renameCommand.getAliases().contains("mv"));
    }
    
    @Test
    @DisplayName("显示帮助")
    void testShowHelp() {
        CommandResult result = renameCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("重命名命令"));
    }
    
    @Test
    @DisplayName("重命名会话")
    void testRenameSession() {
        CommandResult result = renameCommand.execute("session-001 我的会话", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已重命名"));
    }
    
    @Test
    @DisplayName("重命名文件")
    void testRenameFile() {
        CommandResult result = renameCommand.execute("old.txt new.txt", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已重命名"));
    }
    
    @Test
    @DisplayName("参数不足")
    void testInsufficientArgs() {
        CommandResult result = renameCommand.execute("only-one-arg", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertTrue(result.getMessage().contains("用法"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = renameCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("rename"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(renameCommand.getAliases().contains("mv"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
