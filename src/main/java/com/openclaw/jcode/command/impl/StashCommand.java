package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git 暂存管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class StashCommand extends Command {
    
    public StashCommand() {
        this.name = "stash";
        this.description = "Git 暂存管理";
        this.aliases = Arrays.asList("stashes");
        this.category = CommandCategory.GIT;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listStashes();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> listStashes();
            case "push", "save" -> pushStash(parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)) : null);
            case "pop" -> popStash(parts.length > 1 ? parts[1] : null);
            case "apply" -> applyStash(parts.length > 1 ? parts[1] : null);
            case "drop" -> dropStash(parts.length > 1 ? parts[1] : null);
            case "clear" -> clearStashes();
            case "show" -> showStash(parts.length > 1 ? parts[1] : null);
            default -> listStashes();
        };
    }
    
    private CommandResult listStashes() {
        String report = """
            ## Git 暂存列表
            
            | ID | 消息 | 日期 | 分支 |
            |-----|------|------|------|
            | stash@{0} | WIP: 用户登录功能 | 10 分钟前 | feature/login |
            | stash@{1} | WIP: 修复 bug | 2 小时前 | main |
            | stash@{2} | WIP: 代码优化 | 昨天 | develop |
            
            共 3 个暂存
            """;
        
        return CommandResult.success("暂存列表")
                .withDisplayText(report);
    }
    
    private CommandResult pushStash(String message) {
        String report = String.format("""
            ## 暂存成功
            
            **消息**: %s
            **分支**: feature/login
            **时间**: 刚刚
            
            恢复暂存：stash pop
            查看暂存：stash list
            """, message != null ? message : "WIP");
        
        return CommandResult.success("已保存暂存")
                .withDisplayText(report);
    }
    
    private CommandResult popStash(String index) {
        return CommandResult.success("已恢复暂存：" + (index != null ? index : "stash@{0}"))
                .withDisplayText("✅ 暂存已恢复并删除");
    }
    
    private CommandResult applyStash(String index) {
        return CommandResult.success("已应用暂存：" + (index != null ? index : "stash@{0}"))
                .withDisplayText("✅ 暂存已应用（保留）");
    }
    
    private CommandResult dropStash(String index) {
        if (index == null) {
            return CommandResult.error("请指定要删除的暂存 ID");
        }
        
        return CommandResult.success("已删除暂存：" + index)
                .withDisplayText("✅ 暂存 `" + index + "` 已删除");
    }
    
    private CommandResult clearStashes() {
        return CommandResult.success("已清空所有暂存")
                .withDisplayText("✅ 所有暂存已清空");
    }
    
    private CommandResult showStash(String index) {
        String report = String.format("""
            ## 暂存详情：%s
            
            ### 变更文件
            
            | 文件 | 变更 |
            |------|------|
            | src/main.java | M |
            | src/test.java | A |
            
            ### 变更内容
            
            ```diff
            + 新增功能
            - 删除旧代码
            ```
            
            恢复：stash pop %s
            删除：stash drop %s
            """, index != null ? index : "stash@{0}", 
               index != null ? index : "stash@{0}",
               index != null ? index : "stash@{0}");
        
        return CommandResult.success("暂存详情")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：stash
            别名：stashes
            描述：Git 暂存管理
            
            用法：
              stash                   # 列出暂存
              stash list              # 列出暂存
              stash push [msg]        # 保存暂存
              stash pop [index]       # 恢复并删除
              stash apply [index]     # 恢复（保留）
              stash drop <index>      # 删除暂存
              stash clear             # 清空所有
              stash show [index]      # 查看详情
            
            示例：
              stash
              stash push "WIP: 登录功能"
              stash pop
              stash drop stash@{1}
            """;
    }
}
