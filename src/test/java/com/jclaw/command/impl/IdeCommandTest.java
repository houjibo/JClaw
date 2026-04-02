package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * IdeCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("IdeCommand 测试")
class IdeCommandTest {
    
    private IdeCommand ideCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        ideCommand = new IdeCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("ide", ideCommand.getName());
        assertEquals("IDE 集成和插件管理", ideCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, ideCommand.getCategory());
        assertTrue(ideCommand.getAliases().contains("editor"));
        assertTrue(ideCommand.getAliases().contains("vscode"));
    }
    
    @Test
    @DisplayName("显示 IDE 状态")
    void testShowIdeStatus() {
        CommandResult result = ideCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("IDE 集成状态"));
    }
    
    @Test
    @DisplayName("安装 VS Code 插件")
    void testInstallVscode() {
        CommandResult result = ideCommand.execute("install vscode", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("VS Code"));
    }
    
    @Test
    @DisplayName("安装 IDEA 插件")
    void testInstallIdea() {
        CommandResult result = ideCommand.execute("install idea", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("IntelliJ"));
    }
    
    @Test
    @DisplayName("卸载插件")
    void testUninstallPlugin() {
        CommandResult result = ideCommand.execute("uninstall vscode", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("显示配置")
    void testShowConfig() {
        CommandResult result = ideCommand.execute("config", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("IDE 配置"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = ideCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("ide"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(ideCommand.getAliases().contains("editor"));
        assertTrue(ideCommand.getAliases().contains("vscode"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
