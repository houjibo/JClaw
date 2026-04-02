package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CoordinatorCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CoordinatorCommand 测试")
class CoordinatorCommandTest {
    
    private CoordinatorCommand coordinatorCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        coordinatorCommand = new CoordinatorCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("coordinator", coordinatorCommand.getName());
        assertEquals("多 Agent 协调", coordinatorCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, coordinatorCommand.getCategory());
        assertTrue(coordinatorCommand.getAliases().contains("coord"));
        assertTrue(coordinatorCommand.getAliases().contains("co"));
    }
    
    @Test
    @DisplayName("显示状态")
    void testShowStatus() {
        CommandResult result = coordinatorCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("Coordinator 状态"));
    }
    
    @Test
    @DisplayName("分配任务")
    void testAssignTask() {
        CommandResult result = coordinatorCommand.execute("assign 代码审查", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已分配"));
    }
    
    @Test
    @DisplayName("执行任务")
    void testExecuteTask() {
        CommandResult result = coordinatorCommand.execute("execute 生成测试", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("执行中"));
    }
    
    @Test
    @DisplayName("查看结果")
    void testShowResult() {
        CommandResult result = coordinatorCommand.execute("result task-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("任务结果"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少任务描述")
    void testMissingTask() {
        CommandResult result = coordinatorCommand.execute("assign", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 缺少任务 ID")
    void testMissingTaskId() {
        CommandResult result = coordinatorCommand.execute("result", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = coordinatorCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("coordinator"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(coordinatorCommand.getAliases().contains("coord"));
        assertTrue(coordinatorCommand.getAliases().contains("co"));
    }
    
    @Test
    @DisplayName("AgentCoordinator 功能测试")
    void testAgentCoordinator() {
        CoordinatorCommand.AgentCoordinator coordinator = new CoordinatorCommand.AgentCoordinator();
        
        assertEquals(3, coordinator.getActiveAgents());
        assertEquals(0, coordinator.getPendingTasks());
        assertEquals(0, coordinator.getCompletedTasks());
        
        String taskId = coordinator.assignTask("test task");
        assertNotNull(taskId);
        assertTrue(taskId.startsWith("task-"));
        
        String execId = coordinator.executeTask("exec task");
        assertNotNull(execId);
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
