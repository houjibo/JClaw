package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NpmCommand 单元测试
 */
@DisplayName("NpmCommand 测试")
class NpmCommandTest {
    
    private NpmCommand npmCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        npmCommand = new NpmCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("npm", npmCommand.getName());
        assertEquals("Node 包管理", npmCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, npmCommand.getCategory());
        assertTrue(npmCommand.getAliases().contains("node"));
        assertTrue(npmCommand.getAliases().contains("yarn"));
    }
    
    @Test
    @DisplayName("显示 NPM 信息")
    void testShowNpmInfo() {
        CommandResult result = npmCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("NPM"));
    }
    
    @Test
    @DisplayName("安装包")
    void testInstallPackage() {
        CommandResult result = npmCommand.execute("install express", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("安装"));
    }
    
    @Test
    @DisplayName("运行脚本")
    void testRunScript() {
        CommandResult result = npmCommand.execute("run build", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("运行"));
    }
    
    @Test
    @DisplayName("构建项目")
    void testBuildProject() {
        CommandResult result = npmCommand.execute("build", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("构建"));
    }
    
    @Test
    @DisplayName("错误处理 - 缺少包名")
    void testMissingPackage() {
        CommandResult result = npmCommand.execute("install", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = npmCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("npm"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
