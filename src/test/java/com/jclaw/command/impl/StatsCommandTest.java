package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * StatsCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("StatsCommand 测试")
class StatsCommandTest {
    
    private StatsCommand statsCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        statsCommand = new StatsCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("stats", statsCommand.getName());
        assertEquals("显示项目和代码统计", statsCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, statsCommand.getCategory());
        assertTrue(statsCommand.getAliases().contains("statistics"));
        assertTrue(statsCommand.getAliases().contains("st"));
    }
    
    @Test
    @DisplayName("显示概览统计")
    void testOverview() {
        CommandResult result = statsCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("项目统计概览"));
    }
    
    @Test
    @DisplayName("文件统计")
    void testFileStats() {
        CommandResult result = statsCommand.execute("files", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("文件统计"));
    }
    
    @Test
    @DisplayName("代码行数统计")
    void testLineStats() {
        CommandResult result = statsCommand.execute("lines", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("代码行数"));
    }
    
    @Test
    @DisplayName("提交统计")
    void testCommitStats() {
        CommandResult result = statsCommand.execute("commits", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("提交统计"));
    }
    
    @Test
    @DisplayName("工具统计")
    void testToolStats() {
        CommandResult result = statsCommand.execute("tools", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("工具系统"));
    }
    
    @Test
    @DisplayName("命令统计")
    void testCommandStats() {
        CommandResult result = statsCommand.execute("commands", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("命令系统"));
    }
    
    @Test
    @DisplayName("概览包含数据")
    void testOverviewContainsData() {
        CommandResult result = statsCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("javaFiles"));
        assertTrue(result.getData().containsKey("tools"));
        assertTrue(result.getData().containsKey("commands"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = statsCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("stats"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("类型"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(statsCommand.getAliases().contains("statistics"));
        assertTrue(statsCommand.getAliases().contains("st"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
