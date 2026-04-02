package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * VimCommand 单元测试
 */
@DisplayName("VimCommand 测试")
class VimCommandTest {
    
    private VimCommand vimCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        vimCommand = new VimCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("vim", vimCommand.getName());
        assertEquals("Vim 编辑模式", vimCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, vimCommand.getCategory());
        assertTrue(vimCommand.getAliases().contains("vi"));
        assertTrue(vimCommand.getAliases().contains("nvim"));
    }
    
    @Test
    @DisplayName("显示 Vim 信息")
    void testShowVimInfo() {
        CommandResult result = vimCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Vim"));
    }
    
    @Test
    @DisplayName("打开文件")
    void testOpenFile() {
        CommandResult result = vimCommand.execute("open test.txt", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("打开"));
    }
    
    @Test
    @DisplayName("显示 Vim 帮助")
    void testShowVimHelp() {
        CommandResult result = vimCommand.execute("help", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("帮助"));
    }
    
    @Test
    @DisplayName("显示 Vim 模式")
    void testShowVimModes() {
        CommandResult result = vimCommand.execute("modes", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("模式"));
    }
    
    @Test
    @DisplayName("显示 Vim 命令")
    void testShowVimCommands() {
        CommandResult result = vimCommand.execute("commands", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("命令"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少参数")
    void testMissingArgs() {
        CommandResult result = vimCommand.execute("open", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = vimCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("vim"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
