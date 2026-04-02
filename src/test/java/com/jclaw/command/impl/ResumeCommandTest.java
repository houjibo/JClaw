package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResumeCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ResumeCommand 测试")
class ResumeCommandTest {
    
    private ResumeCommand resumeCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        resumeCommand = new ResumeCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("resume", resumeCommand.getName());
        assertEquals("恢复之前的会话", resumeCommand.getDescription());
        assertEquals(Command.CommandCategory.SESSION, resumeCommand.getCategory());
        assertTrue(resumeCommand.getAliases().contains("continue"));
        assertTrue(resumeCommand.getAliases().contains("restore"));
    }
    
    @Test
    @DisplayName("列出可恢复的会话")
    void testListSessions() {
        CommandResult result = resumeCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("可恢复的会话"));
    }
    
    @Test
    @DisplayName("恢复会话")
    void testResumeSession() {
        CommandResult result = resumeCommand.execute("session-001", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已恢复"));
    }
    
    @Test
    @DisplayName("恢复最新会话")
    void testResumeLatest() {
        CommandResult result = resumeCommand.execute("--latest", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("恢复会话包含数据")
    void testResumeSessionContainsData() {
        CommandResult result = resumeCommand.execute("session-001", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("resumed"));
        assertTrue(result.getData().containsKey("sessionId"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = resumeCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("resume"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(resumeCommand.getAliases().contains("continue"));
        assertTrue(resumeCommand.getAliases().contains("restore"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
