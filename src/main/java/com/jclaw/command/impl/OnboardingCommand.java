package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Onboarding 命令 - 新手引导
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class OnboardingCommand extends Command {
    
    public OnboardingCommand() {
        this.name = "onboarding";
        this.description = "新手引导";
        this.aliases = Arrays.asList("guide", "tutorial", "help");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showOnboarding();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "start" -> startOnboarding();
            case "skip" -> skipOnboarding();
            case "step" -> showStep(parts.length > 1 ? parts[1] : null);
            default -> showOnboarding();
        };
    }
    
    private CommandResult showOnboarding() {
        String report = """
            ## JClaw 新手引导
            
            👋 欢迎使用 JClaw！
            
            这是一个交互式引导，帮助你快速上手 JClaw。
            
            ### 引导内容
            
            | 步骤 | 内容 | 预计时间 |
            |------|------|---------|
            | 1 | 认识 JClaw | 2 分钟 |
            | 2 | 基本命令 | 5 分钟 |
            | 3 | 工具使用 | 5 分钟 |
            | 4 | 实战练习 | 10 分钟 |
            
            ### 开始引导
            
            ```
            onboarding start    # 开始引导
            onboarding step 1   # 查看步骤 1
            onboarding skip     # 跳过引导
            ```
            
            ### 快速开始
            
            如果不想看引导，可以直接尝试：
            
            ```
            help                # 查看所有命令
            files               # 查看文件
            git status          # 查看 Git 状态
            ```
            
            ### 获取帮助
            
            - `help` - 查看所有命令
            - `help <命令>` - 查看命令帮助
            - `onboarding` - 重新显示此引导
            
            准备好开始了吗？输入 `onboarding start` 开始引导！
            """;
        
        return CommandResult.success("新手引导")
                .withDisplayText(report);
    }
    
    private CommandResult startOnboarding() {
        String report = """
            ## 新手引导 - 开始
            
            ### 步骤 1/4: 认识 JClaw
            
            
            #### 核心能力
            
            1. **命令系统** - 72 个实用命令
            2. **工具系统** - 46 个专业工具
            3. **Agent 系统** - 多 Agent 协作
            4. **插件系统** - 可扩展架构
            
            #### 快速体验
            
            试试这些命令：
            
            ```
            files               # 查看当前目录文件
            status              # 查看系统状态
            help                # 查看所有命令
            ```
            
            #### 下一步
            
            输入 `onboarding step 2` 继续学习基本命令。
            
            或者随时输入 `onboarding` 返回主菜单。
            
            ---
            进度：1/4 (25%)
            """;
        
        return CommandResult.success("引导步骤 1")
                .withDisplayText(report);
    }
    
    private CommandResult showStep(String step) {
        if (step == null) {
            return showOnboarding();
        }
        
        String report = switch (step) {
            case "1" -> """
                ## 步骤 1/4: 认识 JClaw
                
                **JClaw** 是一个基于 Java 的 AI 编码助手。
                
                ### 核心特性
                
                - 📦 72 个实用命令
                - 🛠️ 46 个专业工具
                - 🤖 多 Agent 协作
                - 🔌 可扩展架构
                
                ### 试试这些命令
                
                ```
                files
                status
                help
                ```
                
                输入 `onboarding step 2` 继续。
                """;
            case "2" -> """
                ## 步骤 2/4: 基本命令
                
                ### 文件操作
                
                ```
                files                   # 查看文件
                cat <文件>              # 查看内容
                find <模式>             # 查找文件
                ```
                
                ### Git 操作
                
                ```
                git status              # 查看状态
                git log                 # 查看历史
                git diff                # 查看差异
                ```
                
                ### 系统命令
                
                ```
                status                  # 系统状态
                process                 # 进程信息
                disk                    # 磁盘使用
                ```
                
                输入 `onboarding step 3` 继续。
                """;
            case "3" -> """
                ## 步骤 3/4: 工具使用
                
                ### HTTP 工具
                
                ```
                curl https://api.example.com
                http -X POST https://api.example.com -d '{"key":"value"}'
                ```
                
                ### 构建工具
                
                ```
                maven compile           # Maven 编译
                gradle build            # Gradle 构建
                npm install             # NPM 安装
                ```
                
                ### 数据库工具
                
                ```
                db connect <URL>        # 连接数据库
                db query <SQL>          # 执行查询
                ```
                
                输入 `onboarding step 4` 继续。
                """;
            case "4" -> """
                ## 步骤 4/4: 实战练习
                
                ### 练习任务
                
                1. 创建一个新项目
                   ```
                   files create my-project
                   cd my-project
                   ```
                
                2. 初始化 Git 仓库
                   ```
                   git init
                   git add .
                   git commit -m "Initial commit"
                   ```
                
                3. 查看项目状态
                   ```
                   status
                   git status
                   ```
                
                ### 恭喜你！
                
                🎉 已完成新手引导！
                
                现在你可以：
                - 使用 `help` 查看所有命令
                - 使用 `help <命令>` 查看帮助
                - 开始你的第一个项目！
                
                ### 下一步
                
                - 查看文档：`docs`
                - 查看示例：`examples`
                - 开始编码！
                
                ---
                进度：4/4 (100%) ✅
                """;
            default -> "无效的步骤编号，请输入 1-4";
        };
        
        return CommandResult.success("引导步骤 " + step)
                .withDisplayText(report);
    }
    
    private CommandResult skipOnboarding() {
        return CommandResult.success("已跳过引导")
                .withDisplayText("""
                    ## 已跳过新手引导
                    
                    你可以随时输入 `onboarding` 重新查看引导。
                    
                    ### 快速开始
                    
                    ```
                    help                # 查看所有命令
                    files               # 查看文件
                    status              # 查看状态
                    ```
                    
                    ### 获取帮助
                    
                    - `help` - 查看所有命令
                    - `help <命令>` - 查看命令帮助
                    
                    祝你使用愉快！
                    """);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：onboarding
            别名：guide, tutorial, help
            描述：新手引导
            
            用法：
              onboarding                # 显示引导
              onboarding start          # 开始引导
              onboarding step <N>       # 查看步骤
              onboarding skip           # 跳过引导
            
            示例：
              onboarding
              onboarding start
              onboarding step 1
            """;
    }
}
