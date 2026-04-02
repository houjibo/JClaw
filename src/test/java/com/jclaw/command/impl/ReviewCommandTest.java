package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ReviewCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ReviewCommand 测试")
class ReviewCommandTest {
    
    private ReviewCommand reviewCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        reviewCommand = new ReviewCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("review", reviewCommand.getName());
        assertEquals("代码审查", reviewCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, reviewCommand.getCategory());
        assertTrue(reviewCommand.getAliases().contains("pr"));
        assertTrue(reviewCommand.getAliases().contains("code-review"));
    }
    
    @Test
    @DisplayName("显示帮助")
    void testShowHelp() {
        CommandResult result = reviewCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("代码审查命令"));
    }
    
    @Test
    @DisplayName("审查 PR")
    void testReviewPR() {
        CommandResult result = reviewCommand.execute("pr 123", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("PR"));
        assertTrue(result.getDisplayText().contains("审查报告"));
    }
    
    @Test
    @DisplayName("审查文件")
    void testReviewFile() {
        CommandResult result = reviewCommand.execute("file src/main.java", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("文件审查"));
    }
    
    @Test
    @DisplayName("查看变更")
    void testShowChanges() {
        CommandResult result = reviewCommand.execute("changes", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("代码变更"));
    }
    
    @Test
    @DisplayName("审查摘要")
    void testShowSummary() {
        CommandResult result = reviewCommand.execute("summary", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("审查摘要"));
    }
    
    @Test
    @DisplayName("审查代码")
    void testReviewCode() {
        CommandResult result = reviewCommand.execute("some code here", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("代码审查"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = reviewCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("review"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(reviewCommand.getAliases().contains("pr"));
        assertTrue(reviewCommand.getAliases().contains("code-review"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
