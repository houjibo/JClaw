package com.jclaw.agent.service;

import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.mapper.SubagentMapper;
import com.jclaw.agent.service.impl.SubagentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SubagentService 单元测试
 */
class SubagentServiceTest {

    @Mock
    private SubagentMapper subagentMapper;

    @InjectMocks
    private SubagentServiceImpl subagentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSubagent() {
        // 准备测试数据
        when(subagentMapper.insert(any(Subagent.class))).thenReturn(1);

        // 执行测试（API 调用失败不会阻止创建记录）
        Subagent result = subagentService.createSubagent(
            "parent-id", 
            "architect", 
            "设计数据库 schema"
        );

        // 验证结果
        assertNotNull(result);
        assertEquals("parent-id", result.getParentAgentId());
        assertEquals("architect", result.getRole());
        assertEquals("设计数据库 schema", result.getTask());
        assertEquals("pending", result.getStatus());
        assertNotNull(result.getCreatedAt());
        verify(subagentMapper, times(1)).insert(any(Subagent.class));
    }

    @Test
    void testGetSubagent() {
        // 准备测试数据
        Subagent subagent = new Subagent();
        subagent.setId("sub-001");
        subagent.setRole("fullstack");

        when(subagentMapper.selectById("sub-001")).thenReturn(subagent);

        // 执行测试
        Subagent result = subagentService.getSubagent("sub-001");

        // 验证结果
        assertNotNull(result);
        assertEquals("sub-001", result.getId());
        assertEquals("fullstack", result.getRole());
    }

    @Test
    void testListSubagents() {
        // 准备测试数据
        when(subagentMapper.selectList(any())).thenReturn(java.util.List.of());

        // 执行测试
        var result = subagentService.listSubagents("parent-id");

        // 验证结果
        assertNotNull(result);
        verify(subagentMapper, times(1)).selectList(any());
    }

    @Test
    void testUpdateStatus() {
        // 准备测试数据
        Subagent subagent = new Subagent();
        subagent.setId("sub-001");
        subagent.setStatus("pending");

        when(subagentMapper.selectById("sub-001")).thenReturn(subagent);
        when(subagentMapper.updateById(any(Subagent.class))).thenReturn(1);

        // 执行测试
        subagentService.updateStatus("sub-001", "running");

        // 验证结果
        assertEquals("running", subagent.getStatus());
        verify(subagentMapper, times(1)).updateById(any(Subagent.class));
    }

    @Test
    void testUpdateStatus_Completed() {
        // 准备测试数据
        Subagent subagent = new Subagent();
        subagent.setId("sub-001");
        subagent.setStatus("pending");

        when(subagentMapper.selectById("sub-001")).thenReturn(subagent);
        when(subagentMapper.updateById(any(Subagent.class))).thenReturn(1);

        // 执行测试
        subagentService.updateStatus("sub-001", "completed");

        // 验证结果
        assertEquals("completed", subagent.getStatus());
        assertNotNull(subagent.getCompletedAt());
    }

    @Test
    void testSubmitResult() {
        // 准备测试数据
        Subagent subagent = new Subagent();
        subagent.setId("sub-001");

        when(subagentMapper.selectById("sub-001")).thenReturn(subagent);
        when(subagentMapper.updateById(any(Subagent.class))).thenReturn(1);

        // 执行测试
        subagentService.submitResult("sub-001", "任务执行结果");

        // 验证结果
        assertEquals("任务执行结果", subagent.getOutput());
        verify(subagentMapper, times(1)).updateById(any(Subagent.class));
    }

    @Test
    void testWaitForCompletion_Completed() {
        // 准备测试数据
        Subagent subagent = new Subagent();
        subagent.setId("sub-001");
        subagent.setStatus("completed");

        when(subagentMapper.selectById("sub-001")).thenReturn(subagent);

        // 执行测试
        Subagent result = subagentService.waitForCompletion("sub-001", 1000);

        // 验证结果
        assertNotNull(result);
        assertEquals("completed", result.getStatus());
    }

    @Test
    void testWaitForCompletion_Timeout() {
        // 准备测试数据
        Subagent subagent = new Subagent();
        subagent.setId("sub-001");
        subagent.setStatus("running");

        when(subagentMapper.selectById("sub-001")).thenReturn(subagent);

        // 执行测试（超时返回 null）
        Subagent result = subagentService.waitForCompletion("sub-001", 100);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testWaitForCompletion_NotFound() {
        // 准备测试数据
        when(subagentMapper.selectById("not-found")).thenReturn(null);

        // 执行测试
        Subagent result = subagentService.waitForCompletion("not-found", 100);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testGetModelForRole() {
        // 测试模型选择逻辑（虽然是私有方法，但可以通过 createSubagent 间接验证）
        when(subagentMapper.insert((Subagent) any())).thenReturn(1);
        
        // 不同角色应该选择不同模型
        assertDoesNotThrow(() -> {
            subagentService.createSubagent("parent", "pm-qa", "task");
            subagentService.createSubagent("parent", "qa", "task");
            subagentService.createSubagent("parent", "architect", "task");
            subagentService.createSubagent("parent", "fullstack", "task");
            subagentService.createSubagent("parent", "devops", "task");
            subagentService.createSubagent("parent", "analyst", "task");
            subagentService.createSubagent("parent", "unknown", "task");
        });
    }
}
