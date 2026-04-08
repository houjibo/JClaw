package com.jclaw.controller;

import com.jclaw.command.Command;
import com.jclaw.command.CommandRegistry;
import com.jclaw.command.CommandResult;
import com.jclaw.command.CommandContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CommandController 单元测试（JDK 21 + Mockito）
 */
@DisplayName("CommandController 单元测试")
class CommandControllerTest {

    @Mock
    private CommandRegistry commandRegistry;

    @InjectMocks
    private CommandController commandController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试列出所有命令 - 无分类过滤")
    void testListCommands_NoCategory() {
        // Arrange
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("commit");
        when(mockCommand.getCategory()).thenReturn(Command.CommandCategory.GIT);
        
        List<Command> mockCommands = Arrays.asList(mockCommand);
        Map<String, Object> mockStats = Map.of("total", 1);
        when(commandRegistry.listCommands()).thenReturn(mockCommands);
        when(commandRegistry.getStats()).thenReturn(mockStats);

        // Act
        Map<String, Object> result = commandController.listCommands(null);

        // Assert
        assertEquals(1, ((List<?>) result.get("commands")).size());
        assertEquals(1, result.get("total"));
        assertNotNull(result.get("stats"));
        verify(commandRegistry, times(1)).listCommands();
    }

    @Test
    @DisplayName("测试列出命令 - 按分类过滤")
    void testListCommands_WithCategory() {
        // Arrange
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("commit");
        when(mockCommand.getCategory()).thenReturn(Command.CommandCategory.GIT);
        
        List<Command> mockCommands = Arrays.asList(mockCommand);
        when(commandRegistry.listCommandsByCategory(Command.CommandCategory.GIT))
            .thenReturn(mockCommands);

        // Act
        Map<String, Object> result = commandController.listCommands("GIT");

        // Assert
        assertEquals(1, ((List<?>) result.get("commands")).size());
        verify(commandRegistry, times(1)).listCommandsByCategory(Command.CommandCategory.GIT);
    }

    @Test
    @DisplayName("测试列出命令 - 无效分类")
    void testListCommands_InvalidCategory() {
        // Act
        Map<String, Object> result = commandController.listCommands("INVALID");

        // Assert
        assertNotNull(result.get("error"));
        assertTrue(((String) result.get("error")).contains("无效的类别"));
    }

    @Test
    @DisplayName("测试获取命令详情 - 存在")
    void testGetCommand_Exists() {
        // Arrange
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("commit");
        when(mockCommand.getDescription()).thenReturn("Git 提交");
        when(mockCommand.getCategory()).thenReturn(Command.CommandCategory.GIT);
        when(commandRegistry.getCommand("commit")).thenReturn(mockCommand);

        // Act
        Map<String, Object> result = commandController.getCommand("commit");

        // Assert
        assertEquals("commit", result.get("name"));
        assertEquals("Git 提交", result.get("description"));
        assertEquals("GIT", result.get("category"));
    }

    @Test
    @DisplayName("测试获取命令详情 - 不存在")
    void testGetCommand_NotExists() {
        // Arrange
        when(commandRegistry.getCommand("nonexistent")).thenReturn(null);

        // Act
        Map<String, Object> result = commandController.getCommand("nonexistent");

        // Assert
        assertTrue(((String) result.get("error")).contains("命令不存在"));
    }

    @Test
    @DisplayName("测试执行命令 - 成功")
    void testExecuteCommand_Success() {
        // Arrange
        Map<String, Object> params = new HashMap<>();
        params.put("args", "-m 'Initial commit'");
        
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("commit");
        when(mockCommand.isRequiresConfirmation()).thenReturn(false);
        when(commandRegistry.getCommand("commit")).thenReturn(mockCommand);
        when(mockCommand.execute(anyString(), any(CommandContext.class)))
            .thenReturn(CommandResult.success("提交成功"));

        // Act
        Map<String, Object> result = commandController.executeCommand("commit", params);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("提交成功", result.get("message"));
    }

    @Test
    @DisplayName("测试执行命令 - 命令不存在")
    void testExecuteCommand_NotExists() {
        // Arrange
        when(commandRegistry.getCommand("nonexistent")).thenReturn(null);

        // Act
        Map<String, Object> result = commandController.executeCommand("nonexistent", new HashMap<>());

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("命令不存在"));
    }

    @Test
    @DisplayName("测试执行命令 - 需要确认")
    void testExecuteCommand_RequiresConfirmation() {
        // Arrange
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("rm");
        when(mockCommand.isRequiresConfirmation()).thenReturn(true);
        when(commandRegistry.getCommand("rm")).thenReturn(mockCommand);

        // Act
        Map<String, Object> result = commandController.executeCommand("rm", new HashMap<>());

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue((Boolean) result.get("requiresConfirmation"));
        assertEquals("此命令需要确认", result.get("message"));
    }

    @Test
    @DisplayName("测试执行命令 - 参数为 null")
    void testExecuteCommand_NullParams() {
        // Arrange
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("status");
        when(mockCommand.isRequiresConfirmation()).thenReturn(false);
        when(commandRegistry.getCommand("status")).thenReturn(mockCommand);
        when(mockCommand.execute(anyString(), any(CommandContext.class)))
            .thenReturn(CommandResult.success("状态正常"));

        // Act
        Map<String, Object> result = commandController.executeCommand("status", null);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("状态正常", result.get("message"));
    }
}
