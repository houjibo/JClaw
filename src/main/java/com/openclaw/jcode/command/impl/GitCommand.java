package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * 支持：commit, branch, status, log, diff 等
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class GitCommand extends Command {
    
    public GitCommand() {
        this.name = "git";
        this.description = "Git 版本控制操作";
        this.aliases = Arrays.asList("g");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        // 定义参数
        this.parameters.put("subcommand", new CommandParameter(
            "subcommand", 
            "Git 子命令 (status, commit, branch, log, diff, push, pull)", 
            true
        ).choices("status", "commit", "branch", "log", "diff", "push", "pull", "add", "reset", "checkout"));
        
        this.parameters.put("message", new CommandParameter(
            "message", 
            "提交消息（commit 时使用）", 
            false
        ));
        
        this.parameters.put("branch", new CommandParameter(
            "branch", 
            "分支名称", 
            false
        ));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return CommandResult.error("请指定 Git 子命令，例如：git status");
        }
        
        try {
            // 解析命令参数
            String[] parts = args.trim().split("\\s+", 2);
            String subcommand = parts[0];
            String subArgs = parts.length > 1 ? parts[1] : "";
            
            // 执行 Git 命令
            ProcessBuilder pb = new ProcessBuilder("git", subcommand);
            
            // 添加额外参数
            if (!subArgs.isBlank()) {
                String[] extraArgs = subArgs.split("\\s+");
                for (String arg : extraArgs) {
                    if (!arg.isBlank()) {
                        pb.command().add(arg);
                    }
                }
            }
            
            pb.directory(new java.io.File(context.getWorkingDirectory()));
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return CommandResult.success(
                    "Git " + subcommand + " 执行成功",
                    java.util.Map.of("output", output.toString().trim())
                ).withDisplayText(formatGitOutput(subcommand, output.toString()));
            } else {
                return CommandResult.error("Git 命令执行失败 (exit=" + exitCode + "): " + output.toString());
            }
            
        } catch (IOException | InterruptedException e) {
            context.error("Git 命令执行失败：" + e.getMessage());
            return CommandResult.error("Git 命令执行失败：" + e.getMessage());
        }
    }
    
    /**
     * 格式化 Git 输出
     */
    private String formatGitOutput(String subcommand, String output) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Git ").append(subcommand).append("\n\n");
        sb.append("```\n").append(output).append("\n```");
        return sb.toString();
    }
    
    /**
     * 获取帮助信息
     */
    @Override
    public String getHelp() {
        return """
            命令：git
            描述：Git 版本控制操作
            别名：g
            
            用法：
              git <subcommand> [options]
            
            支持的子命令：
              status  - 查看仓库状态
              commit  - 提交更改（使用 -m 指定消息）
              branch  - 分支管理
              log     - 查看提交历史
              diff    - 查看差异
              push    - 推送到远程
              pull    - 从远程拉取
              add     - 添加文件到暂存区
              reset   - 重置暂存区
              checkout- 切换分支
            
            示例：
              git status
              git commit -m "修复 bug"
              git branch -a
              git log --oneline
              git diff HEAD
            """;
    }
}
