package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HelpCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("HelpCommand 测试")
class HelpCommandTest {
    
    private HelpCommand helpCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        helpCommand = new HelpCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("help", helpCommand.getName());
        assertEquals("显示帮助信息", helpCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, helpCommand.getCategory());
        assertTrue(helpCommand.getAliases().contains("h"));
        assertTrue(helpCommand.getAliases().contains("?"));
    }
    
    @Test
    @DisplayName("显示一般帮助")
    void testShowGeneralHelp() {
        CommandResult result = helpCommand.execute(null, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("JClaw 帮助"));
        assertTrue(result.getDisplayText().contains("快速开始"));
    }
    
    @Test
    @DisplayName("显示一般帮助（空参数）")
    void testShowGeneralHelpEmpty() {
        CommandResult result = helpCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("帮助包含命令列表")
    void testHelpContainsCommands() {
        CommandResult result = helpCommand.execute(null, context);
        
        assertNotNull(result.getDisplayText());
        String display = result.getDisplayText();
        assertTrue(display.contains("help"));
        assertTrue(display.contains("git"));
        assertTrue(display.contains("config"));
    }
    
    @Test
    @DisplayName("帮助包含示例")
    void testHelpContainsExamples() {
        CommandResult result = helpCommand.execute(null, context);
        
        assertNotNull(result.getDisplayText());
        String display = result.getDisplayText();
        assertTrue(display.contains("示例"));
        assertTrue(display.contains("```"));
    }
    
    @Test
    @DisplayName("帮助信息方法")
    void testGetHelp() {
        String help = helpCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("help"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("示例"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(helpCommand.getAliases().contains("h"));
        assertTrue(helpCommand.getAliases().contains("?"));
    }
    
    @Test
    @DisplayName("不需要确认")
    void testNoConfirmationRequired() {
        assertFalse(helpCommand.isRequiresConfirmation());
    }
    
    @Test
    @DisplayName("支持非交互模式")
    void testSupportsNonInteractive() {
        assertTrue(helpCommand.isSupportsNonInteractive());
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
