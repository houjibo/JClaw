package com.jclaw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MCP 服务测试
 */
@DisplayName("MCP 服务测试")
class McpServiceTest {
    
    private McpService mcpService;
    
    @BeforeEach
    void setUp() {
        mcpService = new McpService();
    }
    
    @Test
    @DisplayName("测试注册 MCP 服务器")
    void testRegisterServer() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        List<Map<String, Object>> servers = mcpService.listServers();
        
        assertEquals(1, servers.size());
        assertEquals("test-server", servers.get(0).get("name"));
        assertEquals("http://localhost:3000/mcp", servers.get(0).get("url"));
    }
    
    @Test
    @DisplayName("测试列出服务器")
    void testListServers() {
        mcpService.registerServer("server1", "http://localhost:3000/mcp");
        mcpService.registerServer("server2", "http://localhost:3001/mcp");
        
        List<Map<String, Object>> servers = mcpService.listServers();
        
        assertEquals(2, servers.size());
    }
    
    @Test
    @DisplayName("测试列出工具")
    void testListTools() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        // 由于是测试，服务器不存在，应该返回空列表或抛出异常
        assertDoesNotThrow(() -> mcpService.listTools("test-server"));
    }
    
    @Test
    @DisplayName("测试列出不存在的服务器工具")
    void testListToolsForNonExistentServer() {
        assertThrows(RuntimeException.class, () -> {
            mcpService.listTools("non-exist");
        });
    }
    
    @Test
    @DisplayName("测试启用/禁用服务器")
    void testSetServerEnabled() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        // 禁用服务器
        mcpService.setServerEnabled("test-server", false);
        
        List<Map<String, Object>> servers = mcpService.listServers();
        assertEquals(false, servers.get(0).get("enabled"));
        
        // 重新启用
        mcpService.setServerEnabled("test-server", true);
        servers = mcpService.listServers();
        assertEquals(true, servers.get(0).get("enabled"));
    }
    
    @Test
    @DisplayName("测试移除服务器")
    void testRemoveServer() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        assertEquals(1, mcpService.listServers().size());
        
        mcpService.removeServer("test-server");
        
        assertEquals(0, mcpService.listServers().size());
    }
    
    @Test
    @DisplayName("测试调用不存在的服务器工具")
    void testCallToolOnNonExistentServer() {
        assertThrows(RuntimeException.class, () -> {
            mcpService.callTool("non-exist", "test-tool", Map.of());
        });
    }
    
    @Test
    @DisplayName("测试调用已禁用服务器的工具")
    void testCallToolOnDisabledServer() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        mcpService.setServerEnabled("test-server", false);
        
        assertThrows(RuntimeException.class, () -> {
            mcpService.callTool("test-server", "test-tool", Map.of());
        });
    }
    
    @Test
    @DisplayName("测试读取不存在的服务器资源")
    void testReadResourceFromNonExistentServer() {
        assertThrows(RuntimeException.class, () -> {
            mcpService.readResource("non-exist", "resource://test");
        });
    }
    
    @Test
    @DisplayName("测试获取不存在的服务器提示词")
    void testGetPromptFromNonExistentServer() {
        assertThrows(RuntimeException.class, () -> {
            mcpService.getPrompt("non-exist", "test-prompt", Map.of());
        });
    }
}
