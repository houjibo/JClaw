package com.jclaw.controller;

import com.jclaw.services.McpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * McpController 单元测试（纯单元测试，不使用 Spring）
 */
@DisplayName("McpController 单元测试")
@ExtendWith(MockitoExtension.class)
class McpControllerTest {

    @Mock
    private McpService mcpService;

    @InjectMocks
    private McpController mcpController;

    @Test
    @DisplayName("测试列出 MCP 服务器")
    void testListServers() {
        // Arrange
        List<Map<String, Object>> mockServers = Arrays.asList(
            Map.of("name", "filesystem", "url", "http://localhost:3000"),
            Map.of("name", "database", "url", "http://localhost:3001")
        );
        when(mcpService.listServers()).thenReturn(mockServers);

        // Act
        List<Map<String, Object>> result = mcpController.listServers();

        // Assert
        assertEquals(2, result.size());
        verify(mcpService, times(1)).listServers();
    }

    @Test
    @DisplayName("测试注册 MCP 服务器 - 成功")
    void testRegisterServer_Success() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("name", "test-server");
        config.put("url", "http://localhost:3000");
        doNothing().when(mcpService).registerServer("test-server", "http://localhost:3000");

        // Act
        Map<String, Object> result = mcpController.registerServer(config);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("test-server", result.get("name"));
        assertEquals("http://localhost:3000", result.get("url"));
        verify(mcpService, times(1)).registerServer("test-server", "http://localhost:3000");
    }

    @Test
    @DisplayName("测试注册 MCP 服务器 - 缺少 name")
    void testRegisterServer_MissingName() {
        // Arrange
        Map<String, String> config = new HashMap<>();
        config.put("url", "http://localhost:3000");

        // Act
        Map<String, Object> result = mcpController.registerServer(config);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("name 和 url 不能为空"));
        verify(mcpService, never()).registerServer(anyString(), anyString());
    }

    @Test
    @DisplayName("测试列出 MCP 工具")
    void testListTools() {
        // Arrange
        List<Map<String, Object>> mockTools = Arrays.asList(
            Map.of("name", "read_file", "description", "读取文件"),
            Map.of("name", "write_file", "description", "写入文件")
        );
        when(mcpService.listTools("filesystem")).thenReturn(mockTools);

        // Act
        List<Map<String, Object>> result = mcpController.listTools("filesystem");

        // Assert
        assertEquals(2, result.size());
        verify(mcpService, times(1)).listTools("filesystem");
    }

    @Test
    @DisplayName("测试列出 MCP 资源")
    void testListResources() {
        // Arrange
        List<Map<String, Object>> mockResources = Arrays.asList(
            Map.of("uri", "file:///etc/hosts", "name", "hosts")
        );
        when(mcpService.listResources("filesystem")).thenReturn(mockResources);

        // Act
        List<Map<String, Object>> result = mcpController.listResources("filesystem");

        // Assert
        assertEquals(1, result.size());
        verify(mcpService, times(1)).listResources("filesystem");
    }

    @Test
    @DisplayName("测试列出 MCP 提示词")
    void testListPrompts() {
        // Arrange
        List<Map<String, Object>> mockPrompts = Arrays.asList(
            Map.of("name", "code_review", "description", "代码审查提示词")
        );
        when(mcpService.listPrompts("assistant")).thenReturn(mockPrompts);

        // Act
        List<Map<String, Object>> result = mcpController.listPrompts("assistant");

        // Assert
        assertEquals(1, result.size());
        verify(mcpService, times(1)).listPrompts("assistant");
    }

    @Test
    @DisplayName("测试调用 MCP 工具 - 成功")
    void testCallTool_Success() {
        // Arrange
        Map<String, Object> args = new HashMap<>();
        args.put("path", "test.txt");
        Map<String, Object> mockResult = Map.of("content", "文件内容");
        when(mcpService.callTool("filesystem", "read_file", args)).thenReturn(mockResult);

        // Act
        Map<String, Object> result = mcpController.callTool("filesystem", "read_file", args);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("filesystem", result.get("server"));
        assertEquals("read_file", result.get("tool"));
        assertNotNull(result.get("result"));
        verify(mcpService, times(1)).callTool("filesystem", "read_file", args);
    }

    @Test
    @DisplayName("测试调用 MCP 工具 - 失败")
    void testCallTool_Failure() {
        // Arrange
        when(mcpService.callTool(anyString(), anyString(), anyMap()))
            .thenThrow(new RuntimeException("工具调用失败"));

        // Act
        Map<String, Object> result = mcpController.callTool("filesystem", "invalid_tool", new HashMap<>());

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("工具调用失败"));
        verify(mcpService, times(1)).callTool(anyString(), anyString(), anyMap());
    }

    @Test
    @DisplayName("测试读取 MCP 资源 - 成功")
    void testReadResource_Success() {
        // Arrange
        String serverName = "filesystem";
        String uri = "file:///etc/hosts";
        String mockContent = "127.0.0.1 localhost";
        
        when(mcpService.readResource(eq(serverName), eq(uri)))
            .thenReturn(mockContent);

        // Act
        Map<String, Object> result = mcpController.readResource(serverName, uri);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals(serverName, result.get("server"));
        assertEquals(uri, result.get("uri"));
        assertEquals(mockContent, result.get("content"));
        verify(mcpService, times(1)).readResource(eq(serverName), eq(uri));
    }

    @Test
    @DisplayName("测试读取 MCP 资源 - 失败")
    void testReadResource_Failure() {
        // Arrange
        String serverName = "filesystem";
        String uri = "invalid-uri";
        
        when(mcpService.readResource(eq(serverName), eq(uri)))
            .thenThrow(new RuntimeException("资源不存在"));

        // Act
        Map<String, Object> result = mcpController.readResource(serverName, uri);

        // Assert
        assertFalse((Boolean) result.get("success"));
        assertTrue(((String) result.get("error")).contains("资源不存在"));
        verify(mcpService, times(1)).readResource(eq(serverName), eq(uri));
    }

    @Test
    @DisplayName("测试获取 MCP 提示词")
    void testGetPrompt() {
        // Arrange
        Map<String, Object> mockPrompt = Map.of("messages", Arrays.asList("请审查代码"));
        when(mcpService.getPrompt("assistant", "code_review", new HashMap<>()))
            .thenReturn(mockPrompt);

        // Act
        Map<String, Object> result = mcpController.getPrompt("assistant", "code_review", new HashMap<>());

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("assistant", result.get("server"));
        assertEquals("code_review", result.get("prompt"));
        verify(mcpService, times(1)).getPrompt("assistant", "code_review", new HashMap<>());
    }

    @Test
    @DisplayName("测试切换 MCP 服务器状态 - 启用")
    void testToggleServer_Enable() {
        // Arrange
        doNothing().when(mcpService).setServerEnabled("filesystem", true);

        // Act
        Map<String, Object> result = mcpController.toggleServer("filesystem", true);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("filesystem", result.get("server"));
        assertTrue((Boolean) result.get("enabled"));
        verify(mcpService, times(1)).setServerEnabled("filesystem", true);
    }

    @Test
    @DisplayName("测试切换 MCP 服务器状态 - 禁用")
    void testToggleServer_Disable() {
        // Arrange
        doNothing().when(mcpService).setServerEnabled("filesystem", false);

        // Act
        Map<String, Object> result = mcpController.toggleServer("filesystem", false);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("filesystem", result.get("server"));
        assertFalse((Boolean) result.get("enabled"));
        verify(mcpService, times(1)).setServerEnabled("filesystem", false);
    }

    @Test
    @DisplayName("测试移除 MCP 服务器")
    void testRemoveServer() {
        // Arrange
        doNothing().when(mcpService).removeServer("filesystem");

        // Act
        Map<String, Object> result = mcpController.removeServer("filesystem");

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("filesystem", result.get("server"));
        verify(mcpService, times(1)).removeServer("filesystem");
    }
}
