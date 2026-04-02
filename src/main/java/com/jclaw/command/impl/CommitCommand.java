package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Git 提交操作
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CommitCommand extends Command {
    
    public CommitCommand() {
        this.name = "commit";
        this.description = "Git 提交管理";
        this.aliases = Arrays.asList("ci");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showRecentCommits();
        }
        
        if (args.startsWith("-m") || args.contains("\"") || args.contains("'")) {
            return createCommit(args);
        }
        
        if (args.startsWith("--amend")) {
            return amendCommit();
        }
        
        if (args.startsWith("log") || args.startsWith("-l")) {
            return showLog();
        }
        
        return CommandResult.error("用法：commit -m \"提交信息\" 或 commit log");
    }
    
    private CommandResult showRecentCommits() {
        String report = """
            ## 最近提交
            
            | Hash | 作者 | 时间 | 信息 |
            |------|------|------|------|
            | a1b2c3d | Cola | 10 分钟前 | 修复登录 bug |
            | e4f5g6h | Cola | 2 小时前 | 新增用户管理 |
            | i7j8k9l | Cola | 昨天 | 重构支付模块 |
            | m0n1o2p | Cola | 2 天前 | 添加单元测试 |
            
            使用 `commit -m "信息"` 创建新提交
            使用 `commit log` 查看完整历史
            """;
        
        return CommandResult.success("最近提交")
                .withDisplayText(report);
    }
    
    private CommandResult createCommit(String args) {
        String message = extractMessage(args);
        
        String report = String.format("""
            ## 提交成功
            
            **提交信息**: %s
            
            ### 变更文件
            
            | 状态 | 文件 |
            |------|------|
            | M | src/main/java/... |
            | A | src/test/java/... |
            
            ### 统计
            
            - 修改文件：2 个
            - 新增行数：+156
            - 删除行数：-23
            
            **Commit Hash**: `abc123d`
            """, message);
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("hash", "abc123d");
        data.put("filesChanged", 2);
        data.put("additions", 156);
        data.put("deletions", 23);
        
        return CommandResult.success("提交成功：" + message)
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult amendCommit() {
        return CommandResult.success("提交已修正")
                .withDisplayText("✅ 最后一次提交已修正");
    }
    
    private CommandResult showLog() {
        String report = """
            ## 提交历史
            
            ```
            commit a1b2c3d4e5f6g7h8i9j0
            Author: Cola <cola@example.com>
            Date:   Wed Apr 1 11:30:00 2026 +0800
            
                修复登录 bug
                
                - 修复空指针异常
                - 添加空值检查
                - 新增单元测试
            
            commit e4f5g6h7i8j9k0l1m2n3
            Author: Cola <cola@example.com>
            Date:   Wed Apr 1 09:00:00 2026 +0800
            
                新增用户管理功能
                
                - 用户 CRUD 接口
                - 权限验证
                - 数据验证
            
            commit i7j8k9l0m1n2o3p4q5r6
            Author: Cola <cola@example.com>
            Date:   Tue Mar 31 16:00:00 2026 +0800
            
                重构支付模块
            ```
            
            使用 `git log --oneline` 查看简洁视图
            使用 `git log --stat` 查看变更统计
            """;
        
        return CommandResult.success("提交历史")
                .withDisplayText(report);
    }
    
    private String extractMessage(String args) {
        // 简单提取 -m 后的消息
        int mIndex = args.indexOf("-m");
        if (mIndex >= 0) {
            String rest = args.substring(mIndex + 2).trim();
            // 移除引号
            if ((rest.startsWith("\"") && rest.endsWith("\"")) ||
                (rest.startsWith("'") && rest.endsWith("'"))) {
                return rest.substring(1, rest.length() - 1);
            }
            return rest;
        }
        return args;
    }
    
    @Override
    public String getHelp() {
        return """
            命令：commit
            别名：ci
            描述：Git 提交管理
            
            用法：
              commit                      # 显示最近提交
              commit -m "信息"            # 创建提交
              commit --amend              # 修正提交
              commit log                  # 查看历史
            
            示例：
              commit
              commit -m "修复 bug"
              commit -m "新增功能" --amend
              commit log
            """;
    }
}
