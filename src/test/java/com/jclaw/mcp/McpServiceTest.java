package com.jclaw.mcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import com.jclaw.mcp.McpProtocol.Tool;
import com.jclaw.mcp.McpProtocol.Resource;
import com.jclaw.mcp.McpProtocol.Prompt;
import com.jclaw.mcp.McpProtocol.Response;

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
        
        List<Tool> tools = mcpService.listTools();
        assertNotNull(tools);
    }
    
    @Test
    @DisplayName("测试启用/禁用服务器")
    void testSetServerEnabled() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        mcpService.setServerEnabled("test-server", false);
        
        List<Map<String, Object>> servers = mcpService.listServers();
        assertEquals(false, servers.get(0).get("enabled"));
        
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
    @DisplayName("测试调用工具 - 工具未找到")
    void testCallToolNotFound() {
        Response response = mcpService.callTool("non-exist-tool", Map.of());
        assertNotNull(response.getError());
        assertEquals(-32601, response.getError().getCode());
        assertTrue(response.getError().getMessage().contains("工具未找到"));
    }
    
    @Test
    @DisplayName("测试读取资源 - 资源未找到")
    void testReadResourceNotFound() {
        String content = mcpService.readResource("resource://non-exist");
        assertEquals("", content);
    }
    
    @Test
    @DisplayName("测试列出资源")
    void testListResources() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        List<Resource> resources = mcpService.listResources();
        assertNotNull(resources);
    }
    
    @Test
    @DisplayName("测试列出提示词")
    void testListPrompts() {
        mcpService.registerServer("test-server", "http://localhost:3000/mcp");
        
        List<Prompt> prompts = mcpService.listPrompts();
        assertNotNull(prompts);
    }
}
