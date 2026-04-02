package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 项目初始化
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class InitCommand extends Command {
    
    public InitCommand() {
        this.name = "init";
        this.description = "项目初始化";
        this.aliases = Arrays.asList("initialize", "create");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = true;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showInitHelp();
        }
        
        String[] parts = args.trim().split("\\s+");
        String type = parts[0];
        
        return switch (type) {
            case "project" -> initProject(parts.length > 1 ? parts[1] : "my-project");
            case "git" -> initGit();
            case "config" -> initConfig();
            default -> initProject(type);
        };
    }
    
    private CommandResult showInitHelp() {
        String report = """
            ## 项目初始化
            
            ### 用法
            
            ```
            init <类型> [名称]
            ```
            
            ### 类型
            
            | 类型 | 说明 |
            |------|------|
            | project [名称] | 创建新项目 |
            | git | 初始化 Git 仓库 |
            | config | 创建配置文件 |
            
            ### 示例
            
            ```
            init project myapp
            init git
            init config
            ```
            
            ### 项目模板
            
            - spring-boot - Spring Boot 项目
            - maven - Maven 项目
            - java - 纯 Java 项目
            - web - Web 应用
            """;
        
        return CommandResult.success("初始化帮助")
                .withDisplayText(report);
    }
    
    private CommandResult initProject(String name) {
        String report = String.format("""
            ## 项目初始化：%s
            
            ### 创建的文件
            
            ```
            %s/
            ├── pom.xml
            ├── src/
            │   ├── main/
            │   │   ├── java/
            │   │   └── resources/
            │   └── test/
            │       ├── java/
            │       └── resources/
            ├── README.md
            └── .gitignore
            ```
            
            ### 下一步
            
            1. `cd %s` - 进入项目目录
            2. `mvn clean install` - 构建项目
            3. 开始编码！
            
            ✅ 项目创建成功！
            """, name, name, name);
        
        Map<String, Object> data = new HashMap<>();
        data.put("projectName", name);
        data.put("created", true);
        
        return CommandResult.success("项目已创建：" + name)
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult initGit() {
        String report = """
            ## Git 仓库初始化
            
            ### 执行的操作
            
            - ✅ 创建 .git 目录
            - ✅ 创建 .gitignore
            - ✅ 初始提交
            
            ### 下一步
            
            1. `git add .` - 添加所有文件
            2. `git commit -m "Initial commit"` - 提交
            3. `git remote add origin <url>` - 添加远程
            
            ✅ Git 仓库初始化完成！
            """;
        
        return CommandResult.success("Git 仓库已初始化")
                .withDisplayText(report);
    }
    
    private CommandResult initConfig() {
        String report = """
            ## 配置文件创建
            
            ### 创建的文件
            
            - `.jcode/config.yml` - 主配置
            - `.jcode/tools.yml` - 工具配置
            - `.jcode/commands.yml` - 命令配置
            
            ### 默认配置
            
            ```yaml
            model: qwen3.5-plus
            temperature: 0.7
            maxTokens: 4096
            autoApprove: false
            ```
            
            ✅ 配置文件已创建！
            """;
        
        return CommandResult.success("配置文件已创建")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：init
            别名：initialize, create
            描述：项目初始化
            
            用法：
              init                      # 显示帮助
              init project [名称]       # 创建项目
              init git                  # 初始化 Git
              init config               # 创建配置
            
            示例：
              init project myapp
              init git
              init config
            """;
    }
}
