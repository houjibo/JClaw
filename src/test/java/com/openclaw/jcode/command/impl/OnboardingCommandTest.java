package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * OnboardingCommand 单元测试
 */
@DisplayName("OnboardingCommand 测试")
class OnboardingCommandTest {
    
    private OnboardingCommand onboardingCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        onboardingCommand = new OnboardingCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("onboarding", onboardingCommand.getName());
        assertEquals("新手引导", onboardingCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, onboardingCommand.getCategory());
        assertTrue(onboardingCommand.getAliases().contains("guide"));
        assertTrue(onboardingCommand.getAliases().contains("tutorial"));
    }
    
    @Test
    @DisplayName("显示引导")
    void testShowOnboarding() {
        CommandResult result = onboardingCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("新手引导"));
    }
    
    @Test
    @DisplayName("开始引导")
    void testStartOnboarding() {
        CommandResult result = onboardingCommand.execute("start", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("引导"));
    }
    
    @Test
    @DisplayName("显示步骤")
    void testShowStep() {
        CommandResult result = onboardingCommand.execute("step 1", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("步骤"));
    }
    
    @Test
    @DisplayName("跳过引导")
    void testSkipOnboarding() {
        CommandResult result = onboardingCommand.execute("skip", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("跳过"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = onboardingCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("onboarding"));
        assertTrue(help.contains("用法"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
