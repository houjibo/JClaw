package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TagCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("TagCommand 测试")
class TagCommandTest {
    
    private TagCommand tagCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        tagCommand = new TagCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("tag", tagCommand.getName());
        assertEquals("Git 标签管理", tagCommand.getDescription());
        assertEquals(Command.CommandCategory.GIT, tagCommand.getCategory());
        assertTrue(tagCommand.getAliases().contains("tags"));
        assertTrue(tagCommand.getAliases().contains("t"));
    }
    
    @Test
    @DisplayName("列出标签")
    void testListTags() {
        CommandResult result = tagCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("Git 标签列表"));
    }
    
    @Test
    @DisplayName("列出所有标签")
    void testListAllTags() {
        CommandResult result = tagCommand.execute("-a", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("所有标签"));
    }
    
    @Test
    @DisplayName("创建标签")
    void testCreateTag() {
        CommandResult result = tagCommand.execute("-c v1.2.0 Release", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已创建"));
    }
    
    @Test
    @DisplayName("删除标签")
    void testDeleteTag() {
        CommandResult result = tagCommand.execute("-d v1.0.0", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已删除"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = tagCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("tag"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(tagCommand.getAliases().contains("tags"));
        assertTrue(tagCommand.getAliases().contains("t"));
    }
    
    private CommandContext createMockContext() {
        com.jclaw.core.ToolContext toolContext = 
            com.jclaw.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
