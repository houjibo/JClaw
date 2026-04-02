package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NetstatCommand 单元测试
 */
@DisplayName("NetstatCommand 测试")
class NetstatCommandTest {
    
    private NetstatCommand netstatCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        netstatCommand = new NetstatCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("netstat", netstatCommand.getName());
        assertEquals("网络状态查看", netstatCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, netstatCommand.getCategory());
        assertTrue(netstatCommand.getAliases().contains("ss"));
        assertTrue(netstatCommand.getAliases().contains("network"));
    }
    
    @Test
    @DisplayName("显示网络状态")
    void testShowNetworkStatus() {
        CommandResult result = netstatCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("网络"));
    }
    
    @Test
    @DisplayName("显示接口")
    void testShowInterfaces() {
        CommandResult result = netstatCommand.execute("interfaces", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("接口"));
    }
    
    @Test
    @DisplayName("显示路由")
    void testShowRoutes() {
        CommandResult result = netstatCommand.execute("routes", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("路由"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = netstatCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("netstat"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
