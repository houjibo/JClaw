package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.ToolContext;
import com.openclaw.jcode.core.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SkillTool 测试
 */
@DisplayName("技能工具测试")
class SkillToolTest {
    
    private SkillTool skillTool;
    private ToolContext context;
    
    @BeforeEach
    void setUp() {
        skillTool = new SkillTool();
        context = ToolContext.defaultContext();
    }
    
    @Test
    @DisplayName("测试列出技能")
    void testListSkills() {
        ToolResult result = skillTool.execute(Map.of("action", "list"), context);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("可用技能"));
        assertTrue(result.getOutput().contains("java-expert"));
    }
    
    @Test
    @DisplayName("测试激活技能")
    void testActivateSkill() {
        // 激活技能
        ToolResult result = skillTool.execute(Map.of("action", "activate", "name", "java-expert"), context);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("已激活技能"));
        
        // 验证技能信息
        ToolResult infoResult = skillTool.execute(Map.of("action", "info", "name", "java-expert"), context);
        assertTrue(infoResult.isSuccess());
    }
    
    @Test
    @DisplayName("测试关闭技能")
    void testDeactivateSkill() {
        // 先激活
        skillTool.execute(Map.of("action", "activate", "name", "java-expert"), context);
        
        // 关闭技能
        ToolResult result = skillTool.execute(Map.of("action", "deactivate", "name", "java-expert"), context);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("已关闭技能"));
    }
    
    @Test
    @DisplayName("测试激活不存在的技能")
    void testActivateNonExistentSkill() {
        ToolResult result = skillTool.execute(Map.of("action", "activate", "name", "non-exist"), context);
        
        assertFalse(result.isSuccess());
        String errorMsg = result.getError() != null ? result.getError() : result.getMessage();
        assertTrue(errorMsg != null && errorMsg.contains("技能不存在"));
    }
    
    @Test
    @DisplayName("测试关闭不存在的技能")
    void testDeactivateNonExistentSkill() {
        ToolResult result = skillTool.execute(Map.of("action", "deactivate", "name", "non-exist"), context);
        
        assertFalse(result.isSuccess());
        String errorMsg = result.getError() != null ? result.getError() : result.getMessage();
        assertTrue(errorMsg != null && errorMsg.contains("技能不存在"));
    }
    
    @Test
    @DisplayName("测试获取技能信息")
    void testGetSkillInfo() {
        ToolResult result = skillTool.execute(Map.of("action", "info", "name", "java-expert"), context);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }
    
    @Test
    @DisplayName("测试获取不存在的技能信息")
    void testGetNonExistentSkillInfo() {
        ToolResult result = skillTool.execute(Map.of("action", "info", "name", "non-exist"), context);
        
        assertFalse(result.isSuccess());
        String errorMsg = result.getError() != null ? result.getError() : result.getMessage();
        assertTrue(errorMsg != null && errorMsg.contains("技能不存在"));
    }
    
    @Test
    @DisplayName("测试配置技能")
    void testConfigureSkill() {
        Map<String, Object> config = Map.of("key1", "value1", "key2", 123);
        ToolResult result = skillTool.execute(
            Map.of("action", "config", "name", "java-expert", "config", config),
            context
        );
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    @DisplayName("测试安装技能")
    void testInstallSkill() {
        ToolResult result = skillTool.execute(
            Map.of("action", "install", "name", "test-skill", "url", "http://example.com/skill.zip"),
            context
        );
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    @DisplayName("测试卸载技能")
    void testUninstallSkill() {
        // 先安装
        skillTool.execute(
            Map.of("action", "install", "name", "temp-skill", "url", "http://example.com/skill.zip"),
            context
        );
        
        // 卸载
        ToolResult result = skillTool.execute(
            Map.of("action", "uninstall", "name", "temp-skill"),
            context
        );
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    @DisplayName("测试安装技能缺少参数")
    void testInstallSkillMissingParams() {
        ToolResult result = skillTool.execute(
            Map.of("action", "install", "name", "test-skill"),
            context
        );
        
        assertFalse(result.isSuccess());
        String errorMsg = result.getError() != null ? result.getError() : result.getMessage();
        assertTrue(errorMsg != null && errorMsg.contains("url 参数不能为空"));
    }
    
    @Test
    @DisplayName("测试未知操作")
    void testUnknownAction() {
        ToolResult result = skillTool.execute(
            Map.of("action", "unknown-action"),
            context
        );
        
        assertFalse(result.isSuccess());
        String errorMsg = result.getError() != null ? result.getError() : result.getMessage();
        assertTrue(errorMsg != null && errorMsg.contains("未知操作"));
    }
    
    @Test
    @DisplayName("测试默认操作为 list")
    void testDefaultAction() {
        ToolResult result = skillTool.execute(Map.of(), context);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("可用技能"));
    }
}
