package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RebaseCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("RebaseCommand 测试")
class RebaseCommandTest {
    
    private RebaseCommand rebaseCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        rebaseCommand = new RebaseCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("rebase", rebaseCommand.getName());
        assertEquals("Git 变基操作", rebaseCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, rebaseCommand.getCategory());
        assertTrue(rebaseCommand.getAliases().contains("re"));
    }
    
    @Test
    @DisplayName("标准变基")
    void testStandardRebase() {
        CommandResult result = rebaseCommand.execute("main", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Git 变基"));
    }
    
    @Test
    @DisplayName("交互式变基")
    void testInteractiveRebase() {
        CommandResult result = rebaseCommand.execute("-i HEAD~3", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("交互式变基"));
    }
    
    @Test
    @DisplayName("继续变基")
    void testContinueRebase() {
        CommandResult result = rebaseCommand.execute("--continue", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("继续"));
    }
    
    @Test
    @DisplayName("中止变基")
    void testAbortRebase() {
        CommandResult result = rebaseCommand.execute("--abort", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("中止"));
    }
    
    @Test
    @DisplayName("跳过提交")
    void testSkipRebase() {
        CommandResult result = rebaseCommand.execute("--skip", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("跳过"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少分支")
    void testMissingBranch() {
        CommandResult result = rebaseCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("帮助"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = rebaseCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("rebase"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(rebaseCommand.getAliases().contains("re"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
