package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UpgradeCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("UpgradeCommand 测试")
class UpgradeCommandTest {
    
    private UpgradeCommand upgradeCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        upgradeCommand = new UpgradeCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("upgrade", upgradeCommand.getName());
        assertEquals("升级检查和应用", upgradeCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, upgradeCommand.getCategory());
        assertTrue(upgradeCommand.getAliases().contains("update"));
        assertTrue(upgradeCommand.getAliases().contains("up"));
    }
    
    @Test
    @DisplayName("检查升级")
    void testCheckUpgrade() {
        CommandResult result = upgradeCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("升级检查"));
    }
    
    @Test
    @DisplayName("检查升级包含版本信息")
    void testCheckUpgradeContainsVersion() {
        CommandResult result = upgradeCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("currentVersion"));
        assertTrue(result.getData().containsKey("latestVersion"));
    }
    
    @Test
    @DisplayName("显示升级信息")
    void testShowUpgradeInfo() {
        CommandResult result = upgradeCommand.execute("info", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("升级信息"));
    }
    
    @Test
    @DisplayName("显示升级历史")
    void testShowUpgradeHistory() {
        CommandResult result = upgradeCommand.execute("history", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("升级历史"));
    }
    
    @Test
    @DisplayName("需要确认")
    void testRequiresConfirmation() {
        assertTrue(upgradeCommand.isRequiresConfirmation());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = upgradeCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("upgrade"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(upgradeCommand.getAliases().contains("update"));
        assertTrue(upgradeCommand.getAliases().contains("up"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
