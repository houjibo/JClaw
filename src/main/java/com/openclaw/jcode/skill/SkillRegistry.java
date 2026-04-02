package com.openclaw.jcode.skill;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能注册表
 */
@Component
public class SkillRegistry {
    
    private final Map<String, Skill> skills = new ConcurrentHashMap<>();
    private final List<String> activeSkills = new ArrayList<>();
    
    public SkillRegistry() {
        // 注册内置技能
        registerBundledSkills();
    }
    
    private void registerBundledSkills() {
        // 代码审查技能
        register(new Skill("code-review", "代码审查", "专业的代码审查技能", "CODE"));
        
        // 单元测试技能
        register(new Skill("unit-test", "单元测试", "生成单元测试代码", "TEST"));
        
        // 文档生成技能
        register(new Skill("doc-generator", "文档生成", "自动生成代码文档", "DOC"));
        
        // 重构技能
        register(new Skill("refactor", "代码重构", "优化和重构代码", "CODE"));
        
        // 调试技能
        register(new Skill("debug", "调试助手", "帮助调试和定位问题", "DEBUG"));
        
        // 翻译技能
        register(new Skill("translator", "翻译", "多语言翻译", "LANG"));
        
        // 解释技能
        register(new Skill("explainer", "代码解释", "解释代码功能和逻辑", "EDU"));
        
        // 安全扫描技能
        register(new Skill("security-scan", "安全扫描", "检测代码安全漏洞", "SECURITY"));
    }
    
    public void register(Skill skill) {
        skills.put(skill.getId(), skill);
        System.out.println("[SkillRegistry] 注册技能：" + skill.getId() + " - " + skill.getName());
    }
    
    public Optional<Skill> getSkill(String skillId) {
        return Optional.ofNullable(skills.get(skillId));
    }
    
    public List<Skill> listSkills() {
        return new ArrayList<>(skills.values());
    }
    
    public List<Skill> listSkillsByCategory(String category) {
        return skills.values().stream()
                .filter(s -> category.equalsIgnoreCase(s.getCategory()))
                .toList();
    }
    
    public void activateSkill(String skillId) {
        if (skills.containsKey(skillId) && !activeSkills.contains(skillId)) {
            activeSkills.add(skillId);
            System.out.println("[SkillRegistry] 激活技能：" + skillId);
        }
    }
    
    public void deactivateSkill(String skillId) {
        activeSkills.remove(skillId);
        System.out.println("[SkillRegistry] 关闭技能：" + skillId);
    }
    
    public List<String> getActiveSkills() {
        return new ArrayList<>(activeSkills);
    }
    
    public boolean isSkillActive(String skillId) {
        return activeSkills.contains(skillId);
    }
    
    public int size() {
        return skills.size();
    }
}
