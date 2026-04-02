package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("LoginCommand 测试")
class LoginCommandTest {
    
    private LoginCommand loginCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        loginCommand = new LoginCommand();
        context = createMockContext();
        // 重置登录状态
        resetLoginState();
    }
    
    @AfterEach
    void tearDown() {
        // 清理登录状态
        resetLoginState();
    }
    
    // 使用反射重置静态字段
    private void resetLoginState() {
        try {
            Field loggedInField = LoginCommand.class.getDeclaredField("isLoggedIn");
            loggedInField.setAccessible(true);
            loggedInField.set(null, false);
            
            Field usernameField = LoginCommand.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            usernameField.set(null, "");
        } catch (Exception e) {
            // 忽略反射错误
        }
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("login", loginCommand.getName());
        assertEquals("登录认证", loginCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, loginCommand.getCategory());
        assertTrue(loginCommand.getAliases().contains("signin"));
        assertTrue(loginCommand.getAliases().contains("auth"));
    }
    
    @Test
    @DisplayName("显示登录帮助")
    void testShowLoginHelp() {
        CommandResult result = loginCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("登录帮助"));
    }
    
    @Test
    @DisplayName("登录（参数不足）")
    void testLoginInsufficientArgs() {
        CommandResult result = loginCommand.execute("user", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("登录帮助"));
    }
    
    @Test
    @DisplayName("登录成功")
    void testLoginSuccess() {
        CommandResult result = loginCommand.execute("user@example.com password123", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("登录成功"));
    }
    
    @Test
    @DisplayName("登录返回数据")
    void testLoginReturnsData() {
        CommandResult result = loginCommand.execute("user@example.com password123", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("loggedIn"));
        assertTrue(result.getData().containsKey("sessionId"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = loginCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("login"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(loginCommand.getAliases().contains("signin"));
        assertTrue(loginCommand.getAliases().contains("auth"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
