package com.jclaw.controller;

import com.jclaw.services.AgentCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AgentController 单元测试
 */
@DisplayName("AgentController 单元测试")
class AgentControllerTest {

    @Mock
    private AgentCoordinator coordinator;

    @InjectMocks
    private AgentController agentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试列出所有 Agent")
    void testListAgents() {
        // Arrange
        List<Map<String, Object>> mockAgents = Arrays.asList(
            Map.of("id", "agent-1", "role", "architect", "model", "qwen3.5-plus", "status", "idle")
        );
        when(coordinator.listAgents()).thenReturn(mockAgents);

        // Act
        List<Map<String, Object>> result = agentController.listAgents();

        // Assert
        assertEquals(1, result.size());
        verify(coordinator, times(1)).listAgents();
    }

    @Test
    @DisplayName("测试创建 Agent - 成功")
    void testCreateAgent_Success() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("role", "architect");
        config.put("model", "qwen3.5-plus");
        
        AgentCoordinator.AgentInstance mockAgent = 
            new AgentCoordinator.AgentInstance("agent-1", "architect", "qwen3.5-plus");
        when(coordinator.createAgent("architect", "qwen3.5-plus")).thenReturn(mockAgent);

        // Act
        Map<String, Object> result = agentController.createAgent(config);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("agent"));
        verify(coordinator, times(1)).createAgent("architect", "qwen3.5-plus");
    }

    @Test
    @DisplayName("测试创建 Agent - role 为空")
    void testCreateAgent_MissingRole() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("model", "qwen3.5-plus");

        // Act
        Map<String, Object> result = agentController.createAgent(config);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("role 不能为空"));
        verify(coordinator, never()).createAgent(anyString(), anyString());
    }

    @Test
    @DisplayName("测试删除 Agent - 成功")
    void testDeleteAgent_Success() {
        // Arrange
        when(coordinator.deleteAgent("agent-1")).thenReturn(true);

        // Act
        Map<String, Object> result = agentController.deleteAgent("agent-1");

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("agent-1", result.get("agentId"));
        verify(coordinator, times(1)).deleteAgent("agent-1");
    }

    @Test
    @DisplayName("测试删除 Agent - 失败")
    void testDeleteAgent_Failure() {
        // Arrange
        when(coordinator.deleteAgent("agent-1")).thenReturn(false);

        // Act
        Map<String, Object> result = agentController.deleteAgent("agent-1");

        // Assert
        assertFalse((Boolean) result.get("success"));
        verify(coordinator, times(1)).deleteAgent("agent-1");
    }

    @Test
    @DisplayName("测试创建任务 - 成功")
    void testCreateTask_Success() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("description", "设计系统架构");
        
        AgentCoordinator.TaskInstance mockTask = 
            new AgentCoordinator.TaskInstance("task-1", "设计系统架构");
        when(coordinator.createTask("设计系统架构")).thenReturn(mockTask);

        // Act
        Map<String, Object> result = agentController.createTask(config);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("task"));
        verify(coordinator, times(1)).createTask("设计系统架构");
    }

    @Test
    @DisplayName("测试创建任务 - description 为空")
    void testCreateTask_MissingDescription() {
        // Arrange
        Map<String, String> config = new HashMap<>();

        // Act
        Map<String, Object> result = agentController.createTask(config);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("description 不能为空"));
        verify(coordinator, never()).createTask(anyString());
    }

    @Test
    @DisplayName("测试分配任务 - 自动分配成功")
    void testAssignTask_AutoAssign_Success() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("role", "architect");
        when(coordinator.autoAssignTask("task-1", "architect")).thenReturn("agent-1");

        // Act
        Map<String, Object> result = agentController.assignTask("task-1", config);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("agent-1", result.get("assignedTo"));
        verify(coordinator, times(1)).autoAssignTask("task-1", "architect");
    }

    @Test
    @DisplayName("测试分配任务 - 指定 Agent 成功")
    void testAssignTask_SpecifiedAgent_Success() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("agentId", "agent-1");
        when(coordinator.assignTask("task-1", "agent-1")).thenReturn(true);

        // Act
        Map<String, Object> result = agentController.assignTask("task-1", config);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("task-1", result.get("taskId"));
        assertEquals("agent-1", result.get("assignedTo"));
        verify(coordinator, times(1)).assignTask("task-1", "agent-1");
    }

    @Test
    @DisplayName("测试分配任务 - 自动分配失败")
    void testAssignTask_AutoAssign_Failure() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("role", "architect");
        when(coordinator.autoAssignTask("task-1", "architect")).thenReturn(null);

        // Act
        Map<String, Object> result = agentController.assignTask("task-1", config);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("没有可用的 Agent"));
        verify(coordinator, times(1)).autoAssignTask("task-1", "architect");
    }

    @Test
    @DisplayName("测试获取任务状态 - 存在")
    void testGetTaskStatus_Exists() {
        // Arrange
        Map<String, Object> mockStatus = Map.of(
            "id", "task-1",
            "description", "设计系统架构",
            "status", "running"
        );
        when(coordinator.getTaskStatus("task-1")).thenReturn(mockStatus);

        // Act
        Map<String, Object> result = agentController.getTaskStatus("task-1");

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals(mockStatus, result.get("task"));
        verify(coordinator, times(1)).getTaskStatus("task-1");
    }

    @Test
    @DisplayName("测试获取任务状态 - 不存在")
    void testGetTaskStatus_NotExists() {
        // Arrange
        when(coordinator.getTaskStatus("task-1")).thenReturn(null);

        // Act
        Map<String, Object> result = agentController.getTaskStatus("task-1");

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("任务不存在"));
        verify(coordinator, times(1)).getTaskStatus("task-1");
    }

    @Test
    @DisplayName("测试获取任务结果 - 存在")
    void testGetTaskResult_Exists() {
        // Arrange
        when(coordinator.getTaskResult("task-1")).thenReturn("任务完成结果");

        // Act
        Map<String, Object> result = agentController.getTaskResult("task-1");

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("任务完成结果", result.get("result"));
        verify(coordinator, times(1)).getTaskResult("task-1");
    }

    @Test
    @DisplayName("测试获取任务结果 - 不存在")
    void testGetTaskResult_NotExists() {
        // Arrange
        when(coordinator.getTaskResult("task-1")).thenReturn(null);

        // Act
        Map<String, Object> result = agentController.getTaskResult("task-1");

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("任务结果不存在"));
        verify(coordinator, times(1)).getTaskResult("task-1");
    }

    @Test
    @DisplayName("测试获取统计信息")
    void testGetStats() {
        // Arrange
        Map<String, Object> mockStats = Map.of(
            "totalAgents", 5,
            "activeTasks", 3,
            "completedTasks", 10
        );
        when(coordinator.getStats()).thenReturn(mockStats);

        // Act
        Map<String, Object> result = agentController.getStats();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.get("totalAgents"));
        verify(coordinator, times(1)).getStats();
    }

    @Test
    @DisplayName("测试发送 Agent 间消息 - 成功")
    void testSendMessage_Success() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("from", "agent-1");
        config.put("to", "agent-2");
        config.put("message", "Hello");
        
        doNothing().when(coordinator).sendMessage("agent-1", "agent-2", "Hello");

        // Act
        Map<String, Object> result = agentController.sendMessage(config);

        // Assert
        assertTrue((Boolean) result.get("success"));
        verify(coordinator, times(1)).sendMessage("agent-1", "agent-2", "Hello");
    }

    @Test
    @DisplayName("测试发送 Agent 间消息 - 缺少参数")
    void testSendMessage_MissingParams() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("from", "agent-1");
        // to 缺失

        // Act
        Map<String, Object> result = agentController.sendMessage(config);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("from, to, message 不能为空"));
        verify(coordinator, never()).sendMessage(anyString(), anyString(), anyString());
    }
}
