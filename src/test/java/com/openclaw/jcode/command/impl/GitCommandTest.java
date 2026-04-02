package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GitCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("GitCommand 测试")
class GitCommandTest {
    
    private GitCommand gitCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        gitCommand = new GitCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("git", gitCommand.getName());
        assertEquals("Git 版本控制操作", gitCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, gitCommand.getCategory());
        assertNotNull(gitCommand.getAliases());
        assertTrue(gitCommand.getAliases().contains("g"));
    }
    
    @Test
    @DisplayName("无参数执行")
    void testExecuteNoArgs() {
        CommandResult result = gitCommand.execute(null, context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertTrue(result.getMessage().contains("请指定"));
    }
    
    @Test
    @DisplayName("空参数执行")
    void testExecuteEmptyArgs() {
        CommandResult result = gitCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("status 子命令")
    void testExecuteStatus() {
        CommandResult result = gitCommand.execute("status", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("Git status"));
    }
    
    @Test
    @DisplayName("branch 子命令")
    void testExecuteBranch() {
        CommandResult result = gitCommand.execute("branch", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("log 子命令")
    void testExecuteLog() {
        CommandResult result = gitCommand.execute("log", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("help 方法")
    void testGetHelp() {
        String help = gitCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("git"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("示例"));
    }
    
    @Test
    @DisplayName("参数验证")
    void testParameterValidation() {
        // 空命令
        CommandResult result1 = gitCommand.execute("", context);
        assertEquals(CommandResult.ResultType.ERROR, result1.getType());
        
        // 空白命令
        CommandResult result2 = gitCommand.execute("   ", context);
        assertEquals(CommandResult.ResultType.ERROR, result2.getType());
    }
    
    @Test
    @DisplayName("显示文本格式")
    void testDisplayTextFormat() {
        CommandResult result = gitCommand.execute("status", context);
        
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("##"));
        assertTrue(result.getDisplayText().contains("Git"));
    }
    
    @Test
    @DisplayName("返回数据")
    void testReturnData() {
        CommandResult result = gitCommand.execute("status", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("output"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
