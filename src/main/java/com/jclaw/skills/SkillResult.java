package com.jclaw.skills;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 技能执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 结果内容
     */
    private String content;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 附加数据
     */
    private Map<String, Object> data;
    
    /**
     * 执行耗时（毫秒）
     */
    private long durationMs;
    
    public static SkillResult success(String content) {
        return SkillResult.builder()
            .success(true)
            .content(content)
            .build();
    }
    
    public static SkillResult error(String error) {
        return SkillResult.builder()
            .success(false)
            .error(error)
            .build();
    }
    
    public static SkillResult success(String content, Map<String, Object> data) {
        return SkillResult.builder()
            .success(true)
            .content(content)
            .data(data)
            .build();
    }
}
