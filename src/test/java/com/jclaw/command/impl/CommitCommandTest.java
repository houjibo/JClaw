package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CommitCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CommitCommand 测试")
class CommitCommandTest {
    
    private CommitCommand commitCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        commitCommand = new CommitCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("commit", commitCommand.getName());
        assertEquals("Git 提交管理", commitCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, commitCommand.getCategory());
        assertTrue(commitCommand.getAliases().contains("ci"));
    }
    
    @Test
    @DisplayName("显示最近提交")
    void testShowRecentCommits() {
        CommandResult result = commitCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("最近提交"));
    }
    
    @Test
    @DisplayName("创建提交")
    void testCreateCommit() {
        CommandResult result = commitCommand.execute("-m \"修复 bug\"", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("提交成功"));
    }
    
    @Test
    @DisplayName("创建提交包含数据")
    void testCreateCommitContainsData() {
        CommandResult result = commitCommand.execute("-m \"修复 bug\"", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("message"));
        assertTrue(result.getData().containsKey("hash"));
    }
    
    @Test
    @DisplayName("修正提交")
    void testAmendCommit() {
        CommandResult result = commitCommand.execute("--amend", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已修正"));
    }
    
    @Test
    @DisplayName("查看日志")
    void testShowLog() {
        CommandResult result = commitCommand.execute("log", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("提交历史"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = commitCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("commit"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(commitCommand.getAliases().contains("ci"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
