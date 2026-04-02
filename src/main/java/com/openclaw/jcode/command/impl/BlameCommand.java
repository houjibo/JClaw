package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Git Blame 命令 - 代码溯源
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class BlameCommand extends Command {
    
    public BlameCommand() {
        this.name = "blame";
        this.description = "Git 代码溯源";
        this.aliases = Arrays.asList("annotate");
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
        
        String file = parts[parts.length - 1];
        String range = parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1)) : null;
        
        return blame(file, range);
    }
    
    private CommandResult blame(String file, String range) {
        if (file == null) {
            return CommandResult.error("请指定文件路径");
        }
        
        String report = String.format("""
            ## Git Blame - 代码溯源
            
            **文件**: %s
            **范围**: %s
            
            ### 示例输出
            
            ```
            ^abc1234 (Alice 2026-03-01 10:00:00 +0800 1) package com.example;
            abc12345 (Bob   2026-03-02 15:30:00 +0800 2) 
            abc12345 (Bob   2026-03-02 15:30:00 +0800 3) public class Main {
            def67890 (Carol 2026-03-03 09:15:00 +0800 4)     public static void main(String[] args) {
            def67890 (Carol 2026-03-03 09:15:00 +0800 5)         System.out.println("Hello");
            def67890 (Carol 2026-03-03 09:15:00 +0800 6)     }
            abc12345 (Bob   2026-03-02 15:30:00 +0800 7) }
            ```
            
            ### 输出说明
            
            | 列 | 说明 |
            |----|------|
            | 1 | 提交哈希 |
            | 2 | 作者 |
            | 3 | 日期 |
            | 4 | 行号 |
            | 5 | 代码内容 |
            
            ### 常用选项
            
            | 选项 | 说明 |
            |------|------|
            | -L <start>,<end> | 只看指定行范围 |
            | -C | 查找代码移动 |
            | -M | 查找代码复制 |
            | -w | 忽略空白字符 |
            | --line-porcelain | 机器可读格式 |
            
            ### 常见场景
            
            1. **查看某行是谁写的**
               ```
               blame -L 42,42 src/main.java
               ```
            
            2. **查看最近修改**
               ```
               blame --since=2.weeks src/main.java
               ```
            
            3. **忽略空白字符变更**
               ```
               blame -w src/main.java
               ```
            
            ### 执行命令
            
            ```bash
            git blame %s %s
            ```
            """, file, range != null ? range : "全部", range != null ? range : "", file);
        
        return CommandResult.success("Blame 操作：" + file)
                .withDisplayText(report);
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Git Blame 帮助
            
            ### 用法
            
            ```
            blame <文件>              # 查看文件溯源
            blame -L <start>,<end>    # 查看指定行
            blame -C <文件>           # 查找代码移动
            ```
            
            ### 常见场景
            
            1. **查看代码作者**
               ```
               blame src/main.java
               ```
            
            2. **查看特定行**
               ```
               blame -L 10,20 src/main.java
               ```
            
            3. **查找代码来源**
               ```
               blame -C -C src/main.java
               ```
            
            ### 输出解读
            
            ```
            abc12345 (作者名 日期 时间 行号) 代码内容
            ```
            
            ### 相关命令
            
            - `git log` - 查看提交历史
            - `git log -p` - 查看变更内容
            - `git show` - 查看提交详情
            """;
        
        return CommandResult.success("Blame 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：blame
            别名：annotate
            描述：Git 代码溯源
            
            用法：
              blame <文件>            # 查看文件溯源
              blame -L <start>,<end>  # 查看指定行
            
            示例：
              blame src/main.java
              blame -L 10,20 src/main.java
            """;
    }
}
