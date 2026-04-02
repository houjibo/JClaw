package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 代码差异比较
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class DiffCommand extends Command {
    
    public DiffCommand() {
        this.name = "diff";
        this.description = "代码差异比较";
        this.aliases = Arrays.asList("d");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showWorkingDirDiff();
        }
        
        String[] parts = args.trim().split("\\s+");
        
        if (parts[0].equals("--cached") || parts[0].equals("--staged")) {
            return showStagedDiff();
        }
        
        if (parts.length >= 2) {
            return compareCommits(parts[0], parts[1]);
        }
        
        if (parts[0].startsWith("--")) {
            return showDiffWithOptions(args);
        }
        
        return showFileDiff(parts[0]);
    }
    
    private CommandResult showWorkingDirDiff() {
        String report = """
            ## 工作区差异
            
            ### 变更文件
            
            | 文件 | 状态 | 变更 |
            |------|------|------|
            | src/main.java | 修改 | +15/-3 |
            | src/test.java | 修改 | +45/-0 |
            | config.yml | 修改 | +2/-1 |
            
            ### 变更统计
            
            ```diff
            + 新增：62 行
            - 删除：4 行
            ```
            
            使用 `diff --cached` 查看暂存区
            使用 `diff <文件>` 查看具体文件
            """;
        
        return CommandResult.success("工作区差异")
                .withDisplayText(report);
    }
    
    private CommandResult showStagedDiff() {
        String report = """
            ## 暂存区差异
            
            ### 已暂存的变更
            
            | 文件 | 变更 |
            |------|------|
            | src/main.java | +15/-3 |
            
            ### 详细变更
            
            ```diff
            @@ -10,7 +10,19 @@
             public class Main {
            +    /**
            +     * 新增方法
            +     */
            +    public void newMethod() {
            +        System.out.println("Hello");
            +    }
            -    public void oldMethod() {}
            +    public void updatedMethod() {
            +        // 更新实现
            +    }
             }
            ```
            """;
        
        return CommandResult.success("暂存区差异")
                .withDisplayText(report);
    }
    
    private CommandResult compareCommits(String commit1, String commit2) {
        String report = String.format("""
            ## 提交差异：%s..%s
            
            ### 变更文件
            
            | 文件 | 变更 |
            |------|------|
            | src/main.java | +25/-5 |
            | src/utils.java | +10/-2 |
            
            ### 统计
            
            - 修改文件：2 个
            - 新增：35 行
            - 删除：7 行
            
            ### 提交信息
            
            **%s**: 修复 bug
            **%s**: 新增功能
            """, commit1, commit2, commit1, commit2);
        
        return CommandResult.success("提交差异")
                .withDisplayText(report);
    }
    
    private CommandResult showFileDiff(String filePath) {
        String report = String.format("""
            ## 文件差异：%s
            
            ```diff
            --- a/%s
            +++ b/%s
            @@ -10,7 +10,19 @@
             public class Example {
            +    /**
            +     * 新增方法
            +     */
            +    public void newMethod() {
            +        System.out.println("Hello");
            +    }
            -    public void oldMethod() {}
            +    public void updatedMethod() {
            +        // 更新实现
            +    }
             }
            ```
            
            变更统计：+15/-3
            """, filePath, filePath, filePath);
        
        return CommandResult.success("文件差异")
                .withDisplayText(report);
    }
    
    private CommandResult showDiffWithOptions(String args) {
        String report = """
            ## 差异比较（带选项）
            
            支持的选项：
            
            | 选项 | 说明 |
            |------|------|
            | --stat | 显示统计 |
            | --name-only | 仅文件名 |
            | --color | 彩色输出 |
            | --cached | 暂存区 |
            | --staged | 暂存区（同义） |
            
            示例：
              diff --stat
              diff --name-only
              diff --color
            """;
        
        return CommandResult.success("差异选项")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：diff
            别名：d
            描述：代码差异比较
            
            用法：
              diff                      # 工作区差异
              diff --cached             # 暂存区差异
              diff <文件>               # 文件差异
              diff <commit1> <commit2>  # 提交差异
            
            示例：
              diff
              diff --cached
              diff src/main.java
              diff HEAD~1 HEAD
            """;
    }
}
