package com.jclaw.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 技能市场服务测试
 */
@DisplayName("技能市场服务测试")
class SkillMarketServiceTest {
    
    private SkillMarketService skillMarket;
    
    @BeforeEach
    void setUp() {
        skillMarket = new SkillMarketService();
        
        // 注册测试技能
        SkillMarketService.SkillPackage skill1 = createTestSkill("skill-1", "Test Skill 1", "A test skill");
        skillMarket.registerSkill(skill1);
        
        SkillMarketService.SkillPackage skill2 = createTestSkill("skill-2", "Test Skill 2", "Another test skill");
        skillMarket.registerSkill(skill2);
    }
    
    private SkillMarketService.SkillPackage createTestSkill(String name, String displayName, String description) {
        SkillMarketService.SkillPackage skill = new SkillMarketService.SkillPackage();
        skill.setMetadata(new SkillMarketService.SkillMetadata());
        skill.getMetadata().setName(displayName);
        skill.getMetadata().setDescription(description);
        skill.getMetadata().setVersion("1.0.0");
        skill.getMetadata().setAuthor("Test Author");
        skill.getMetadata().setTags(List.of("test", "demo"));
        skill.getMetadata().setDownloads(0);
        skill.getMetadata().setRating(0.0);
        skill.setContent("test content");
        skill.setChecksum("test-checksum");
        return skill;
    }
    
    @Test
    @DisplayName("测试注册技能")
    void testRegisterSkill() {
        SkillMarketService.SkillPackage skill = createTestSkill("skill-3", "New Skill", "A new skill");
        SkillMarketService.SkillPackage registered = skillMarket.registerSkill(skill);
        
        assertNotNull(registered);
        assertNotNull(registered.getMetadata().getId());
        assertTrue(registered.getMetadata().getId().startsWith("skill-"));
    }
    
    @Test
    @DisplayName("测试搜索技能 - 按查询")
    void testSearchSkillsByQuery() {
        List<SkillMarketService.SkillMetadata> results = skillMarket.searchSkills("Test Skill 1", null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Skill 1", results.get(0).getName());
    }
    
    @Test
    @DisplayName("测试搜索技能 - 按标签")
    void testSearchSkillsByTags() {
        List<SkillMarketService.SkillMetadata> results = skillMarket.searchSkills(null, List.of("test"));
        
        assertNotNull(results);
        assertTrue(results.size() >= 2);
    }
    
    @Test
    @DisplayName("测试获取技能详情")
    void testGetSkillDetails() {
        List<SkillMarketService.SkillMetadata> allSkills = skillMarket.listAllSkills();
        String skillId = allSkills.get(0).getId();
        
        SkillMarketService.SkillMetadata details = skillMarket.getSkillDetails(skillId);
        
        assertNotNull(details);
        assertEquals(skillId, details.getId());
    }
    
    @Test
    @DisplayName("测试获取不存在的技能")
    void testGetNonExistentSkill() {
        SkillMarketService.SkillMetadata details = skillMarket.getSkillDetails("non-existent");
        
        assertNull(details);
    }
    
    @Test
    @DisplayName("测试安装技能")
    void testInstallSkill() {
        List<SkillMarketService.SkillMetadata> allSkills = skillMarket.listAllSkills();
        String skillId = allSkills.get(0).getId();
        
        boolean installed = skillMarket.installSkill(skillId);
        
        assertTrue(installed);
        
        List<SkillMarketService.SkillMetadata> installedSkills = skillMarket.listInstalledSkills();
        assertTrue(installedSkills.stream().anyMatch(s -> s.getId().equals(skillId)));
    }
    
    @Test
    @DisplayName("测试安装不存在的技能")
    void testInstallNonExistentSkill() {
        boolean installed = skillMarket.installSkill("non-existent");
        
        assertFalse(installed);
    }
    
    @Test
    @DisplayName("测试卸载技能")
    void testUninstallSkill() {
        List<SkillMarketService.SkillMetadata> allSkills = skillMarket.listAllSkills();
        String skillId = allSkills.get(0).getId();
        
        // 先安装
        skillMarket.installSkill(skillId);
        
        // 再卸载
        boolean uninstalled = skillMarket.uninstallSkill(skillId);
        
        assertTrue(uninstalled);
        
        List<SkillMarketService.SkillMetadata> installedSkills = skillMarket.listInstalledSkills();
        assertTrue(installedSkills.stream().noneMatch(s -> s.getId().equals(skillId)));
    }
    
    @Test
    @DisplayName("测试评分技能")
    void testRateSkill() {
        List<SkillMarketService.SkillMetadata> allSkills = skillMarket.listAllSkills();
        String skillId = allSkills.get(0).getId();
        
        // 先安装几次来增加下载数
        skillMarket.installSkill(skillId);
        skillMarket.installSkill(skillId);
        skillMarket.installSkill(skillId);
        
        // 评分
        skillMarket.rateSkill(skillId, 5.0);
        
        SkillMarketService.SkillMetadata details = skillMarket.getSkillDetails(skillId);
        assertNotNull(details);
        assertTrue(details.getRating() > 0);
    }
    
    @Test
    @DisplayName("测试列出所有技能")
    void testListAllSkills() {
        List<SkillMarketService.SkillMetadata> skills = skillMarket.listAllSkills();
        
        assertNotNull(skills);
        assertTrue(skills.size() >= 2);
    }
    
    @Test
    @DisplayName("测试获取统计信息")
    void testGetStats() {
        SkillMarketService.SkillMarketStats stats = skillMarket.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.getTotalSkills() >= 2);
        assertEquals(0, stats.getInstalledSkills());
    }
    
    @Test
    @DisplayName("测试更新技能")
    void testUpdateSkill() {
        List<SkillMarketService.SkillMetadata> allSkills = skillMarket.listAllSkills();
        String skillId = allSkills.get(0).getId();
        
        long beforeUpdate = skillMarket.getSkillDetails(skillId).getUpdatedAt();
        
        SkillMarketService.SkillPackage updated = skillMarket.updateSkill(skillId, "updated content");
        
        assertNotNull(updated);
        assertEquals("updated content", updated.getContent());
        assertTrue(updated.getMetadata().getUpdatedAt() >= beforeUpdate);
    }
    
    @Test
    @DisplayName("测试更新不存在的技能")
    void testUpdateNonExistentSkill() {
        assertThrows(IllegalArgumentException.class, () -> {
            skillMarket.updateSkill("non-existent", "content");
        });
    }
    
    @Test
    @DisplayName("测试删除技能")
    void testDeleteSkill() {
        List<SkillMarketService.SkillMetadata> allSkills = skillMarket.listAllSkills();
        String skillId = allSkills.get(0).getId();
        
        boolean deleted = skillMarket.deleteSkill(skillId);
        
        assertTrue(deleted);
        
        SkillMarketService.SkillMetadata details = skillMarket.getSkillDetails(skillId);
        assertNull(details);
    }
}
