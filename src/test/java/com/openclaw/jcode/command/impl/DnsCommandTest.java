package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DnsCommand 单元测试
 */
@DisplayName("DnsCommand 测试")
class DnsCommandTest {
    
    private DnsCommand dnsCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        dnsCommand = new DnsCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("dns", dnsCommand.getName());
        assertEquals("DNS 查询", dnsCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, dnsCommand.getCategory());
        assertTrue(dnsCommand.getAliases().contains("nslookup"));
        assertTrue(dnsCommand.getAliases().contains("dig"));
    }
    
    @Test
    @DisplayName("显示帮助")
    void testShowHelp() {
        CommandResult result = dnsCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("帮助"));
    }
    
    @Test
    @DisplayName("DNS 查询")
    void testDnsLookup() {
        CommandResult result = dnsCommand.execute("google.com", context);
        
        assertNotNull(result);
        // DNS 查询可能成功或失败（取决于网络）
        assertTrue(result.getType() == CommandResult.ResultType.SUCCESS || 
                   result.getType() == CommandResult.ResultType.ERROR);
    }
    
    @Test
    @DisplayName("错误处理 - 缺少域名")
    void testMissingDomain() {
        CommandResult result = dnsCommand.execute("query", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = dnsCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("dns"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
