package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git Cherry-Pick 命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CherryPickCommand extends Command {
    
    public CherryPickCommand() {
        this.name = "cherry-pick";
        this.description = "Git 挑选提交";
        this.aliases = Arrays.asList("cherry", "cp");
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
        boolean edit = parts.length > 1 && "--edit".equals(parts[1]);
        boolean noCommit = parts.length > 1 && "--no-commit".equals(parts[1]);
        
        return cherryPick(commit, edit, noCommit);
    }
    
    private CommandResult cherryPick(String commit, boolean edit, boolean noCommit) {
        if (commit == null) {
            return CommandResult.error("请指定提交哈希");
        }
        
        String options = "";
        if (edit) options += " --edit";
        if (noCommit) options += " --no-commit";
        
        String report = String.format("""
            ## Git Cherry-Pick
            
            **操作**: cherry-pick %s%s
            
            ### 执行步骤
            
            1. 切换到目标分支
            2. 应用提交 %s
            3. 解决冲突（如果有）
            4. 完成提交
            
            ### 命令
            
            ```bash
            git cherry-pick %s%s
            ```
            
            ### 选项说明
            
            | 选项 | 说明 |
            |------|------|
            | --edit | 编辑提交信息 |
            | --no-commit | 应用更改但不提交 |
            | -x | 在提交信息中添加引用 |
            | -n | 同 --no-commit |
            | -s | 添加 Signed-off-by 行 |
            
            ### 常见场景
            
            1. **从其他分支挑选修复**
               ```
               cherry-pick abc123
               ```
            
            2. **挑选多个提交**
               ```
               cherry-pick A^..B
               ```
            
            3. **挑选后编辑提交信息**
               ```
               cherry-pick --edit abc123
               ```
            
            ⚠️ 如果有冲突，解决后运行 `git cherry-pick --continue`
            """, commit, options, commit, commit, options);
        
        return CommandResult.success("Cherry-pick 操作：" + commit)
                .withDisplayText(report);
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Git Cherry-Pick 帮助
            
            ### 用法
            
            ```
            cherry-pick <提交>        # 挑选单个提交
            cherry-pick A^..B         # 挑选多个提交
            cherry-pick --edit <提交> # 挑选并编辑
            ```
            
            ### 常见场景
            
            1. **修复 bug 后应用到其他分支**
               ```
               # 在 hotfix 分支修复
               git checkout hotfix
               # 修复并提交
               git checkout main
               cherry-pick abc123
               ```
            
            2. **选择性应用提交**
               ```
               cherry-pick abc123 def456
               ```
            
            3. **应用提交范围**
               ```
               cherry-pick A^..B
               ```
            
            ### 冲突处理
            
            - 解决冲突文件
            - `git add <文件>`
            - `git cherry-pick --continue`
            
            ### 相关命令
            
            - `git rebase` - 变基
            - `git merge` - 合并
            - `git reset` - 重置
            """;
        
        return CommandResult.success("Cherry-pick 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：cherry-pick
            别名：cherry, cp
            描述：Git 挑选提交
            
            用法：
              cherry-pick <提交>        # 挑选提交
              cherry-pick --edit <提交> # 挑选并编辑
            
            示例：
              cherry-pick abc123
              cherry-pick A^..B
            """;
    }
}
