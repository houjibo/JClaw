package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git Reset 命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ResetCommand extends Command {
    
    public ResetCommand() {
        this.name = "reset";
        this.description = "Git 重置操作";
        this.aliases = Arrays.asList("rst");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = true;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showHelp();
        }
        
        String mode = "--mixed";
        String target = "HEAD";
        
        for (String part : parts) {
            if (part.equals("--soft")) mode = "--soft";
            else if (part.equals("--mixed")) mode = "--mixed";
            else if (part.equals("--hard")) mode = "--hard";
            else if (!part.startsWith("-")) target = part;
        }
        
        return reset(mode, target);
    }
    
    private CommandResult reset(String mode, String target) {
        String report = String.format("""
            ## Git Reset
            
            **模式**: %s
            **目标**: %s
            
            ### 模式说明
            
            | 模式 | HEAD | 暂存区 | 工作区 |
            |------|------|--------|--------|
            | --soft | 重置 | 不变 | 不变 |
            | --mixed | 重置 | 重置 | 不变 |
            | --hard | 重置 | 重置 | 重置 |
            
            ### 执行命令
            
            ```bash
            git reset %s %s
            ```
            
            ### 效果
            
            %s
            
            ⚠️ --hard 会丢弃未提交的更改，请谨慎使用！
            """,
            mode, target,
            mode, target,
            getEffectDescription(mode));
        
        return CommandResult.success("Reset 操作：" + mode + " " + target)
                .withDisplayText(report);
    }
    
    private String getEffectDescription(String mode) {
        return switch (mode) {
            case "--soft" -> "HEAD 移动到目标，暂存区和工作区保持不变";
            case "--mixed" -> "HEAD 和暂存区重置，工作区保持不变（默认）";
            case "--hard" -> "HEAD、暂存区和工作区全部重置（危险！）";
            default -> "未知模式";
        };
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Git Reset 帮助
            
            ### 用法
            
            ```
            reset [选项] [目标]
            ```
            
            ### 选项
            
            | 选项 | 说明 |
            |------|------|
            | --soft | 只重置 HEAD |
            | --mixed | 重置 HEAD 和暂存区（默认） |
            | --hard | 重置所有（危险！） |
            
            ### 常见场景
            
            1. **撤销上一次提交（保留更改）**
               ```
               reset --soft HEAD~1
               ```
            
            2. **取消暂存（保留工作区）**
               ```
               reset HEAD file.txt
               ```
            
            3. **丢弃所有未提交更改**
               ```
               reset --hard HEAD
               ```
            
            ⚠️ 警告：--hard 会永久丢失未提交的更改！
            """;
        
        return CommandResult.success("Reset 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：reset
            别名：rst
            描述：Git 重置操作
            
            用法：
              reset --soft <目标>    # 只重置 HEAD
              reset --mixed <目标>   # 重置 HEAD 和暂存区
              reset --hard <目标>    # 重置所有（危险）
            
            示例：
              reset --soft HEAD~1
              reset --hard origin/main
            """;
    }
}
