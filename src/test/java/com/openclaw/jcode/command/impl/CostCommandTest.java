package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CostCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CostCommand 测试")
class CostCommandTest {
    
    private CostCommand costCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        costCommand = new CostCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("cost", costCommand.getName());
        assertEquals("查看 API 调用成本统计", costCommand.getDescription());
        assertEquals(Command.CommandCategory.COST, costCommand.getCategory());
        assertTrue(costCommand.getAliases().contains("costs"));
        assertTrue(costCommand.getAliases().contains("expense"));
    }
    
    @Test
    @DisplayName("查看全部统计")
    void testExecuteAll() {
        CommandResult result = costCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("成本统计"));
    }
    
    @Test
    @DisplayName("查看今日统计")
    void testExecuteToday() {
        CommandResult result = costCommand.execute("today", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("今日"));
    }
    
    @Test
    @DisplayName("查看详细统计")
    void testExecuteDetail() {
        CommandResult result = costCommand.execute("--detail", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("详细"));
    }
    
    @Test
    @DisplayName("统计数据包含总成本")
    void testTotalCost() {
        CommandResult result = costCommand.execute("", context);
        
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.getData().get("stats");
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalCost"));
    }
    
    @Test
    @DisplayName("统计数据包含调用次数")
    void testTotalCalls() {
        CommandResult result = costCommand.execute("", context);
        
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.getData().get("stats");
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalCalls"));
    }
    
    @Test
    @DisplayName("按模型统计")
    void testByModel() {
        CommandResult result = costCommand.execute("", context);
        
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.getData().get("stats");
        assertNotNull(stats);
        assertTrue(stats.containsKey("byModel"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = costCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("cost"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("period"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(costCommand.getAliases().contains("costs"));
        assertTrue(costCommand.getAliases().contains("expense"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
