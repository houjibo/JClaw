package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ConfigCommand 测试")
class ConfigCommandTest {
    
    private ConfigCommand configCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        configCommand = new ConfigCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("config", configCommand.getName());
        assertEquals("查看和修改配置", configCommand.getDescription());
        assertEquals(Command.CommandCategory.CONFIG, configCommand.getCategory());
        assertTrue(configCommand.getAliases().contains("cfg"));
        assertTrue(configCommand.getAliases().contains("settings"));
    }
    
    @Test
    @DisplayName("列出所有配置")
    void testListConfig() {
        CommandResult result = configCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("配置"));
    }
    
    @Test
    @DisplayName("获取配置值")
    void testGetConfig() {
        CommandResult result = configCommand.execute("model", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("key"));
        assertTrue(result.getData().containsKey("value"));
    }
    
    @Test
    @DisplayName("设置配置值")
    void testSetConfig() {
        CommandResult result = configCommand.execute("temperature 0.8", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已更新"));
    }
    
    @Test
    @DisplayName("设置布尔配置")
    void testSetBooleanConfig() {
        CommandResult result = configCommand.execute("autoApprove true", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("设置整数配置")
    void testSetIntegerConfig() {
        CommandResult result = configCommand.execute("maxTokens 8192", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("获取不存在的配置")
    void testGetNonExistentConfig() {
        CommandResult result = configCommand.execute("nonexistent", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertTrue(result.getMessage().contains("不存在"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = configCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("config"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("示例"));
    }
    
    @Test
    @DisplayName("帮助参数")
    void testHelpParameter() {
        CommandResult result = configCommand.execute("-h", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        // 验证别名已正确设置
        assertTrue(configCommand.getAliases().contains("cfg"));
        assertTrue(configCommand.getAliases().contains("settings"));
        assertTrue(configCommand.getAliases().contains("set"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
