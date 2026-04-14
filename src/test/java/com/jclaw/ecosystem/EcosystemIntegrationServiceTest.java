package com.jclaw.ecosystem;

import com.jclaw.ecosystem.EcosystemIntegrationService.ExternalSystemConfig;
import com.jclaw.ecosystem.EcosystemIntegrationService.EcosystemMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 生态集成服务测试
 */
@DisplayName("生态集成服务测试")
class EcosystemIntegrationServiceTest {
    
    private EcosystemIntegrationService ecosystemService;
    
    @BeforeEach
    void setUp() {
        ecosystemService = new EcosystemIntegrationService();
    }
    
    @Test
    @DisplayName("测试连接 OpenClaw")
    void testConnectToOpenClaw() {
        boolean success = ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        assertTrue(success);
        
        Collection<ExternalSystemConfig> systems = ecosystemService.listIntegratedSystems();
        assertEquals(1, systems.size());
        
        ExternalSystemConfig config = systems.iterator().next();
        assertEquals("openclaw-main", config.getSystemId());
        assertEquals("openclaw", config.getSystemType());
        assertTrue(config.getCapabilities().containsKey("subagent_spawn"));
    }
    
    @Test
    @DisplayName("测试连接 Claude Code")
    void testConnectToClaudeCode() {
        boolean success = ecosystemService.connectToClaudeCode("http://localhost:8080", "test-api-key");
        
        assertTrue(success);
        
        Collection<ExternalSystemConfig> systems = ecosystemService.listIntegratedSystems();
        assertEquals(1, systems.size());
        
        ExternalSystemConfig config = systems.iterator().next();
        assertEquals("claude-code-main", config.getSystemId());
        assertEquals("claude-code", config.getSystemType());
    }
    
    @Test
    @DisplayName("测试调用 OpenClaw 能力 - Subagent 孵化")
    void testCallOpenClawSubagentSpawn() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        Object result = ecosystemService.callExternalCapability("openclaw-main", "subagent_spawn", Map.of(
            "task", "test task",
            "role", "developer"
        ));
        
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertTrue(((Map<?, ?>) result).containsKey("success"));
    }
    
    @Test
    @DisplayName("测试调用 OpenClaw 能力 - Session 管理")
    void testCallOpenClawSessionManagement() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        Object result = ecosystemService.callExternalCapability("openclaw-main", "session_management", Map.of(
            "action", "list"
        ));
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("测试调用 OpenClaw 能力 - 技能市场")
    void testCallOpenClawSkillMarket() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        Object result = ecosystemService.callExternalCapability("openclaw-main", "skill_market", Map.of(
            "action", "list"
        ));
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("测试调用 Claude Code 能力 - 工具执行")
    void testCallClaudeCodeToolExecution() {
        ecosystemService.connectToClaudeCode("http://localhost:8080", "test-api-key");
        
        Object result = ecosystemService.callExternalCapability("claude-code-main", "tool_execution", Map.of(
            "tool", "file_read",
            "path", "/test/file.txt"
        ));
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("测试调用 Claude Code 能力 - 代码分析")
    void testCallClaudeCodeCodeAnalysis() {
        ecosystemService.connectToClaudeCode("http://localhost:8080", "test-api-key");
        
        Object result = ecosystemService.callExternalCapability("claude-code-main", "code_analysis", Map.of(
            "file", "/test/code.java"
        ));
        
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("测试发送生态消息")
    void testSendMessage() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        EcosystemMessage message = new EcosystemMessage();
        message.setFromSystem("jclaw");
        message.setToSystem("openclaw-main");
        message.setMessageType("capability_call");
        message.setPayload(Map.of("capability", "subagent_spawn"));
        
        boolean success = ecosystemService.sendMessage(message);
        
        assertTrue(success);
        assertEquals(1, ecosystemService.getMessageQueueSize());
    }
    
    @Test
    @DisplayName("测试测试连接")
    void testTestConnection() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        boolean success = ecosystemService.testConnection("openclaw-main");
        
        assertTrue(success);
        
        boolean nonExistent = ecosystemService.testConnection("non-existent");
        assertFalse(nonExistent);
    }
    
    @Test
    @DisplayName("测试断开连接")
    void testDisconnectSystem() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        Collection<ExternalSystemConfig> before = ecosystemService.listIntegratedSystems();
        assertEquals(1, before.size());
        
        ecosystemService.disconnectSystem("openclaw-main");
        
        Collection<ExternalSystemConfig> after = ecosystemService.listIntegratedSystems();
        assertEquals(0, after.size());
    }
    
    @Test
    @DisplayName("测试调用不支持的能力")
    void testCallUnsupportedCapability() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        assertThrows(IllegalArgumentException.class, () -> {
            ecosystemService.callExternalCapability("openclaw-main", "unsupported_capability", Map.of());
        });
    }
    
    @Test
    @DisplayName("测试调用不存在系统的能力")
    void testCallNonExistentSystemCapability() {
        assertThrows(IllegalStateException.class, () -> {
            ecosystemService.callExternalCapability("non-existent", "some_capability", Map.of());
        });
    }
    
    @Test
    @DisplayName("测试接收消息")
    void testReceiveMessage() {
        ecosystemService.connectToOpenClaw("http://localhost:18789", "test-api-key");
        
        EcosystemMessage message = new EcosystemMessage();
        message.setFromSystem("jclaw");
        message.setToSystem("openclaw-main");
        message.setMessageType("test");
        
        ecosystemService.sendMessage(message);
        
        EcosystemMessage received = ecosystemService.receiveMessage("openclaw-main");
        assertNotNull(received);
        assertEquals("test", received.getMessageType());
    }
}
