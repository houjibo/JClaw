package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BranchCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("BranchCommand 测试")
class BranchCommandTest {
    
    private BranchCommand branchCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        branchCommand = new BranchCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("branch", branchCommand.getName());
        assertEquals("Git 分支管理", branchCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, branchCommand.getCategory());
        assertTrue(branchCommand.getAliases().contains("br"));
    }
    
    @Test
    @DisplayName("列出分支")
    void testListBranches() {
        CommandResult result = branchCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("Git 分支列表"));
    }
    
    @Test
    @DisplayName("列出所有分支")
    void testListAllBranches() {
        CommandResult result = branchCommand.execute("-a", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("所有分支"));
    }
    
    @Test
    @DisplayName("列出远程分支")
    void testListRemoteBranches() {
        CommandResult result = branchCommand.execute("-r", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("远程分支"));
    }
    
    @Test
    @DisplayName("删除分支")
    void testDeleteBranch() {
        CommandResult result = branchCommand.execute("-d feature-old", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已删除"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = branchCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("branch"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(branchCommand.getAliases().contains("br"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
