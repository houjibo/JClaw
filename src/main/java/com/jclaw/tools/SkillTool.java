package com.jclaw.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能管理工具 - 增强版
 * 
 * 功能：
 * - 技能列出
 * - 技能激活/关闭
 * - 技能安装/卸载
 * - 技能配置管理
 * - 技能自动发现
 */
@Component
public class SkillTool extends Tool {
    
    private static final Logger logger = LoggerFactory.getLogger(SkillTool.class);
    
    /**
     * 技能定义
     */
    public static class SkillDefinition {
        public String name;
        public String description;
        public String version;
        public boolean enabled;
        public Map<String, Object> config;
        public Path path;
        
        public SkillDefinition(String name, String description, String version) {
            this.name = name;
            this.description = description;
            this.version = version;
            this.enabled = false;
            this.config = new HashMap<>();
        }
    }
    
    /**
     * 技能存储
     */
    private final ConcurrentHashMap<String, SkillDefinition> skills = new ConcurrentHashMap<>();
    
    /**
     * 技能目录
     */
    private final Path skillsDir;
    
    /**
     * ObjectMapper
     */
    private final ObjectMapper objectMapper;
    
    public SkillTool() {
        this.name = "skill";
        this.description = "管理技能（激活/切换/列出/安装/卸载）";
        this.category = ToolCategory.SKILL;
        this.requiresConfirmation = false;
        this.objectMapper = new ObjectMapper();
        
        // 初始化技能目录
        this.skillsDir = Paths.get(
            System.getProperty("user.home"),
            ".openclaw",
            "jcode",
            "skills"
        );
        
        // 加载技能
        loadSkills();
    }
    
    /**
     * 加载技能
     */
    private void loadSkills() {
        try {
            Files.createDirectories(skillsDir);
            
            // 内置技能
            registerBuiltInSkills();
            
            // 从目录加载技能
            if (Files.exists(skillsDir)) {
                try (var stream = Files.list(skillsDir)) {
                    stream.filter(Files::isDirectory)
                        .forEach(this::loadSkillFromDir);
                }
            }
            
            logger.info("加载了 {} 个技能", skills.size());
            
        } catch (IOException e) {
            logger.error("加载技能失败", e);
        }
    }
    
    /**
     * 注册内置技能
     */
    private void registerBuiltInSkills() {
        skills.put("java-expert", new SkillDefinition(
            "java-expert",
            "Java 开发专家 - 精通 Java、Spring Boot、微服务",
            "1.0.0"
        ));
        
        skills.put("python-expert", new SkillDefinition(
            "python-expert",
            "Python 开发专家 - 精通 Python、Django、FastAPI",
            "1.0.0"
        ));
        
        skills.put("reviewer", new SkillDefinition(
            "reviewer",
            "代码审查专家 - 代码质量检查、最佳实践建议",
            "1.0.0"
        ));
        
        skills.put("architect", new SkillDefinition(
            "architect",
            "架构师 - 系统设计、架构评审、技术选型",
            "1.0.0"
        ));
        
        skills.put("devops", new SkillDefinition(
            "devops",
            "DevOps 专家 - CI/CD、Docker、Kubernetes",
            "1.0.0"
        ));
    }
    
    /**
     * 从目录加载技能
     */
    private void loadSkillFromDir(Path skillDir) {
        try {
            Path manifestPath = skillDir.resolve("skill.json");
            if (Files.exists(manifestPath)) {
                String content = Files.readString(manifestPath);
                Map<?, ?> manifest = objectMapper.readValue(content, Map.class);
                
                String name = (String) manifest.get("name");
                String description = (String) manifest.get("description");
                String version = (String) manifest.get("version");
                
                SkillDefinition skill = new SkillDefinition(name, description, version);
                skill.path = skillDir;
                Object configObj = manifest.get("config");
                if (configObj instanceof Map) {
                    skill.config = new HashMap<>((Map<String, Object>) configObj);
                }
                
                skills.put(name, skill);
                logger.info("加载技能：{}", name);
            }
        } catch (IOException e) {
            logger.error("加载技能失败：{}", skillDir, e);
        }
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String action = getStringParam(params.get("action"));
        
        if (action == null) {
            action = "list";
        }
        
        return switch (action.toLowerCase()) {
            case "list" -> listSkills();
            case "activate" -> activateSkill(getStringParam(params.get("name")));
            case "deactivate" -> deactivateSkill(getStringParam(params.get("name")));
            case "install" -> installSkill(getStringParam(params.get("name")), getStringParam(params.get("url")));
            case "uninstall" -> uninstallSkill(getStringParam(params.get("name")));
            case "config" -> configureSkill(getStringParam(params.get("name")), 
                                            getMapParam(params.get("config")));
            case "info" -> getSkillInfo(getStringParam(params.get("name")));
            default -> ToolResult.error("未知操作：" + action + "，支持：list, activate, deactivate, install, uninstall, config, info");
        };
    }
    
