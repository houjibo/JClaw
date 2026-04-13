package com.jclaw.skills;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能引擎 - 管理和执行所有技能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillEngine {
    
    private final List<Skill> skills;
    private final Map<String, Skill> skillRegistry = new ConcurrentHashMap<>();
    
    /**
     * 初始化：注册所有技能
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        for (Skill skill : skills) {
            skillRegistry.put(skill.getName(), skill);
            log.info("注册技能：{} - {}", skill.getName(), skill.getDescription());
        }
        log.info("技能引擎初始化完成，共 {} 个技能", skillRegistry.size());
    }
    
    /**
     * 执行技能
     */
    public SkillResult execute(String skillName, Map<String, Object> params) {
        long start = System.currentTimeMillis();
        
        Skill skill = skillRegistry.get(skillName);
        if (skill == null) {
            return SkillResult.error("未知技能：" + skillName);
        }
        
        log.info("执行技能：{}, 参数：{}", skillName, params.keySet());
        
        SkillResult result = skill.execute(params);
        result.setDurationMs(System.currentTimeMillis() - start);
        
        log.info("技能执行完成：{} ({}ms, success={})", 
            skillName, result.getDurationMs(), result.isSuccess());
        
        return result;
    }
    
    /**
     * 获取技能列表
     */
    public List<Skill> listSkills() {
        return skills;
    }
    
    /**
     * 获取技能详情
     */
    public Skill getSkill(String name) {
        return skillRegistry.get(name);
    }
}
