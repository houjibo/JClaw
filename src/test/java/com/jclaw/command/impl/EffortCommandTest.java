package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EffortCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("EffortCommand 测试")
class EffortCommandTest {
    
    private EffortCommand effortCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        effortCommand = new EffortCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("effort", effortCommand.getName());
        assertEquals("评估任务工作量", effortCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, effortCommand.getCategory());
        assertTrue(effortCommand.getAliases().contains("estimate"));
        assertTrue(effortCommand.getAliases().contains("time"));
    }
    
    @Test
    @DisplayName("显示评估指南")
    void testEstimateDefault() {
        CommandResult result = effortCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("工作量评估指南"));
    }
    
    @Test
    @DisplayName("评估 Bug 修复")
    void testEstimateBugFix() {
        CommandResult result = effortCommand.execute("修复登录 bug", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Bug 修复"));
    }
    
    @Test
    @DisplayName("评估新功能")
    void testEstimateFeature() {
        CommandResult result = effortCommand.execute("新增用户管理功能", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("新功能"));
    }
    
    @Test
    @DisplayName("评估测试编写")
    void testEstimateTest() {
        CommandResult result = effortCommand.execute("编写单元测试", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("测试编写"));
    }
    
    @Test
    @DisplayName("评估代码重构")
    void testEstimateRefactor() {
        CommandResult result = effortCommand.execute("重构支付模块", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("代码重构"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = effortCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("effort"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(effortCommand.getAliases().contains("estimate"));
        assertTrue(effortCommand.getAliases().contains("time"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
