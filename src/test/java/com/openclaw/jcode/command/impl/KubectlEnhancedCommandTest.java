package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * KubectlEnhancedCommand 单元测试
 */
@DisplayName("KubectlEnhancedCommand 测试")
class KubectlEnhancedCommandTest {
    
    private KubectlEnhancedCommand kubectlEnhancedCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        kubectlEnhancedCommand = new KubectlEnhancedCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("kubectl-enhanced", kubectlEnhancedCommand.getName());
        assertEquals("Kubectl 增强功能", kubectlEnhancedCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, kubectlEnhancedCommand.getCategory());
        assertTrue(kubectlEnhancedCommand.getAliases().contains("k8s-enhanced"));
    }
    
    @Test
    @DisplayName("显示 Kubectl 增强信息")
    void testShowKubectlEnhancedInfo() {
        CommandResult result = kubectlEnhancedCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Kubectl"));
    }
    
    @Test
    @DisplayName("Kubectl Contexts")
    void testKubectlContexts() {
        CommandResult result = kubectlEnhancedCommand.execute("contexts", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("上下文"));
    }
    
    @Test
    @DisplayName("Kubectl Namespaces")
    void testKubectlNamespaces() {
        CommandResult result = kubectlEnhancedCommand.execute("namespaces", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("命名空间"));
    }
    
    @Test
    @DisplayName("Kubectl Port Forward")
    void testKubectlPortForward() {
        CommandResult result = kubectlEnhancedCommand.execute("port-forward myapp", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("端口转发"));
    }
    
    @Test
    @DisplayName("Kubectl Debug")
    void testKubectlDebug() {
        CommandResult result = kubectlEnhancedCommand.execute("debug myapp", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("调试"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少参数")
    void testMissingArgs() {
        CommandResult result = kubectlEnhancedCommand.execute("port-forward", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = kubectlEnhancedCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("kubectl-enhanced"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
