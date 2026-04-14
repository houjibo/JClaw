package com.jclaw.skill;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能市场服务（简化版）
 * 
 * 功能：
 * - 技能注册
 * - 技能搜索
 * - 技能安装
 * - 技能管理
 * 
 * 状态：简化版实现
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Service
public class SkillMarketService {
    
    /**
     * 技能元数据
     */
    @Data
    public static class SkillMetadata {
        private String id;
        private String name;
        private String description;
        private String version;
        private String author;
        private List<String> tags;
        private long downloads;
        private double rating;
        private long createdAt;
        private long updatedAt;
    }
    
    /**
     * 技能包
     */
    @Data
    public static class SkillPackage {
        private SkillMetadata metadata;
        private String content;
        private String checksum;
    }
    
    /**
     * 已注册技能
     */
    private final Map<String, SkillPackage> skills = new ConcurrentHashMap<>();
    
    /**
     * 已安装技能
     */
    private final Set<String> installedSkills = ConcurrentHashMap.newKeySet();
    
    /**
     * 注册技能
     */
    public SkillPackage registerSkill(SkillPackage skillPackage) {
        log.info("注册技能：{} v{}", skillPackage.getMetadata().getName(), skillPackage.getMetadata().getVersion());
        
        skillPackage.getMetadata().setId(generateSkillId(skillPackage.getMetadata().getName()));
        skillPackage.getMetadata().setCreatedAt(System.currentTimeMillis());
        skillPackage.getMetadata().setUpdatedAt(System.currentTimeMillis());
        
        skills.put(skillPackage.getMetadata().getId(), skillPackage);
        
        log.info("技能注册成功：{}", skillPackage.getMetadata().getId());
        return skillPackage;
    }
    
    /**
     * 搜索技能
     */
    public List<SkillMetadata> searchSkills(String query, List<String> tags) {
        log.info("搜索技能：query={}, tags={}", query, tags);
        
        return skills.values().stream()
            .map(SkillPackage::getMetadata)
            .filter(metadata -> {
                boolean matchesQuery = query == null || query.isEmpty() ||
                    metadata.getName().toLowerCase().contains(query.toLowerCase()) ||
                    metadata.getDescription().toLowerCase().contains(query.toLowerCase());
                
                boolean matchesTags = tags == null || tags.isEmpty() ||
                    metadata.getTags() != null && metadata.getTags().stream()
                        .anyMatch(tag -> tags.contains(tag));
                
                return matchesQuery && matchesTags;
            })
            .toList();
    }
    
    /**
     * 获取技能详情
     */
    public SkillMetadata getSkillDetails(String skillId) {
        SkillPackage skillPackage = skills.get(skillId);
        return skillPackage != null ? skillPackage.getMetadata() : null;
    }
    
    /**
     * 安装技能
     */
    public boolean installSkill(String skillId) {
        log.info("安装技能：{}", skillId);
        
        SkillPackage skillPackage = skills.get(skillId);
        if (skillPackage == null) {
            log.warn("技能不存在：{}", skillId);
            return false;
        }
        
        installedSkills.add(skillId);
        skillPackage.getMetadata().setDownloads(skillPackage.getMetadata().getDownloads() + 1);
        
        log.info("技能安装成功：{}", skillId);
        return true;
    }
    
    /**
     * 卸载技能
     */
    public boolean uninstallSkill(String skillId) {
        log.info("卸载技能：{}", skillId);
        
        boolean removed = installedSkills.remove(skillId);
        
        if (removed) {
            log.info("技能卸载成功：{}", skillId);
        } else {
            log.warn("技能未安装：{}", skillId);
        }
        
        return removed;
    }
    
    /**
     * 列出已安装技能
     */
    public List<SkillMetadata> listInstalledSkills() {
        return installedSkills.stream()
            .map(skills::get)
            .filter(Objects::nonNull)
            .map(SkillPackage::getMetadata)
            .toList();
    }
    
    /**
     * 列出所有可用技能
     */
    public List<SkillMetadata> listAllSkills() {
        return skills.values().stream()
            .map(SkillPackage::getMetadata)
            .toList();
    }
    
    /**
     * 评分技能
     */
    public void rateSkill(String skillId, double rating) {
        SkillPackage skillPackage = skills.get(skillId);
        if (skillPackage != null) {
            // 简单平均
            SkillMetadata metadata = skillPackage.getMetadata();
            double currentRating = metadata.getRating();
            long downloads = metadata.getDownloads();
            
            if (downloads > 0) {
                metadata.setRating((currentRating * (downloads - 1) + rating) / downloads);
            } else {
                metadata.setRating(rating);
            }
            
            metadata.setUpdatedAt(System.currentTimeMillis());
            
            log.info("技能评分：{} - {}", skillId, rating);
        }
    }
    
    /**
     * 更新技能
     */
    public SkillPackage updateSkill(String skillId, String content) {
        SkillPackage skillPackage = skills.get(skillId);
        if (skillPackage == null) {
            throw new IllegalArgumentException("技能不存在：" + skillId);
        }
        
        skillPackage.setContent(content);
        skillPackage.getMetadata().setUpdatedAt(System.currentTimeMillis());
        
        log.info("技能更新：{}", skillId);
        return skillPackage;
    }
    
    /**
     * 删除技能
     */
    public boolean deleteSkill(String skillId) {
        SkillPackage removed = skills.remove(skillId);
        if (removed != null) {
            installedSkills.remove(skillId);
            log.info("技能删除：{}", skillId);
            return true;
        }
        return false;
    }
    
    /**
     * 生成技能 ID
     */
    private String generateSkillId(String name) {
        return "skill-" + name.toLowerCase().replaceAll("\\s+", "-") + "-" + System.currentTimeMillis();
    }
    
    /**
     * 获取技能统计
     */
    public SkillMarketStats getStats() {
        SkillMarketStats stats = new SkillMarketStats();
        stats.setTotalSkills(skills.size());
        stats.setInstalledSkills(installedSkills.size());
        stats.setTotalDownloads(skills.values().stream()
            .mapToLong(s -> s.getMetadata().getDownloads())
            .sum());
        return stats;
    }
    
    /**
     * 技能市场统计
     */
    @Data
    public static class SkillMarketStats {
        private int totalSkills;
        private int installedSkills;
        private long totalDownloads;
    }
}
