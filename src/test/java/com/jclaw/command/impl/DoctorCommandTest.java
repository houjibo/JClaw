package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DoctorCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("DoctorCommand 测试")
class DoctorCommandTest {
    
    private DiagnosticCommand doctorCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        doctorCommand = new DiagnosticCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("doctor", doctorCommand.getName());
        assertEquals("系统诊断和健康检查", doctorCommand.getDescription());
        assertEquals(Command.CommandCategory.DIAGNOSTIC, doctorCommand.getCategory());
        assertTrue(doctorCommand.getAliases().contains("diagnose"));
        assertTrue(doctorCommand.getAliases().contains("health"));
        assertTrue(doctorCommand.getAliases().contains("check"));
    }
    
    @Test
    @DisplayName("完整诊断")
    void testExecuteAll() {
        CommandResult result = doctorCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("诊断报告"));
    }
    
    @Test
    @DisplayName("系统检查")
    void testSystemCheck() {
        CommandResult result = doctorCommand.execute("system", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("checks"));
    }
    
    @Test
    @DisplayName("工具检查")
    void testToolsCheck() {
        CommandResult result = doctorCommand.execute("tools", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("MCP 检查")
    void testMcpCheck() {
        CommandResult result = doctorCommand.execute("mcp", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("网络检查")
    void testNetworkCheck() {
        CommandResult result = doctorCommand.execute("network", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("诊断结果包含总体状态")
    void testOverallStatus() {
        CommandResult result = doctorCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("overall"));
    }
    
    @Test
    @DisplayName("诊断结果包含时间戳")
    void testTimestamp() {
        CommandResult result = doctorCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("timestamp"));
    }
    
    @Test
    @DisplayName("诊断报告包含检查详情")
    void testReportContainsChecks() {
        CommandResult result = doctorCommand.execute("", context);
        
        String display = result.getDisplayText();
        assertNotNull(display);
        assertTrue(display.contains("检查详情"));
        // 检查至少包含一个检查项
        assertTrue(display.contains("✅") || display.contains("⚠️"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = doctorCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("doctor"));
        assertTrue(help.contains("用法"));
        assertTrue(help.contains("check"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(doctorCommand.getAliases().contains("diagnose"));
        assertTrue(doctorCommand.getAliases().contains("health"));
        assertTrue(doctorCommand.getAliases().contains("check"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
