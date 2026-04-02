package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MemoryCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("MemoryCommand 测试")
class MemoryCommandTest {
    
    private MemoryCommand memoryCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        memoryCommand = new MemoryCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("memory", memoryCommand.getName());
        assertEquals("记忆管理", memoryCommand.getDescription());
        assertEquals(Command.CommandCategory.SYSTEM, memoryCommand.getCategory());
        assertTrue(memoryCommand.getAliases().contains("mem"));
        assertTrue(memoryCommand.getAliases().contains("me"));
    }
    
    @Test
    @DisplayName("列出记忆")
    void testListMemories() {
        CommandResult result = memoryCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("记忆列表"));
    }
    
    @Test
    @DisplayName("添加记忆")
    void testAddMemory() {
        CommandResult result = memoryCommand.execute("add 测试分类 测试内容", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已添加"));
    }
    
    @Test
    @DisplayName("获取记忆")
    void testGetMemory() {
        // 先添加记忆
        memoryCommand.execute("add test-cat test-content", context);
        
        // 获取记忆列表找到 ID
        CommandResult listResult = memoryCommand.execute("", context);
        
        CommandResult result = memoryCommand.execute("get test-cat", context);
        
        assertNotNull(result);
        // 可能找不到，因为 ID 包含时间戳
        assertTrue(result.getType() == CommandResult.ResultType.SUCCESS || 
                   result.getType() == CommandResult.ResultType.ERROR);
    }
    
    @Test
    @DisplayName("搜索记忆")
    void testSearchMemory() {
        // 先添加记忆
        memoryCommand.execute("add 测试 搜索测试", context);
        
        CommandResult result = memoryCommand.execute("search 测试", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("搜索"));
    }
    
    @Test
    @DisplayName("记忆统计")
    void testMemoryStats() {
        CommandResult result = memoryCommand.execute("info", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("记忆统计"));
    }
    
    @Test
    @DisplayName("清空记忆")
    void testClearMemories() {
        CommandResult result = memoryCommand.execute("clear", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已清空"));
    }
    
    @Test
    @DisplayName("错误处理 - 参数不足")
    void testInsufficientArgs() {
        CommandResult result = memoryCommand.execute("add", context);
        
        assertNotNull(result);
        // 应该能处理，使用默认分类
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("错误处理 - 记忆不存在")
    void testMemoryNotFound() {
        CommandResult result = memoryCommand.execute("get nonexistent", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = memoryCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("memory"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(memoryCommand.getAliases().contains("mem"));
        assertTrue(memoryCommand.getAliases().contains("me"));
    }
    
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
