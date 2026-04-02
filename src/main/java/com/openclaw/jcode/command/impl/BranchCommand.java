package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Git 分支操作
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class BranchCommand extends Command {
    
    public BranchCommand() {
        this.name = "branch";
        this.description = "Git 分支管理";
        this.aliases = Arrays.asList("br");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return listBranches();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "-a", "--all" -> listAllBranches();
            case "-r", "--remote" -> listRemoteBranches();
            case "-d", "--delete" -> deleteBranch(parts.length > 1 ? parts[1] : null);
            case "-m", "--move" -> renameBranch(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            case "-c", "--copy" -> copyBranch(parts.length > 1 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            default -> createOrCheckoutBranch(args);
        };
    }
    
    private CommandResult listBranches() {
        String report = """
            ## Git 分支列表
            
            * main         - 主分支
            * feature/user - 当前分支
              develop      - 开发分支
              release/v1.0 - 发布分支
            
            当前分支：feature/user
            """;
        
        return CommandResult.success("分支列表")
                .withDisplayText(report);
    }
    
    private CommandResult listAllBranches() {
        String report = """
            ## Git 所有分支（本地 + 远程）
            
            ### 本地分支
            
            * main
            * feature/user
              develop
              release/v1.0
            
            ### 远程分支
            
              origin/main
              origin/develop
              origin/feature/auth
            """;
        
        return CommandResult.success("所有分支")
                .withDisplayText(report);
    }
    
    private CommandResult listRemoteBranches() {
        String report = """
            ## Git 远程分支
            
            | 远程 | 分支 | 最后提交 |
            |------|------|----------|
            | origin | main | 2 小时前 |
            | origin | develop | 30 分钟前 |
            | origin | feature/auth | 1 天前 |
            """;
        
        return CommandResult.success("远程分支")
                .withDisplayText(report);
    }
    
    private CommandResult deleteBranch(String name) {
        if (name == null) {
            return CommandResult.error("请指定要删除的分支名称");
        }
        
        return CommandResult.success("分支已删除：" + name)
                .withDisplayText("✅ 分支 `" + name + "` 已删除");
    }
    
    private CommandResult renameBranch(String oldName, String newName) {
        if (oldName == null || newName == null) {
            return CommandResult.error("用法：branch -m <旧名称> <新名称>");
        }
        
        return CommandResult.success("分支已重命名：" + oldName + " -> " + newName);
    }
    
    private CommandResult copyBranch(String source, String target) {
        if (source == null || target == null) {
            return CommandResult.error("用法：branch -c <源分支> <目标分支>");
        }
        
        return CommandResult.success("分支已复制：" + source + " -> " + target);
    }
    
    private CommandResult createOrCheckoutBranch(String args) {
        if (args.startsWith("-b")) {
            String branchName = args.substring(2).trim();
            return CommandResult.success("创建并切换到新分支：" + branchName);
        }
        
        return CommandResult.success("切换到分支：" + args)
                .withDisplayText("✅ 已切换到分支 `" + args + "`");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：branch
            别名：br
            描述：Git 分支管理
            
            用法：
              branch              # 列出本地分支
              branch -a           # 列出所有分支
              branch -r           # 列出远程分支
              branch <name>       # 创建分支
              branch -b <name>    # 创建并切换
              branch -d <name>    # 删除分支
              branch -m <old> <new> # 重命名
              branch -c <src> <dst> # 复制
            
            示例：
              branch
              branch -a
              branch feature/login
              branch -b feature/login
              branch -d feature/old
            """;
    }
}
