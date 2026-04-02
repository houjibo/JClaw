package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ModelCommand 单元测试
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@DisplayName("ModelCommand 测试")
class ModelCommandTest {
    
    private ModelCommand modelCommand;
    private CommandContext context;
    
    @BeforeEach
    void setUp() {
        modelCommand = new ModelCommand();
        context = createMockContext();
    }
    
    @Test
    @DisplayName("命令基本信息")
    void testCommandInfo() {
        assertEquals("model", modelCommand.getName());
        assertEquals("模型切换和管理", modelCommand.getDescription());
        assertEquals(Command.CommandCategory.CONFIG, modelCommand.getCategory());
        assertTrue(modelCommand.getAliases().contains("models"));
        assertTrue(modelCommand.getAliases().contains("llm"));
    }
    
    @Test
    @DisplayName("列出模型")
    void testListModels() {
        CommandResult result = modelCommand.execute("", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertNotNull(result.getDisplayText());
        assertTrue(result.getDisplayText().contains("可用模型列表"));
    }
    
    @Test
    @DisplayName("列出模型包含当前模型")
    void testListModelsContainsCurrent() {
        CommandResult result = modelCommand.execute("", context);
        
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("currentModel"));
    }
    
    @Test
    @DisplayName("切换模型")
    void testSetModel() {
        CommandResult result = modelCommand.execute("qwen3.5-plus", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getMessage().contains("已切换"));
    }
    
    @Test
    @DisplayName("切换模型（使用 use）")
    void testSetModelWithUse() {
        CommandResult result = modelCommand.execute("use glm-4.7", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
    }
    
    @Test
    @DisplayName("切换不存在的模型")
    void testSetNonExistentModel() {
        CommandResult result = modelCommand.execute("nonexistent-model", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.ERROR, result.getType());
        assertTrue(result.getMessage().contains("未知模型"));
    }
    
    @Test
    @DisplayName("模型信息")
    void testModelInfo() {
        CommandResult result = modelCommand.execute("info qwen3.5-plus", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("模型信息"));
    }
    
    @Test
    @DisplayName("模型对比")
    void testCompareModels() {
        CommandResult result = modelCommand.execute("compare", context);
        
        assertNotNull(result);
        assertEquals(CommandResult.ResultType.SUCCESS, result.getType());
        assertTrue(result.getDisplayText().contains("模型对比"));
    }
    
    @Test
    @DisplayName("帮助信息")
    void testGetHelp() {
        String help = modelCommand.getHelp();
        
        assertNotNull(help);
        assertTrue(help.contains("model"));
        assertTrue(help.contains("用法"));
    }
    
    @Test
    @DisplayName("别名测试")
    void testAliases() {
        assertTrue(modelCommand.getAliases().contains("models"));
        assertTrue(modelCommand.getAliases().contains("llm"));
    }
    
    // 创建模拟上下文
    private CommandContext createMockContext() {
        com.openclaw.jcode.core.ToolContext toolContext = 
            com.openclaw.jcode.core.ToolContext.defaultContext();
        return new CommandContext(toolContext, "test-user", "test-session", true);
    }
}
