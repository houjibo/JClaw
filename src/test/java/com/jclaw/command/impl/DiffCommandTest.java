package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DiffCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("DiffCommand 测试")
class DiffCommandTest {
    
    private DiffCommand diffCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        diffCommand = new DiffCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("diff", diffCommand.getName());
        assertEquals("代码差异比较", diffCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, diffCommand.getCategory());
        assertTrue(diffCommand.getAliases().contains("d"));
    }
    
    @Test
    @DisplayName("显示工作区差异")
    void testShowWorkingDirDiff() {
        CommandResult result = diffCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("工作区差异"));
    }
    
    @Test
    @DisplayName("显示暂存区差异")
    void testShowStagedDiff() {
        CommandResult result = diffCommand.execute("--cached", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("暂存区差异"));
    }
    
    @Test
    @DisplayName("比较提交")
    void testCompareCommits() {
        CommandResult result = diffCommand.execute("HEAD~1 HEAD", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("提交差异"));
    }
    
    @Test
    @DisplayName("显示文件差异")
    void testShowFileDiff() {
        CommandResult result = diffCommand.execute("src/main.java", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("文件差异"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = diffCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("diff"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(diffCommand.getAliases().contains("d"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
