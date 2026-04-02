package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FeedbackCommand 单元测试
 */
@DisplayName("FeedbackCommand 测试")
class FeedbackCommandTest {
    
    private FeedbackCommand feedbackCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        feedbackCommand = new FeedbackCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("feedback", feedbackCommand.getName());
        assertEquals("用户反馈", feedbackCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, feedbackCommand.getCategory());
        assertTrue(feedbackCommand.getAliases().contains("report"));
        assertTrue(feedbackCommand.getAliases().contains("issue"));
    }
    
    @Test
    @DisplayName("显示反馈功能")
    void testShowFeedbackInfo() {
        CommandResult result = feedbackCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("反馈"));
    }
    
    @Test
    @DisplayName("提交反馈")
    void testSubmitFeedback() {
        CommandResult result = feedbackCommand.execute("submit bug 测试反馈", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("提交"));
    }
    
    @Test
    @DisplayName("反馈列表")
    void testListFeedback() {
        CommandResult result = feedbackCommand.execute("list", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("列表"));
    }
    
    @Test
    @DisplayName("反馈状态")
    void testFeedbackStatus() {
        CommandResult result = feedbackCommand.execute("status FB-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("状态"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少参数")
    void testMissingArgs() {
        CommandResult result = feedbackCommand.execute("submit", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = feedbackCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("feedback"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
