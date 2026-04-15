package com.jclaw.skill.remote;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 技能注册中心 REST API
 * 
 * @author JClaw
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/skills/registry")
@RequiredArgsConstructor
public class SkillRegistryController {
    
    private final SkillRegistryService registryService;
    
    /**
     * 搜索技能
     */
    @GetMapping("/search")
    public List<SkillRegistryService.SkillInfo> searchSkills(
        @RequestParam String q,
        @RequestParam(required = false) List<String> tags
    ) {
        return registryService.searchSkills(q, tags);
    }
    
    /**
     * 获取技能详情
     */
    @GetMapping("/{skillId}")
    public SkillRegistryService.SkillInfo getSkillInfo(@PathVariable String skillId) {
        return registryService.getSkillInfo(skillId);
    }
    
    /**
     * 获取热门技能
     */
    @GetMapping("/popular")
    public List<SkillRegistryService.SkillInfo> getPopularSkills(
        @RequestParam(defaultValue = "10") int limit
    ) {
        return registryService.getPopularSkills(limit);
    }
    
    /**
     * 下载技能
     */
    @GetMapping("/{skillId}/download")
    public byte[] downloadSkill(
        @PathVariable String skillId,
        @RequestParam String version
    ) {
        return registryService.downloadSkill(skillId, version);
    }
    
    /**
     * 检查连接状态
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        boolean connected = registryService.isConnected();
        return Map.of(
            "status", connected ? "ok" : "error",
            "connected", connected
        );
    }
}