    /**
     * 列出技能
     */
    private ToolResult listSkills() {
        StringBuilder sb = new StringBuilder();
        sb.append("可用技能:\n\n");
        
        int enabledCount = 0;
        for (SkillDefinition skill : skills.values()) {
            String status = skill.enabled ? "✅" : "⏸";
            sb.append(String.format("%s %-20s - %s (v%s)\n", 
                status, skill.name, skill.description, skill.version));
            if (skill.enabled) enabledCount++;
        }
        
        sb.append(String.format("\n总计：%d 个技能，已激活：%d 个", skills.size(), enabledCount));
        
        return ToolResult.success("找到 " + skills.size() + " 个技能", sb.toString());
    }
    
    /**
     * 激活技能
     */
    private ToolResult activateSkill(String name) {
        if (name == null) {
            return ToolResult.error("name 参数不能为空");
        }
        
        SkillDefinition skill = skills.get(name);
        if (skill == null) {
            return ToolResult.error("技能不存在：" + name);
        }
        
        skill.enabled = true;
        logger.info("激活技能：{}", name);
        
        return ToolResult.success("技能已激活", "已激活技能：" + name);
    }
    
    /**
     * 关闭技能
     */
    private ToolResult deactivateSkill(String name) {
        if (name == null) {
            return ToolResult.error("name 参数不能为空");
        }
        
        SkillDefinition skill = skills.get(name);
        if (skill == null) {
            return ToolResult.error("技能不存在：" + name);
        }
        
        skill.enabled = false;
        logger.info("关闭技能：{}", name);
        
        return ToolResult.success("技能已关闭", "已关闭技能：" + name);
    }
    
    /**
     * 安装技能
     */
    private ToolResult installSkill(String name, String url) {
        if (name == null) {
            return ToolResult.error("name 参数不能为空");
        }
        
        if (url == null) {
            return ToolResult.error("url 参数不能为空（技能下载地址）");
        }
        
        // 模拟安装（实际应该下载并解压）
        logger.info("安装技能：{} from {}", name, url);
        
        SkillDefinition skill = new SkillDefinition(name, "外部技能", "1.0.0");
        skill.path = skillsDir.resolve(name);
        skills.put(name, skill);
        
        return ToolResult.success("技能已安装", "已安装技能：" + name + " from " + url);
    }
    
    /**
     * 卸载技能
     */
    private ToolResult uninstallSkill(String name) {
        if (name == null) {
            return ToolResult.error("name 参数不能为空");
        }
        
        SkillDefinition skill = skills.remove(name);
        if (skill == null) {
            return ToolResult.error("技能不存在：" + name);
        }
        
        // 删除技能目录
        if (skill.path != null && Files.exists(skill.path)) {
            try {
                Files.walkFileTree(skill.path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.error("删除技能目录失败：{}", name, e);
            }
        }
        
        logger.info("卸载技能：{}", name);
        return ToolResult.success("技能已卸载", "已卸载技能：" + name);
    }
    
    /**
     * 配置技能
     */
    private ToolResult configureSkill(String name, Map<String, Object> config) {
        if (name == null) {
            return ToolResult.error("name 参数不能为空");
        }
        
        SkillDefinition skill = skills.get(name);
        if (skill == null) {
            return ToolResult.error("技能不存在：" + name);
        }
        
        if (config != null) {
            skill.config.putAll(config);
        }
        
        logger.info("配置技能：{} - {}", name, config);
        return ToolResult.success("技能配置已更新", 
            "技能：" + name + "\n配置：" + (config != null ? config.toString() : "无"));
    }
    
    /**
     * 获取技能信息
     */
    private ToolResult getSkillInfo(String name) {
        if (name == null) {
            return ToolResult.error("name 参数不能为空");
        }
        
        SkillDefinition skill = skills.get(name);
        if (skill == null) {
            return ToolResult.error("技能不存在：" + name);
        }
        
        Map<String, Object> info = new HashMap<>();
        info.put("name", skill.name);
        info.put("description", skill.description);
        info.put("version", skill.version);
        info.put("enabled", skill.enabled);
        info.put("config", skill.config);
        info.put("path", skill.path != null ? skill.path.toString() : null);
        
        ToolResult result = ToolResult.success("技能信息");
        result.setData(info);
        return result;
    }
    
    // Helper methods
    private String getStringParam(Object obj) {
        return obj instanceof String ? (String) obj : null;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapParam(Object obj) {
        return obj instanceof Map ? (Map<String, Object>) obj : null;
    }
}
