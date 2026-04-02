package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * McpCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("McpCommand 测试")
class McpCommandTest {
    
    private McpCommand mcpCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        mcpCommand = new McpCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("mcp", mcpCommand.getName());
        assertEquals("MCP 服务器管理", mcpCommand.getDescription());
        assertEquals(Command.CommandCategory.PLUGIN, mcpCommand.getCategory());
        assertTrue(mcpCommand.getAliases().contains("m"));
    }
    
    @Test
    @DisplayName("列出服务器")
    void testListServers() {
        CommandResult result = mcpCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("MCP 服务器列表"));
    }
    
    @Test
    @DisplayName("列出资源")
    void testListResources() {
        CommandResult result = mcpCommand.execute("resources", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("MCP 资源"));
    }
    
    @Test
    @DisplayName("添加服务器")
    void testAddServer() {
        CommandResult result = mcpCommand.execute("add test-server", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已添加"));
    }
    
    @Test
    @DisplayName("移除服务器")
    void testRemoveServer() {
        // 先添加服务器
        mcpCommand.execute("add test-server-to-remove", context);
        
        CommandResult result = mcpCommand.execute("remove test-server-to-remove", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已移除") || result.getMessage().contains("remove"));
    }
    
    @Test
    @DisplayName("认证服务器")
    void testAuthServer() {
        CommandResult result = mcpCommand.execute("auth test-server", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("认证成功"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = mcpCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("mcp"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(mcpCommand.getAliases().contains("m"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
