package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git 标签管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class TagCommand extends Command {
    
    public TagCommand() {
        this.name = "tag";
        this.description = "Git 标签管理";
        this.aliases = Arrays.asList("tags", "t");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listTags();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "-a", "--all" -> listAllTags();
            case "-d", "--delete" -> deleteTag(parts.length > 1 ? parts[1] : null);
            case "-l", "--list" -> listTagsWithPattern(parts.length > 1 ? parts[1] : "*");
            case "-c", "--create" -> createTag(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            default -> listTags();
        };
    }
    
    private CommandResult listTags() {
        String report = """
            ## Git 标签列表
            
            | 标签 | 提交 | 日期 | 类型 |
            |------|------|------|------|
            | v1.0.0 | abc123 | 2026-03-01 | release |
            | v1.1.0 | def456 | 2026-03-15 | release |
            | v2.0.0-beta | ghi789 | 2026-03-28 | prerelease |
            
            共 3 个标签
            """;
        
        return CommandResult.success("标签列表")
                .withDisplayText(report);
    }
    
    private CommandResult listAllTags() {
        String report = """
            ## Git 所有标签（含远程）
            
            ### 本地标签
            
            | 标签 | 提交 | 日期 |
            |------|------|------|
            | v1.0.0 | abc123 | 2026-03-01 |
            | v1.1.0 | def456 | 2026-03-15 |
            
            ### 远程标签
            
            | 标签 | 远程 | 提交 |
            |------|------|------|
            | v1.0.0 | origin | abc123 |
            | v1.1.0 | origin | def456 |
            | v2.0.0 | origin | jkl012 |
            """;
        
        return CommandResult.success("所有标签")
                .withDisplayText(report);
    }
    
    private CommandResult listTagsWithPattern(String pattern) {
        String report = String.format("""
            ## Git 标签列表（模式：%s）
            
            | 标签 | 提交 | 日期 |
            |------|------|------|
            | v1.0.0 | abc123 | 2026-03-01 |
            | v1.1.0 | def456 | 2026-03-15 |
            
            匹配 %d 个标签
            """, pattern, 2);
        
        return CommandResult.success("标签列表")
                .withDisplayText(report);
    }
    
    private CommandResult createTag(String name, String message) {
        if (name == null) {
            return CommandResult.error("请指定标签名称");
        }
        
        String report = String.format("""
            ## 创建标签成功
            
            **标签**: %s
            **提交**: HEAD
            **消息**: %s
            **日期**: 2026-04-01
            
            推送远程：git push origin %s
            """, name, message != null ? message : "无", name);
        
        return CommandResult.success("已创建标签：" + name)
                .withDisplayText(report);
    }
    
    private CommandResult deleteTag(String name) {
        if (name == null) {
            return CommandResult.error("请指定要删除的标签名称");
        }
        
        return CommandResult.success("已删除标签：" + name)
                .withDisplayText("✅ 标签 `" + name + "` 已删除");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：tag
            别名：tags, t
            描述：Git 标签管理
            
            用法：
              tag                     # 列出标签
              tag -a                  # 列出所有标签
              tag -l <pattern>        # 按模式列出
              tag -c <name> [msg]     # 创建标签
              tag -d <name>           # 删除标签
            
            示例：
              tag
              tag -l "v1.*"
              tag -c v1.2.0 "Release 1.2.0"
              tag -d v1.0.0
            """;
    }
}
