package com.jclaw.agent.coordinator;

import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.service.SubagentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 联军架构协调器测试
 */
@DisplayName("联军架构协调器测试")
class ArmyCoordinatorTest {
    
    @Mock
    private SubagentService subagentService;
    
    @InjectMocks
    private ArmyCoordinator armyCoordinator;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("测试创建联军任务")
    void testCreateArmyTask() {
        // Arrange
        Subagent mockAgent = new Subagent();
        mockAgent.setId("agent-001");
        mockAgent.setStatus("pending");
        
        when(subagentService.createSubagent(any(), any(), any())).thenReturn(mockAgent);
        
        // Act
        ArmyCoordinator.ArmyContext context = armyCoordinator.createArmyTask(
            "task-001",
            "开发用户管理功能",
            Arrays.asList(ArmyCoordinator.ArmyRole.FULLSTACK, ArmyCoordinator.ArmyRole.PM_QA)
        );
        
        // Assert
        assertNotNull(context);
        assertEquals("task-001", context.getTaskId());
        assertEquals("running", context.getStatus()); // 创建后自动启动
        assertEquals(2, context.getAgents().size());
        
        verify(subagentService, times(2)).createSubagent(any(), any(), any());
    }
    
    @Test
    @DisplayName("测试标准作战流程")
    void testStandardBattle() {
        // Arrange
        Subagent mockAgent = new Subagent();
        mockAgent.setId("agent-001");
        mockAgent.setStatus("completed");
        mockAgent.setOutput("任务完成");
        
        when(subagentService.createSubagent(any(), any(), any())).thenReturn(mockAgent);
        
        // Act
        ArmyCoordinator.ArmyContext context = armyCoordinator.standardBattle(
            "battle-001",
            "完整开发流程",
            5000
        );
        
        // Assert
        assertNotNull(context);
        assertEquals("battle-001", context.getTaskId());
        assertTrue(context.getAgents().size() >= 1); // 至少有一个角色
        
        verify(subagentService, times(context.getAgents().size())).createSubagent(any(), any(), any());
    }
    
    @Test
    @DisplayName("测试获取任务状态")
    void testGetTaskStatus() {
        // Arrange
        Subagent mockAgent = new Subagent();
        mockAgent.setId("agent-001");
        mockAgent.setStatus("pending");
        
        when(subagentService.createSubagent(any(), any(), any())).thenReturn(mockAgent);
        
        armyCoordinator.createArmyTask("task-002", "测试任务", Arrays.asList(ArmyCoordinator.ArmyRole.ARCHITECT));
        
        // Act
        ArmyCoordinator.ArmyContext context = armyCoordinator.getTaskStatus("task-002");
        
        // Assert
        assertNotNull(context);
        assertEquals("task-002", context.getTaskId());
    }
    
    @Test
    @DisplayName("测试获取不存在的任务")
    void testGetNonExistentTask() {
        // Act
        ArmyCoordinator.ArmyContext context = armyCoordinator.getTaskStatus("non-existent");
        
        // Assert
        assertNull(context);
    }
    
    @Test
    @DisplayName("测试列出活跃任务")
    void testListActiveTasks() {
        // Arrange
        Subagent mockAgent = new Subagent();
        mockAgent.setId("agent-001");
        mockAgent.setStatus("pending");
        
        when(subagentService.createSubagent(any(), any(), any())).thenReturn(mockAgent);
        
        armyCoordinator.createArmyTask("task-003", "任务 1", Arrays.asList(ArmyCoordinator.ArmyRole.FULLSTACK));
        armyCoordinator.createArmyTask("task-004", "任务 2", Arrays.asList(ArmyCoordinator.ArmyRole.DEVOPS));
        
        // Act
        List<ArmyCoordinator.ArmyContext> tasks = armyCoordinator.listActiveTasks();
        
        // Assert
        assertTrue(tasks.size() >= 2);
    }
    
    @Test
    @DisplayName("测试五大角色枚举")
    void testArmyRoles() {
        // Act & Assert
        ArmyCoordinator.ArmyRole[] roles = ArmyCoordinator.ArmyRole.values();
        
        assertEquals(5, roles.length);
        assertNotNull(ArmyCoordinator.ArmyRole.PM_QA);
        assertNotNull(ArmyCoordinator.ArmyRole.ARCHITECT);
        assertNotNull(ArmyCoordinator.ArmyRole.FULLSTACK);
        assertNotNull(ArmyCoordinator.ArmyRole.DEVOPS);
        assertNotNull(ArmyCoordinator.ArmyRole.ANALYST);
        
        // 验证角色代码
        assertEquals("pm-qa", ArmyCoordinator.ArmyRole.PM_QA.getCode());
        assertEquals("architect", ArmyCoordinator.ArmyRole.ARCHITECT.getCode());
        assertEquals("fullstack", ArmyCoordinator.ArmyRole.FULLSTACK.getCode());
        assertEquals("devops", ArmyCoordinator.ArmyRole.DEVOPS.getCode());
        assertEquals("analyst", ArmyCoordinator.ArmyRole.ANALYST.getCode());
    }
}
