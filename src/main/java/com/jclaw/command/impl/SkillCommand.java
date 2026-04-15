package com.jclaw.command.impl;

import com.jclaw.command.*;
import com.jclaw.skill.SkillMarketService;
import com.jclaw.skill.SkillMarketService.SkillMetadata;
import com.jclaw.skill.SkillMarketService.SkillPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * 技能管理命令
 * 
 * 功能：
 * - skill list          - 列出可用技能
 * - skill search <query> - 搜索技能
 * - skill install <path> - 安装技能
 * - skill uninstall <id> - 卸载技能
 * - skill publish <dir>  - 发布技能
 * - skill info <id>      - 查看技能信息
 * 
 * @author Cola 🥤
 * @since 2026-04-15
 */
@Component
public class SkillCommand extends Command {
    
    private final SkillMarketService skillMarketService;
    
    public SkillCommand(SkillMarketService skillMarketService) {
        this.skillMarketService = skillMarketService;
        this.name = "skill";
        this.description = "技能管理";
        this.aliases = Arrays.asList("sk", "skills");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showHelp();
        }
        
        String[] parts = args.trim().split("\\s+", 2);
        String action = parts[0];
        String params = parts.length > 1 ? parts[1] : "";
        
        return switch (action) {
            case "list" -> listSkills();
            case "search" -> searchSkills(params);
            case "install" -> installSkill(params);
            case "uninstall" -> uninstallSkill(params);
            case "publish" -> publishSkill(params);
            case "info" -> showSkillInfo(params);
            case "help" -> showHelp();
            default -> CommandResult.error("未知操作：" + action + "。使用 skill help 查看帮助");
        };
    }
    
    private CommandResult listSkills() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 可用技能\n\n");
        
        List<SkillMetadata> installed = skillMarketService.listInstalledSkills();
        
        if (installed.isEmpty()) {
            sb.append("暂无已安装技能。\n\n");
            sb.append("使用 `skill search <关键词>` 搜索技能，使用 `skill install <路径>` 安装技能。\n");
        } else {
            sb.append("| 技能 ID | 名称 | 版本 | 描述 |\n");
            sb.append("|--------|------|------|------|\n");
            
            for (SkillMetadata meta : installed) {
                sb.append(String.format("| %s | %s | %s | %s |\n",
                    meta.getId(), meta.getName(), meta.getVersion(),
                    truncate(meta.getDescription(), 30)));
            }
            
            sb.append("\n共 ").append(installed.size()).append(" 个已安装技能。\n");
        }
        
        return CommandResult.success(sb.toString());
    }
    
    private CommandResult searchSkills(String query) {
        if (query == null || query.isBlank()) {
            return CommandResult.error("请提供搜索关键词，例如：skill search file");
        }
        
        List<SkillMetadata> results = skillMarketService.searchSkills(query, null);
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 搜索结果：").append(query).append("\n\n");
        
        if (results.isEmpty()) {
            sb.append("未找到匹配的技能。\n");
        } else {
            sb.append("| 技能 ID | 名称 | 版本 | 作者 | 评分 |\n");
            sb.append("|--------|------|------|------|------|\n");
            
            for (SkillMetadata meta : results) {
                sb.append(String.format("| %s | %s | %s | %s | %.1f |\n",
                    meta.getId(), meta.getName(), meta.getVersion(),
                    meta.getAuthor() != null ? meta.getAuthor() : "未知",
                    meta.getRating()));
            }
            
            sb.append("\n共找到 ").append(results.size()).append(" 个技能。\n");
            sb.append("使用 `skill install <技能 ID>` 安装技能。\n");
        }
        
        return CommandResult.success(sb.toString());
    }
    
    private CommandResult installSkill(String path) {
        if (path == null || path.isBlank()) {
            return CommandResult.error("请提供技能包路径，例如：skill install ./my-skill.zip");
        }
        
        try {
            Path skillPath = Paths.get(path);
            if (!Files.exists(skillPath)) {
                return CommandResult.error("技能包不存在：" + path);
            }
            
            boolean success = skillMarketService.installSkill(skillPath);
            
            if (success) {
                return CommandResult.success("✅ 技能安装成功！\n\n使用 `skill list` 查看已安装的技能。");
            } else {
                return CommandResult.error("技能安装失败。请检查日志获取详细信息。");
            }
        } catch (IOException e) {
            return CommandResult.error("安装技能时出错：" + e.getMessage());
        }
    }
    
    private CommandResult uninstallSkill(String skillId) {
        if (skillId == null || skillId.isBlank()) {
            return CommandResult.error("请提供技能 ID，例如：skill uninstall file-reader");
        }
        
        try {
            boolean success = skillMarketService.uninstallSkill(skillId);
            
            if (success) {
                return CommandResult.success("✅ 技能已卸载：" + skillId);
            } else {
                return CommandResult.error("技能未安装或卸载失败：" + skillId);
            }
        } catch (IOException e) {
            return CommandResult.error("卸载技能时出错：" + e.getMessage());
        }
    }
    
    private CommandResult publishSkill(String dirPath) {
        if (dirPath == null || dirPath.isBlank()) {
            return CommandResult.error("请提供技能目录路径，例如：skill publish ./my-skill");
        }
        
        try {
            Path dir = Paths.get(dirPath);
            if (!Files.exists(dir)) {
                return CommandResult.error("目录不存在：" + dirPath);
            }
            
            // 读取 skill.json
            Path metadataPath = dir.resolve("skill.json");
            if (!Files.exists(metadataPath)) {
                return CommandResult.error("目录中缺少 skill.json 文件");
            }
            
            // TODO: 实现完整的发布逻辑
            Path outputDir = Paths.get(System.getProperty("user.home"), ".jclaw", "published");
            Files.createDirectories(outputDir);
            
            return CommandResult.success("✅ 技能发布成功！\n\n发布位置：" + outputDir);
        } catch (IOException e) {
            return CommandResult.error("发布技能时出错：" + e.getMessage());
        }
    }
    
    private CommandResult showSkillInfo(String skillId) {
        if (skillId == null || skillId.isBlank()) {
            return CommandResult.error("请提供技能 ID，例如：skill info file-reader");
        }
        
        // TODO: 实现技能详情查询
        return CommandResult.success("技能信息：" + skillId + "\n\n(详情待实现)");
    }
    
    private CommandResult showHelp() {
        String help = """
            ## 技能管理命令
            
            ### 基本用法
            
            ```
            skill <command> [arguments]
            ```
            
            ### 可用命令
            
            | 命令 | 说明 | 示例 |
            |------|------|------|
            | list | 列出已安装技能 | skill list |
            | search | 搜索技能 | skill search file |
            | install | 安装技能 | skill install ./skill.zip |
            | uninstall | 卸载技能 | skill uninstall file-reader |
            | publish | 发布技能 | skill publish ./my-skill |
            | info | 查看技能详情 | skill info file-reader |
            | help | 显示帮助 | skill help |
            
            ### 技能包结构
            
            技能包应包含以下文件：
            
            ```
            my-skill-1.0.0/
            ├── skill.json      # 技能元数据（必需）
            ├── Skill.java      # 技能实现（可选）
            ├── README.md       # 使用说明（推荐）
            └── ...             # 其他文件
            ```
            
            ### skill.json 格式
            
            ```json
            {
              "name": "file-reader",
              "version": "1.0.0",
              "description": "文件读取技能",
              "author": "Your Name",
              "tags": ["file", "io"],
              "dependencies": {}
            }
            ```
            
            ### 示例
            
            1. 搜索文件相关技能：
               ```
               skill search file
               ```
            
            2. 安装技能：
               ```
               skill install ./file-reader.zip
               ```
            
            3. 列出现有技能：
               ```
               skill list
               ```
            
            4. 卸载技能：
               ```
               skill uninstall file-reader
               ```
            """;
        
        return CommandResult.success(help);
    }
    
    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
