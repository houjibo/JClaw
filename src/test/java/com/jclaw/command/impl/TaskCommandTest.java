package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("TaskCommand 测试")
class TaskCommandTest {
    
    private TaskCommand taskCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        taskCommand = new TaskCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("task", taskCommand.getName());
        assertEquals("任务管理（创建、列表、更新、停止、输出）", taskCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, taskCommand.getCategory());
        assertTrue(taskCommand.getAliases().contains("t"));
    }
    
    @Test
    @DisplayName("列出任务")
    void testListTasks() {
        CommandResult result = taskCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("任务列表"));
    }
    
    @Test
    @DisplayName("创建任务")
    void testCreateTask() {
        CommandResult result = taskCommand.execute("create 新任务", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已创建"));
    }
    
    @Test
    @DisplayName("获取任务")
    void testGetTask() {
        CommandResult result = taskCommand.execute("get task-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("更新任务")
    void testUpdateTask() {
        CommandResult result = taskCommand.execute("update task-001 running", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已更新"));
    }
    
    @Test
    @DisplayName("停止任务")
    void testStopTask() {
        CommandResult result = taskCommand.execute("stop task-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已停止"));
    }
    
    @Test
    @DisplayName("获取任务输出")
    void testGetTaskOutput() {
        CommandResult result = taskCommand.execute("output task-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("任务输出"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = taskCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("task"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(taskCommand.getAliases().contains("t"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
