package com.jclaw.skill;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 技能市场服务（完整版）
 * 
 * 功能：
 * - 技能注册/发布
 * - 技能搜索/发现
 * - 技能安装/卸载
 * - 技能版本管理
 * - 技能包导出/导入
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
        private String license;
        private List<String> tags;
        private Map<String, String> dependencies;
        private long downloads;
        private double rating;
        private long createdAt;
        private long updatedAt;
        private String repository;
        private String documentation;
    }
    
    /**
     * 技能包
     */
    @Data
    public static class SkillPackage {
        private SkillMetadata metadata;
        private String content;
        private String checksum;
        private List<String> files;
    }
    
    /**
     * 已注册技能（本地注册表）
     */
    private final Map<String, SkillPackage> skills = new ConcurrentHashMap<>();
    
    /**
     * 已安装技能
     */
    private final Set<String> installedSkills = ConcurrentHashMap.newKeySet();
    
    /**
     * 技能安装目录
     */
    private final Path skillsDir = Paths.get(System.getProperty("user.home"), ".jclaw", "skills");
    
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
     * 发布技能到目录
     */
    public Path publishSkill(SkillPackage skillPackage, Path outputDir) throws IOException {
        log.info("发布技能：{} v{} 到 {}", 
            skillPackage.getMetadata().getName(), 
            skillPackage.getMetadata().getVersion(),
            outputDir);
        
        // 创建技能包目录
        String skillDirName = skillPackage.getMetadata().getName() + "-" + skillPackage.getMetadata().getVersion();
        Path skillDir = outputDir.resolve(skillDirName);
        Files.createDirectories(skillDir);
        
        // 写入元数据
        Path metadataPath = skillDir.resolve("skill.json");
        writeJson(metadataPath, skillPackage.getMetadata());
        
        // 写入技能内容
        if (skillPackage.getContent() != null) {
            Path contentPath = skillDir.resolve("Skill.java");
            Files.writeString(contentPath, skillPackage.getContent());
        }
        
        // 写入 README
        Path readmePath = skillDir.resolve("README.md");
        String readme = generateReadme(skillPackage.getMetadata());
        Files.writeString(readmePath, readme);
        
        // 创建 ZIP 包
        Path zipPath = outputDir.resolve(skillDirName + ".zip");
        createZip(skillDir, zipPath);
        
        // 清理临时目录
        deleteDirectory(skillDir);
        
        log.info("技能发布成功：{}", zipPath);
        return zipPath;
    }
    
    /**
     * 安装技能
     */
    public boolean installSkill(Path skillPackagePath) throws IOException {
        log.info("安装技能包：{}", skillPackagePath);
        
        // 解压技能包
        Path extractDir = Files.createTempDirectory("jclaw-skill-");
        unzip(skillPackagePath, extractDir);
        
        // 读取元数据
        Path metadataPath = extractDir.resolve("skill.json");
        if (!Files.exists(metadataPath)) {
            log.error("技能包缺少 skill.json");
            return false;
        }
        
        SkillMetadata metadata = readJson(metadataPath, SkillMetadata.class);
        
        // 检查依赖
        if (!checkDependencies(metadata)) {
            log.error("技能依赖不满足：{}", metadata.getName());
            return false;
        }
        
        // 安装技能文件
        Path installDir = skillsDir.resolve(metadata.getId());
        Files.createDirectories(installDir);
        copyDirectory(extractDir, installDir);
        
        // 标记为已安装
        installedSkills.add(metadata.getId());
        
        log.info("技能安装成功：{} v{}", metadata.getName(), metadata.getVersion());
        return true;
    }
    
    /**
     * 卸载技能
     */
    public boolean uninstallSkill(String skillId) throws IOException {
        log.info("卸载技能：{}", skillId);
        
        Path skillDir = skillsDir.resolve(skillId);
        if (!Files.exists(skillDir)) {
            log.warn("技能未安装：{}", skillId);
            return false;
        }
        
        deleteDirectory(skillDir);
        installedSkills.remove(skillId);
        
        log.info("技能卸载成功：{}", skillId);
        return true;
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
     * 检查技能是否已安装
     */
    public boolean isInstalled(String skillId) {
        return installedSkills.contains(skillId);
    }
    
    // ==================== 辅助方法 ====================
    
    private String generateSkillId(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "-");
    }
    
    private void writeJson(Path path, Object obj) throws IOException {
        String json = new com.fasterxml.jackson.databind.ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(obj);
        Files.writeString(path, json);
    }
    
    private <T> T readJson(Path path, Class<T> clazz) throws IOException {
        String json = Files.readString(path);
        return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, clazz);
    }
    
    private String generateReadme(SkillMetadata metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(metadata.getName()).append("\n\n");
        sb.append(metadata.getDescription()).append("\n\n");
        sb.append("## 信息\n\n");
        sb.append("- **版本**: ").append(metadata.getVersion()).append("\n");
        sb.append("- **作者**: ").append(metadata.getAuthor()).append("\n");
        if (metadata.getLicense() != null) {
            sb.append("- **许可证**: ").append(metadata.getLicense()).append("\n");
        }
        if (metadata.getTags() != null && !metadata.getTags().isEmpty()) {
            sb.append("- **标签**: ").append(String.join(", ", metadata.getTags())).append("\n");
        }
        sb.append("\n## 安装\n\n");
        sb.append("```bash\njclaw skill install ").append(metadata.getId()).append("\n```\n");
        return sb.toString();
    }
    
    private void createZip(Path sourceDir, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDir)
                .filter(path -> !path.equals(sourceDir))
                .forEach(path -> {
                    try {
                        String zipPathStr = sourceDir.relativize(path).toString();
                        zos.putNextEntry(new ZipEntry(zipPathStr));
                        if (!Files.isDirectory(path)) {
                            Files.copy(path, zos);
                        }
                        zos.closeEntry();
                    } catch (IOException e) {
                        log.error("创建 ZIP 失败", e);
                    }
                });
        }
    }
    
    private void unzip(Path zipPath, Path extractDir) throws IOException {
        try (var zip = new java.util.zip.ZipFile(zipPath.toFile())) {
            zip.entries().asIterator().forEachRemaining(entry -> {
                try {
                    Path entryPath = extractDir.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        try (var is = zip.getInputStream(entry)) {
                            Files.copy(is, entryPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    log.error("解压失败", e);
                }
            });
        }
    }
    
    private boolean checkDependencies(SkillMetadata metadata) {
        if (metadata.getDependencies() == null || metadata.getDependencies().isEmpty()) {
            return true;
        }
        
        // TODO: 检查依赖是否满足
        log.info("检查依赖：{}", metadata.getDependencies());
        return true;
    }
    
    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source)
            .forEach(path -> {
                try {
                    Path targetPath = target.resolve(source.relativize(path));
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    log.error("复制文件失败", e);
                }
            });
    }
    
    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("删除文件失败：{}", path, e);
                    }
                });
        }
    }
}
