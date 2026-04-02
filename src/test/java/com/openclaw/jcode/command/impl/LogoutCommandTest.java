package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LogoutCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("LogoutCommand 测试")
class LogoutCommandTest {
    
    private LogoutCommand logoutCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        logoutCommand = new LogoutCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("logout", logoutCommand.getName());
        assertEquals("登出认证", logoutCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, logoutCommand.getCategory());
        assertTrue(logoutCommand.getAliases().contains("signout"));
    }
    
    @Test
    @DisplayName("登出成功")
    void testLogoutSuccess() {
        CommandResult result = logoutCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("登出成功"));
    }
    
    @Test
    @DisplayName("登出返回数据")
    void testLogoutReturnsData() {
        CommandResult result = logoutCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("loggedOut"));
        assertTrue(result.getData().containsKey("timestamp"));
    }
    
    @Test
    @DisplayName("登出包含会话总结")
    void testLogoutContainsSummary() {
        CommandResult result = logoutCommand.execute("", context);
        
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("会话总结"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = logoutCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("logout"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(logoutCommand.getAliases().contains("signout"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
