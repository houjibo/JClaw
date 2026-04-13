package com.jclaw.controller;

import com.jclaw.skills.SkillEngine;
import com.jclaw.skills.SkillResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 技能 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    
    private final SkillEngine skillEngine;
    
    /**
     * 获取技能列表
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> listSkills() {
        List<Map<String, String>> skills = skillEngine.listSkills().stream()
            .map(skill -> Map.of(
                "name", skill.getName(),
                "description", skill.getDescription()
            ))
            .toList();
        return ResponseEntity.ok(skills);
    }
    
    /**
     * 执行技能
     */
    @PostMapping("/execute")
    public ResponseEntity<SkillResult> executeSkill(@RequestBody SkillRequest request) {
        log.info("执行技能：{} 参数：{}", request.getSkill(), request.getParams());
        
        if (request.getSkill() == null || request.getSkill().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(SkillResult.error("缺少参数：skill"));
        }
        
        SkillResult result = skillEngine.execute(request.getSkill(), request.getParams());
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 快速执行（简化参数）
     */
    @PostMapping("/run/{skillName}")
    public ResponseEntity<SkillResult> runSkill(
            @PathVariable String skillName,
            @RequestBody(required = false) Map<String, Object> params) {
        return executeSkill(new SkillRequest(skillName, params != null ? params : Map.of()));
    }
    
    /**
     * 请求体
     */
    public static class SkillRequest {
        private String skill;
        private Map<String, Object> params;
        
        public SkillRequest() {}
        
        public SkillRequest(String skill, Map<String, Object> params) {
            this.skill = skill;
            this.params = params;
        }
        
        public String getSkill() { return skill; }
        public void setSkill(String skill) { this.skill = skill; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
}
