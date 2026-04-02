package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ThinkbackCommand 单元测试
 */
@DisplayName("ThinkbackCommand 测试")
class ThinkbackCommandTest {
    
    private ThinkbackCommand thinkbackCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        thinkbackCommand = new ThinkbackCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("thinkback", thinkbackCommand.getName());
        assertEquals("思考回溯", thinkbackCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, thinkbackCommand.getCategory());
        assertTrue(thinkbackCommand.getAliases().contains("review"));
        assertTrue(thinkbackCommand.getAliases().contains("reflect"));
    }
    
    @Test
    @DisplayName("显示回溯选项")
    void testShowThinkback() {
        CommandResult result = thinkbackCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("思考回溯"));
    }
    
    @Test
    @DisplayName("会话回溯")
    void testThinkbackSession() {
        CommandResult result = thinkbackCommand.execute("session session-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("回溯"));
    }
    
    @Test
    @DisplayName("决策回溯")
    void testThinkbackDecision() {
        CommandResult result = thinkbackCommand.execute("decision tech-stack", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("决策"));
    }
    
    @Test
    @DisplayName("学习收获")
    void testThinkbackLearning() {
        CommandResult result = thinkbackCommand.execute("learning", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("学习"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少参数")
    void testMissingArgs() {
        CommandResult result = thinkbackCommand.execute("session", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = thinkbackCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("thinkback"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
