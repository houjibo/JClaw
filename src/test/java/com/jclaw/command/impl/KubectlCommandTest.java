package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * KubectlCommand 单元测试
 */
@DisplayName("KubectlCommand 测试")
class KubectlCommandTest {
    
    private KubectlCommand kubectlCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        kubectlCommand = new KubectlCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("kubectl", kubectlCommand.getName());
        assertEquals("K8s 集群管理", kubectlCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, kubectlCommand.getCategory());
        assertTrue(kubectlCommand.getAliases().contains("k8s"));
        assertTrue(kubectlCommand.getAliases().contains("kube"));
    }
    
    @Test
    @DisplayName("显示 Kubectl 信息")
    void testShowKubectlInfo() {
        CommandResult result = kubectlCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("Kubectl"));
    }
    
    @Test
    @DisplayName("Get 命令")
    void testKubectlGet() {
        CommandResult result = kubectlCommand.execute("get pods", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Get"));
    }
    
    @Test
    @DisplayName("Describe 命令")
    void testKubectlDescribe() {
        CommandResult result = kubectlCommand.execute("describe my-pod", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Describe"));
    }
    
    @Test
    @DisplayName("Logs 命令")
    void testKubectlLogs() {
        CommandResult result = kubectlCommand.execute("logs my-pod", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("Logs"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少参数")
    void testMissingArgs() {
        CommandResult result = kubectlCommand.execute("describe", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = kubectlCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("kubectl"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
