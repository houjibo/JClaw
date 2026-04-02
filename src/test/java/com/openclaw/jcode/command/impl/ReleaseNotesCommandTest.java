package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ReleaseNotesCommand 单元测试
 */
@DisplayName("ReleaseNotesCommand 测试")
class ReleaseNotesCommandTest {
    
    private ReleaseNotesCommand releaseNotesCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        releaseNotesCommand = new ReleaseNotesCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("release-notes", releaseNotesCommand.getName());
        assertEquals("发布说明", releaseNotesCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, releaseNotesCommand.getCategory());
        assertTrue(releaseNotesCommand.getAliases().contains("releases"));
        assertTrue(releaseNotesCommand.getAliases().contains("changelog"));
    }
    
    @Test
    @DisplayName("显示最新发布")
    void testShowLatestRelease() {
        CommandResult result = releaseNotesCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("发布"));
    }
    
    @Test
    @DisplayName("发布历史")
    void testListReleases() {
        CommandResult result = releaseNotesCommand.execute("list", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("历史"));
    }
    
    @Test
    @DisplayName("查看版本")
    void testShowVersion() {
        CommandResult result = releaseNotesCommand.execute("version 1.0.0", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("版本"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = releaseNotesCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("release-notes"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
