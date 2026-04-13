package com.jclaw.skills;

import java.util.Map;

/**
 * 技能接口 - 所有技能的统一抽象
 * 
 * @author JClaw
 * @since 2026-04-13
 */
public interface Skill {
    
    /**
     * 技能名称（唯一标识）
     */
    String getName();
    
    /**
     * 技能描述
     */
    String getDescription();
    
    /**
     * 执行技能
     * 
     * @param params 参数
     * @return 执行结果
     */
    SkillResult execute(Map<String, Object> params);
}
