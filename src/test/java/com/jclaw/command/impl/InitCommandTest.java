package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InitCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("InitCommand 测试")
class InitCommandTest {
    
    private InitCommand initCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        initCommand = new InitCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("init", initCommand.getName());
        assertEquals("项目初始化", initCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, initCommand.getCategory());
        assertTrue(initCommand.getAliases().contains("initialize"));
        assertTrue(initCommand.getAliases().contains("create"));
    }
    
    @Test
    @DisplayName("显示帮助")
    void testShowHelp() {
        CommandResult result = initCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("项目初始化"));
    }
    
    @Test
    @DisplayName("初始化项目")
    void testInitProject() {
        CommandResult result = initCommand.execute("project myapp", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已创建"));
    }
    
    @Test
    @DisplayName("初始化 Git")
    void testInitGit() {
        CommandResult result = initCommand.execute("git", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Git"));
    }
    
    @Test
    @DisplayName("初始化配置")
    void testInitConfig() {
        CommandResult result = initCommand.execute("config", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("配置"));
    }
    
    @Test
    @DisplayName("需要确认")
    void testRequiresConfirmation() {
        assertTrue(initCommand.isRequiresConfirmation());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = initCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("init"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(initCommand.getAliases().contains("initialize"));
        assertTrue(initCommand.getAliases().contains("create"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
