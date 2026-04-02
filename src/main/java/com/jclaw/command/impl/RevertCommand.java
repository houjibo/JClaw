package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git Revert 命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class RevertCommand extends Command {
    
    public RevertCommand() {
        this.name = "revert";
        this.description = "Git 撤销提交";
        this.aliases = Arrays.asList("rv");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showHelp();
        }
        
        String commit = parts[0];
        boolean noEdit = parts.length > 1 && "--no-edit".equals(parts[1]);
        boolean noCommit = parts.length > 1 && "--no-commit".equals(parts[1]);
        
        return revert(commit, noEdit, noCommit);
    }
    
    private CommandResult revert(String commit, boolean noEdit, boolean noCommit) {
        if (commit == null) {
            return CommandResult.error("请指定提交哈希");
        }
        
        String options = "";
        if (noEdit) options += " --no-edit";
        if (noCommit) options += " --no-commit";
        
        String report = String.format("""
            ## Git Revert
            
            **操作**: revert %s%s
            **目标提交**: %s
            
            ### 执行步骤
            
            1. 创建一个新的反向提交
            2. 撤销目标提交的所有更改
            3. 保留完整的提交历史
            
            ### 执行命令
            
            ```bash
            git revert %s%s
            ```
            
            ### 与 Reset 的区别
            
            | 特性 | Revert | Reset |
            |------|--------|-------|
            | 历史记录 | 保留 | 重写 |
            | 安全性 | 安全 | 可能危险 |
            | 公共分支 | ✅ 推荐 | ❌ 不推荐 |
            | 创建新提交 | ✅ | ❌ |
            
            ### 选项说明
            
            | 选项 | 说明 |
            |------|------|
            | --no-edit | 使用默认提交信息 |
            | --no-commit | 应用更改但不提交 |
            | -n | 同 --no-commit |
            
            ⚠️ 如果有冲突，解决后运行 `git revert --continue`
            """, commit, options, commit, commit, options);
        
        return CommandResult.success("Revert 操作：" + commit)
                .withDisplayText(report);
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Git Revert 帮助
            
            ### 用法
            
            ```
            revert <提交>           # 撤销单个提交
            revert A..B             # 撤销提交范围
            revert --no-edit <提交> # 使用默认提交信息
            ```
            
            ### 常见场景
            
            1. **撤销错误的提交**
               ```
               revert abc123
               ```
            
            2. **撤销多个连续提交**
               ```
               revert A^..B
               ```
            
            3. **撤销后不立即提交**
               ```
               revert --no-commit abc123
               ```
            
            ### Revert vs Reset
            
            - **Revert**: 创建新提交撤销更改，安全，适合公共分支
            - **Reset**: 直接回退，重写历史，不适合公共分支
            
            ### 相关命令
            
            - `git reset` - 重置
            - `git cherry-pick` - 挑选提交
            - `git commit --amend` - 修改提交
            """;
        
        return CommandResult.success("Revert 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：revert
            别名：rv
            描述：Git 撤销提交
            
            用法：
              revert <提交>           # 撤销提交
              revert --no-edit <提交> # 使用默认信息
            
            示例：
              revert abc123
              revert HEAD~2..HEAD
            """;
    }
}
