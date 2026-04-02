package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git 变基命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class RebaseCommand extends Command {
    
    public RebaseCommand() {
        this.name = "rebase";
        this.description = "Git 变基操作";
        this.aliases = Arrays.asList("re");
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
        
        String action = parts[0];
        
        return switch (action) {
            case "-i", "--interactive" -> interactiveRebase(parts.length > 1 ? parts[1] : null);
            case "--continue" -> continueRebase();
            case "--abort" -> abortRebase();
            case "--skip" -> skipRebase();
            default -> standardRebase(parts[0]);
        };
    }
    
    private CommandResult standardRebase(String branch) {
        if (branch == null) {
            return CommandResult.error("请指定变基分支");
        }
        
        String report = String.format("""
            ## Git 变基
            
            **操作**: rebase %s
            
            ### 执行步骤
            
            1. 切换到当前分支
            2. 获取最新提交
            3. 变基到 %s
            4. 解决冲突（如果有）
            
            ### 命令
            
            ```bash
            git rebase %s
            ```
            
            ⚠️ 变基会重写提交历史，请谨慎使用！
            
            继续：rebase --continue
            跳过：rebase --skip
            中止：rebase --abort
            """, branch, branch, branch);
        
        return CommandResult.success("变基操作：" + branch)
                .withDisplayText(report);
    }
    
    private CommandResult interactiveRebase(String commit) {
        if (commit == null) {
            return CommandResult.error("请指定提交哈希或分支");
        }
        
        String report = String.format("""
            ## Git 交互式变基
            
            **操作**: rebase -i %s
            
            ### 可用命令
            
            | 命令 | 说明 |
            |------|------|
            | p, pick | 使用提交 |
            | r, reword | 使用提交，编辑提交信息 |
            | e, edit | 使用提交，停止以便修改 |
            | s, squash | 使用提交，压缩到前一个提交 |
            | f, fixup | 类似 squash，但丢弃提交信息 |
            | x, exec | 运行命令 |
            | d, drop | 删除提交 |
            
            ### 操作说明
            
            1. 编辑器会打开提交列表
            2. 修改每行的命令
            3. 保存并关闭编辑器
            4. Git 会按指示执行
            
            示例：
            ```
            pick abc123 第一个提交
            reword def456 修改这个提交信息
            squash ghi789 压缩到上一个提交
            ```
            """, commit);
        
        return CommandResult.success("交互式变基")
                .withDisplayText(report);
    }
    
    private CommandResult continueRebase() {
        return CommandResult.success("继续变基")
                .withDisplayText("""
                    ## 继续变基
                    
                    执行：git rebase --continue
                    
                    Git 会继续执行变基操作。
                    如果还有冲突，解决后再次运行此命令。
                    """);
    }
    
    private CommandResult abortRebase() {
        return CommandResult.success("中止变基")
                .withDisplayText("""
                    ## 中止变基
                    
                    执行：git rebase --abort
                    
                    ⚠️ 变基已中止，分支恢复到变基前的状态。
                    """);
    }
    
    private CommandResult skipRebase() {
        return CommandResult.success("跳过提交")
                .withDisplayText("""
                    ## 跳过当前提交
                    
                    执行：git rebase --skip
                    
                    Git 会跳过当前有冲突的提交，继续变基。
                    """);
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Git 变基帮助
            
            ### 用法
            
            ```
            rebase <分支>           # 标准变基
            rebase -i <提交>        # 交互式变基
            rebase --continue       # 继续变基
            rebase --abort          # 中止变基
            rebase --skip           # 跳过提交
            ```
            
            ### 常见场景
            
            1. **整合特性分支**
               ```
               git rebase main
               ```
            
            2. **交互式整理提交**
               ```
               git rebase -i HEAD~3
               ```
            
            3. **解决冲突后继续**
               ```
               git rebase --continue
               ```
            
            ### ⚠️ 注意事项
            
            - 变基会重写提交历史
            - 不要在公共分支上使用
            - 变基前建议备份分支
            
            ### 相关命令
            
            - `git merge` - 合并分支
            - `git cherry-pick` - 挑选提交
            - `git reset` - 重置分支
            """;
        
        return CommandResult.success("变基帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：rebase
            别名：re
            描述：Git 变基操作
            
            用法：
              rebase <分支>           # 标准变基
              rebase -i <提交>        # 交互式变基
              rebase --continue       # 继续变基
              rebase --abort          # 中止变基
            
            示例：
              rebase main
              rebase -i HEAD~3
            """;
    }
}
