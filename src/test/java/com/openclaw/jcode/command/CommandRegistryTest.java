package com.openclaw.jcode.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * CommandRegistry 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("CommandRegistry 测试")
class CommandRegistryTest {
    
    private CommandRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = new CommandRegistry();
    }
    
    @Test
    @DisplayName("注册命令")
    void testRegister() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        Command result = registry.getCommand("test");
        assertNotNull(result);
        assertEquals("test", result.getName());
    }
    
    @Test
    @DisplayName("注册命令（带别名）")
    void testRegisterWithAliases() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        // 通过别名访问
        Command result = registry.getCommand("t");
        assertNotNull(result);
        assertEquals("test", result.getName());
    }
    
    @Test
    @DisplayName("批量注册命令")
    void testRegisterAll() {
        TestCommand cmd1 = new TestCommand();
        TestCommand2 cmd2 = new TestCommand2();
        
        registry.registerAll(List.of(cmd1, cmd2));
        
        assertNotNull(registry.getCommand("test"));
        assertNotNull(registry.getCommand("test2"));
    }
    
    @Test
    @DisplayName("注销命令")
    void testUnregister() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        registry.unregister("test");
        
        assertNull(registry.getCommand("test"));
    }
    
    @Test
    @DisplayName("列出所有命令")
    void testListCommands() {
        TestCommand cmd1 = new TestCommand();
        TestCommand2 cmd2 = new TestCommand2();
        
        registry.register(cmd1);
        registry.register(cmd2);
        
        List<Command> commands = registry.listCommands();
        
        assertEquals(2, commands.size());
    }
    
    @Test
    @DisplayName("按类别列出命令")
    void testListCommandsByCategory() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        List<Command> gitCommands = registry.listCommandsByCategory(Command.CommandCategory.GIT);
        
        assertEquals(1, gitCommands.size());
    }
    
    @Test
    @DisplayName("启用/禁用命令")
    void testSetCommandEnabled() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        // 禁用
        registry.setCommandEnabled("test", false);
        assertNull(registry.getCommand("test"));
        
        // 启用
        registry.setCommandEnabled("test", true);
        assertNotNull(registry.getCommand("test"));
    }
    
    @Test
    @DisplayName("检查命令是否可用")
    void testIsCommandEnabled() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        assertTrue(registry.isCommandEnabled("test"));
        
        registry.setCommandEnabled("test", false);
        assertFalse(registry.isCommandEnabled("test"));
    }
    
    @Test
    @DisplayName("获取命令名称（用于自动完成）")
    void testGetCommandNames() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        java.util.Set<String> names = registry.getCommandNames();
        
        assertTrue(names.contains("test"));
        assertTrue(names.contains("t")); // 别名
    }
    
    @Test
    @DisplayName("获取统计信息")
    void testGetStats() {
        TestCommand cmd = new TestCommand();
        registry.register(cmd);
        
        java.util.Map<String, Object> stats = registry.getStats();
        
        assertTrue((Integer) stats.get("total") > 0);
        assertTrue((Integer) stats.get("enabled") > 0);
        assertNotNull(stats.get("byCategory"));
    }
    
    @Test
    @DisplayName("清除缓存")
    void testClearCache() {
        // 不抛出异常即可
        assertDoesNotThrow(() -> registry.clearCache());
    }
    
    // 测试用命令类
    static class TestCommand extends Command {
        public TestCommand() {
            this.name = "test";
            this.description = "测试命令";
            this.aliases = java.util.Arrays.asList("t");
            this.category = CommandCategory.GIT;
        }
        
        @Override
        public CommandResult execute(String args, CommandContext context) {
            return CommandResult.success("测试执行");
        }
    }
    
    static class TestCommand2 extends Command {
        public TestCommand2() {
            this.name = "test2";
            this.description = "测试命令 2";
            this.category = CommandCategory.CONFIG;
        }
        
        @Override
        public CommandResult execute(String args, CommandContext context) {
            return CommandResult.success("测试执行 2");
        }
    }
}
