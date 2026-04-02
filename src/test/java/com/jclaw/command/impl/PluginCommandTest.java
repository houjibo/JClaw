package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("PluginCommand 测试")
class PluginCommandTest {
    
    private PluginCommand pluginCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        pluginCommand = new PluginCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("plugin", pluginCommand.getName());
        assertEquals("插件管理（列表、安装、卸载、重载）", pluginCommand.getDescription());
        assertEquals(Command.CommandCategory.PLUGIN, pluginCommand.getCategory());
        assertTrue(pluginCommand.getAliases().contains("plugins"));
        assertTrue(pluginCommand.getAliases().contains("plug"));
        assertTrue(pluginCommand.getAliases().contains("p"));
    }
    
    @Test
    @DisplayName("列出插件")
    void testListPlugins() {
        CommandResult result = pluginCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("插件列表"));
    }
    
    @Test
    @DisplayName("安装插件")
    void testInstallPlugin() {
        CommandResult result = pluginCommand.execute("install myplugin", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已安装"));
    }
    
    @Test
    @DisplayName("卸载插件")
    void testUninstallPlugin() {
        // 先安装插件
        pluginCommand.execute("install testplugin", context);
        
        CommandResult result = pluginCommand.execute("uninstall testplugin", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已卸载"));
    }
    
    @Test
    @DisplayName("启用插件")
    void testEnablePlugin() {
        // 先安装插件
        pluginCommand.execute("install testplugin2", context);
        
        CommandResult result = pluginCommand.execute("enable testplugin2", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已启用"));
    }
    
    @Test
    @DisplayName("禁用插件")
    void testDisablePlugin() {
        CommandResult result = pluginCommand.execute("disable myplugin", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已禁用"));
    }
    
    @Test
    @DisplayName("重载插件")
    void testReloadPlugins() {
        CommandResult result = pluginCommand.execute("reload", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已重载"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = pluginCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("plugin"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(pluginCommand.getAliases().contains("plugins"));
        assertTrue(pluginCommand.getAliases().contains("plug"));
        assertTrue(pluginCommand.getAliases().contains("p"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
