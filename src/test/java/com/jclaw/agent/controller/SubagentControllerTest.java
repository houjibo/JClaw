package com.jclaw.agent.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.service.SubagentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SubagentController 单元测试
 */
@DisplayName("SubagentController 单元测试")
class SubagentControllerTest {

    @Mock
    private SubagentService subagentService;

    @InjectMocks
    private com.jclaw.agent.controller.SubagentController subagentController;

    private Subagent mockSubagent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        mockSubagent = new Subagent();
        mockSubagent.setId("subagent-123");
        mockSubagent.setParentAgentId("main-agent");
        mockSubagent.setRole("architect");
        mockSubagent.setTask("设计系统架构");
        mockSubagent.setStatus("running");
        mockSubagent.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("测试创建 Subagent")
    void testCreateSubagent() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("parentAgentId", "main-agent");
        request.put("role", "architect");
        request.put("task", "设计系统架构");
        
        when(subagentService.createSubagent("main-agent", "architect", "设计系统架构"))
            .thenReturn(mockSubagent);

        // Act
        Result<Subagent> result = subagentController.createSubagent(request);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("subagent-123", result.getData().getId());
        verify(subagentService, times(1)).createSubagent("main-agent", "architect", "设计系统架构");
    }

    @Test
    @DisplayName("测试获取 Subagent - 存在")
    void testGetSubagent_Exists() {
        // Arrange
        when(subagentService.getSubagent("subagent-123")).thenReturn(mockSubagent);

        // Act
        Result<Subagent> result = subagentController.getSubagent("subagent-123");

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("subagent-123", result.getData().getId());
        verify(subagentService, times(1)).getSubagent("subagent-123");
    }

    @Test
    @DisplayName("测试获取 Subagent - 不存在")
    void testGetSubagent_NotExists() {
        // Arrange
        when(subagentService.getSubagent("not-found")).thenReturn(null);

        // Act
        Result<Subagent> result = subagentController.getSubagent("not-found");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Subagent 不存在", result.getMessage());
        verify(subagentService, times(1)).getSubagent("not-found");
    }

    @Test
    @DisplayName("测试列出 Subagent - 带父 ID")
    void testListSubagents_WithParentId() {
        // Arrange
        List<Subagent> subagents = Arrays.asList(mockSubagent);
        when(subagentService.listSubagents("main-agent")).thenReturn(subagents);

        // Act
        Result<List<Subagent>> result = subagentController.listSubagents("main-agent");

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(subagentService, times(1)).listSubagents("main-agent");
    }

    @Test
    @DisplayName("测试列出 Subagent - 不带父 ID")
    void testListSubagents_WithoutParentId() {
        // Arrange
        List<Subagent> subagents = Arrays.asList(mockSubagent);
        when(subagentService.listSubagents(null)).thenReturn(subagents);

        // Act
        Result<List<Subagent>> result = subagentController.listSubagents(null);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(subagentService, times(1)).listSubagents(null);
    }

    @Test
    @DisplayName("测试更新 Subagent 状态")
    void testUpdateStatus() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("status", "completed");
        
        doNothing().when(subagentService).updateStatus("subagent-123", "completed");

        // Act
        Result<Void> result = subagentController.updateStatus("subagent-123", request);

        // Assert
        assertTrue(result.isSuccess());
        verify(subagentService, times(1)).updateStatus("subagent-123", "completed");
    }

    @Test
    @DisplayName("测试提交 Subagent 结果")
    void testSubmitResult() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("output", "任务完成输出");
        
        doNothing().when(subagentService).submitResult("subagent-123", "任务完成输出");

        // Act
        Result<Void> result = subagentController.submitResult("subagent-123", request);

        // Assert
        assertTrue(result.isSuccess());
        verify(subagentService, times(1)).submitResult("subagent-123", "任务完成输出");
    }

    @Test
    @DisplayName("测试等待 Subagent 完成 - 成功")
    void testWaitForCompletion_Success() {
        // Arrange
        when(subagentService.waitForCompletion("subagent-123", 300000)).thenReturn(mockSubagent);

        // Act
        Result<Subagent> result = subagentController.waitForCompletion("subagent-123", 300000);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("subagent-123", result.getData().getId());
        verify(subagentService, times(1)).waitForCompletion("subagent-123", 300000);
    }

    @Test
    @DisplayName("测试等待 Subagent 完成 - 超时")
    void testWaitForCompletion_Timeout() {
        // Arrange
        when(subagentService.waitForCompletion("subagent-123", 100)).thenReturn(null);

        // Act
        Result<Subagent> result = subagentController.waitForCompletion("subagent-123", 100);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("等待超时", result.getMessage());
        verify(subagentService, times(1)).waitForCompletion("subagent-123", 100);
    }
}
