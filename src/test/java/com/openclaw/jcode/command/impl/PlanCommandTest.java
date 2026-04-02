package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PlanCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("PlanCommand 测试")
class PlanCommandTest {
    
    private PlanCommand planCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        planCommand = new PlanCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("plan", planCommand.getName());
        assertEquals("计划模式", planCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, planCommand.getCategory());
        assertTrue(planCommand.getAliases().contains("plans"));
        assertTrue(planCommand.getAliases().contains("planning"));
    }
    
    @Test
    @DisplayName("列出计划")
    void testListPlans() {
        CommandResult result = planCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("计划列表"));
    }
    
    @Test
    @DisplayName("创建计划")
    void testCreatePlan() {
        CommandResult result = planCommand.execute("create Test-Plan", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已创建"));
    }
    
    @Test
    @DisplayName("添加步骤")
    void testAddStep() {
        // 先创建计划
        planCommand.execute("create test-plan-add", context);
        
        CommandResult result = planCommand.execute("add test-plan-add 第一步", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已添加"));
    }
    
    @Test
    @DisplayName("开始计划")
    void testStartPlan() {
        // 先创建计划
        planCommand.execute("create test-plan-start", context);
        
        CommandResult result = planCommand.execute("start test-plan-start", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已启动"));
    }
    
    @Test
    @DisplayName("完成步骤")
    void testCompleteStep() {
        // 先创建计划并添加步骤
        planCommand.execute("create test-plan-complete", context);
        planCommand.execute("add test-plan-complete 第一步", context);
        planCommand.execute("start test-plan-complete", context);
        
        CommandResult result = planCommand.execute("complete test-plan-complete 1", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已完成"));
    }
    
    @Test
    @DisplayName("计划详情")
    void testPlanInfo() {
        // 先创建计划
        planCommand.execute("create test-plan-info", context);
        
        CommandResult result = planCommand.execute("info test-plan-info", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("计划详情"));
    }
    
    @Test
    @DisplayName("错误处理 - 计划不存在")
    void testPlanNotFound() {
        CommandResult result = planCommand.execute("info nonexistent", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 参数不足")
    void testInsufficientArgs() {
        CommandResult result = planCommand.execute("create", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = planCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("plan"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(planCommand.getAliases().contains("plans"));
        assertTrue(planCommand.getAliases().contains("planning"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
