package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AgentsCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("AgentsCommand 测试")
class AgentsCommandTest {
    
    private AgentsCommand agentsCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        agentsCommand = new AgentsCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("agents", agentsCommand.getName());
        assertEquals("Agent 管理", agentsCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, agentsCommand.getCategory());
        assertTrue(agentsCommand.getAliases().contains("agent"));
        assertTrue(agentsCommand.getAliases().contains("ag"));
    }
    
    @Test
    @DisplayName("列出 Agent")
    void testListAgents() {
        CommandResult result = agentsCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("Agent 列表"));
    }
    
    @Test
    @DisplayName("创建 Agent")
    void testCreateAgent() {
        CommandResult result = agentsCommand.execute("create Test-Agent", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已创建"));
    }
    
    @Test
    @DisplayName("启动 Agent")
    void testStartAgent() {
        CommandResult result = agentsCommand.execute("start main", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已启动"));
    }
    
    @Test
    @DisplayName("停止 Agent")
    void testStopAgent() {
        CommandResult result = agentsCommand.execute("stop main", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已停止"));
    }
    
    @Test
    @DisplayName("Agent 详情")
    void testAgentInfo() {
        CommandResult result = agentsCommand.execute("info main", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Agent 详情"));
    }
    
    @Test
    @DisplayName("Agent 状态")
    void testAgentStatus() {
        CommandResult result = agentsCommand.execute("status main", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("状态"));
    }
    
    @Test
    @DisplayName("错误处理 - Agent 不存在")
    void testAgentNotFound() {
        CommandResult result = agentsCommand.execute("info nonexistent", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 参数不足")
    void testInsufficientArgs() {
        CommandResult result = agentsCommand.execute("create", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = agentsCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("agents"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(agentsCommand.getAliases().contains("agent"));
        assertTrue(agentsCommand.getAliases().contains("ag"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
