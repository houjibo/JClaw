package com.openclaw.jcode.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Agent 协调器测试
 */
@DisplayName("Agent 协调器测试")
class AgentCoordinatorTest {
    
    private AgentCoordinator coordinator;
    
    @BeforeEach
    void setUp() {
        coordinator = new AgentCoordinator();
    }
    
    @Test
    @DisplayName("测试创建 Agent")
    void testCreateAgent() {
        AgentCoordinator.AgentInstance agent = coordinator.createAgent("developer", "qwen3.5-plus");
        
        assertNotNull(agent);
        assertNotNull(agent.id);
        assertEquals("developer", agent.role);
        assertEquals("qwen3.5-plus", agent.model);
        assertEquals("idle", agent.status);
        assertEquals(0, agent.completedTasks);
    }
    
    @Test
    @DisplayName("测试列出 Agent")
    void testListAgents() {
        coordinator.createAgent("developer", "qwen3.5-plus");
        coordinator.createAgent("reviewer", "kimi-k2.5");
        
        List<Map<String, Object>> agents = coordinator.listAgents();
        
        assertEquals(2, agents.size());
    }
    
    @Test
    @DisplayName("测试删除 Agent")
    void testDeleteAgent() {
        AgentCoordinator.AgentInstance agent = coordinator.createAgent("developer", "qwen3.5-plus");
        
        boolean deleted = coordinator.deleteAgent(agent.id);
        
        assertTrue(deleted);
        assertEquals(0, coordinator.listAgents().size());
    }
    
    @Test
    @DisplayName("测试删除不存在的 Agent")
    void testDeleteNonExistentAgent() {
        boolean deleted = coordinator.deleteAgent("non-exist");
        
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("测试创建任务")
    void testCreateTask() {
        AgentCoordinator.TaskInstance task = coordinator.createTask("代码审查");
        
        assertNotNull(task);
        assertNotNull(task.id);
        assertEquals("代码审查", task.description);
        assertEquals("pending", task.status);
        assertNull(task.assignedTo);
    }
    
    @Test
    @DisplayName("测试分配任务给指定 Agent")
    void testAssignTask() {
        // 创建 Agent
        AgentCoordinator.AgentInstance agent = coordinator.createAgent("developer", "qwen3.5-plus");
        
        // 创建任务
        AgentCoordinator.TaskInstance task = coordinator.createTask("开发功能");
        
        // 分配任务
        boolean assigned = coordinator.assignTask(task.id, agent.id);
        
        assertTrue(assigned);
        assertEquals("busy", agent.status);
        assertEquals("running", task.status);
        assertEquals(agent.id, task.assignedTo);
    }
    
    @Test
    @DisplayName("测试分配任务给不存在的 Agent")
    void testAssignTaskToNonExistentAgent() {
        AgentCoordinator.TaskInstance task = coordinator.createTask("开发功能");
        
        boolean assigned = coordinator.assignTask(task.id, "non-exist");
        
        assertFalse(assigned);
    }
    
    @Test
    @DisplayName("测试自动分配任务")
    void testAutoAssignTask() {
        // 创建 Agent
        coordinator.createAgent("developer", "qwen3.5-plus");
        
        // 创建任务
        AgentCoordinator.TaskInstance task = coordinator.createTask("开发功能");
        
        // 自动分配
        String assignedAgentId = coordinator.autoAssignTask(task.id, "developer");
        
        assertNotNull(assignedAgentId);
        assertEquals("running", task.status);
    }
    
    @Test
    @DisplayName("测试完成任务")
    void testCompleteTask() {
        // 创建 Agent 和任务
        AgentCoordinator.AgentInstance agent = coordinator.createAgent("developer", "qwen3.5-plus");
        AgentCoordinator.TaskInstance task = coordinator.createTask("开发功能");
        coordinator.assignTask(task.id, agent.id);
        
        // 完成任务
        coordinator.completeTask(task.id, "功能开发完成");
        
        assertEquals("idle", agent.status);
        assertNull(agent.currentTask);
        assertEquals(1, agent.completedTasks);
        assertEquals("completed", task.status);
        assertNotNull(task.result);
    }
    
    @Test
    @DisplayName("测试失败任务")
    void testFailTask() {
        // 创建 Agent 和任务
        AgentCoordinator.AgentInstance agent = coordinator.createAgent("developer", "qwen3.5-plus");
        AgentCoordinator.TaskInstance task = coordinator.createTask("开发功能");
        coordinator.assignTask(task.id, agent.id);
        
        // 失败任务
        coordinator.failTask(task.id, "编译失败");
        
        assertEquals("idle", agent.status);
        assertEquals("failed", task.status);
        assertEquals("编译失败", task.result);
    }
    
    @Test
    @DisplayName("测试获取任务状态")
    void testGetTaskStatus() {
        AgentCoordinator.TaskInstance task = coordinator.createTask("开发功能");
        
        Map<String, Object> status = coordinator.getTaskStatus(task.id);
        
        assertNotNull(status);
        assertEquals(task.id, status.get("id"));
        assertEquals("pending", status.get("status"));
    }
    
    @Test
    @DisplayName("测试获取统计信息")
    void testGetStats() {
        // 创建一些数据
        coordinator.createAgent("developer", "qwen3.5-plus");
        coordinator.createAgent("reviewer", "kimi-k2.5");
        coordinator.createTask("任务 1");
        coordinator.createTask("任务 2");
        
        Map<String, Object> stats = coordinator.getStats();
        
        assertEquals(2, stats.get("totalAgents"));
        assertEquals(2, stats.get("totalTasks"));
        assertEquals(2, stats.get("pendingTasks"));
    }
    
    @Test
    @DisplayName("测试 Agent 间消息发送")
    void testSendMessage() {
        AgentCoordinator.AgentInstance agent1 = coordinator.createAgent("developer", "qwen3.5-plus");
        AgentCoordinator.AgentInstance agent2 = coordinator.createAgent("reviewer", "kimi-k2.5");
        
        coordinator.sendMessage(agent1.id, agent2.id, "Hello");
        
        // 接收消息
        Map<String, Object> message = coordinator.receiveMessage();
        
        assertNotNull(message);
        assertEquals(agent1.id, message.get("from"));
        assertEquals(agent2.id, message.get("to"));
        assertEquals("Hello", message.get("message"));
    }
}
