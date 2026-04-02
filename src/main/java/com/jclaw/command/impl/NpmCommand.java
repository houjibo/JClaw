package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * NPM 命令 - Node 包管理
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class NpmCommand extends Command {
    
    public NpmCommand() {
        this.name = "npm";
        this.description = "Node 包管理";
        this.aliases = Arrays.asList("node", "yarn");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showNpmInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "install", "i" -> installPackage(parts.length > 1 ? parts[1] : null);
            case "uninstall", "un" -> uninstallPackage(parts.length > 1 ? parts[1] : null);
            case "run" -> runScript(parts.length > 1 ? parts[1] : null);
            case "build" -> buildProject();
            case "test" -> testProject();
            case "init" -> initProject();
            case "info" -> showNpmInfo();
            default -> showNpmInfo();
        };
    }
    
    private CommandResult showNpmInfo() {
        String report = """
            ## NPM 信息
            
            ### NPM 状态
            
            | 属性 | 值 |
            |------|------|
            | 状态 | ⚪ 未检测 |
            | Node 版本 | - |
            | NPM 版本 | - |
            | 项目 | - |
            
            ### 快速命令
            
            ```bash
            # 查看版本
            node --version
            npm --version
            
            # 安装包
            npm install <包名>
            
            # 运行脚本
            npm run <脚本名>
            
            # 构建项目
            npm run build
            ```
            
            ### JClaw NPM 命令
            
            | 命令 | 说明 |
            |------|------|
            | npm install <包> | 安装包 |
            | npm run <脚本> | 运行脚本 |
            | npm build | 构建项目 |
            | npm test | 运行测试 |
            
            ⚠️ 需要安装 Node.js 才能使用
            """;
        
        return CommandResult.success("NPM 信息")
                .withDisplayText(report);
    }
    
    private CommandResult installPackage(String pkg) {
        if (pkg == null) {
            return CommandResult.error("请指定包名称");
        }
        
        String report = String.format("""
            ## 安装 NPM 包
            
            **包**: %s
            
            ### 执行命令
            
            ```bash
            npm install %s
            npm install %s --save-dev    # 开发依赖
            npm install %s -g            # 全局安装
            ```
            
            ### 常用包
            
            | 包名 | 说明 |
            |------|------|
            | express | Web 框架 |
            | react | UI 库 |
            | vue | UI 库 |
            | typescript | TypeScript |
            | eslint | 代码检查 |
            
            ⚠️ 需要 Node.js 环境
            """, pkg, pkg, pkg, pkg);
        
        return CommandResult.success("安装包：" + pkg)
                .withDisplayText(report);
    }
    
    private CommandResult uninstallPackage(String pkg) {
        if (pkg == null) {
            return CommandResult.error("请指定包名称");
        }
        
        String report = String.format("""
            ## 卸载 NPM 包
            
            **包**: %s
            
            ### 执行命令
            
            ```bash
            npm uninstall %s
            npm uninstall %s --save-dev
            ```
            
            ⚠️ 需要 Node.js 环境
            """, pkg, pkg);
        
        return CommandResult.success("卸载包：" + pkg)
                .withDisplayText(report);
    }
    
    private CommandResult runScript(String script) {
        if (script == null) {
            return CommandResult.error("请指定脚本名称");
        }
        
        String report = String.format("""
            ## 运行 NPM 脚本
            
            **脚本**: %s
            
            ### 执行命令
            
            ```bash
            npm run %s
            ```
            
            ### 常见脚本
            
            | 脚本 | 说明 |
            |------|------|
            | start | 启动应用 |
            | build | 构建项目 |
            | test | 运行测试 |
            | dev | 开发模式 |
            | lint | 代码检查 |
            
            ⚠️ 需要在 package.json 中定义脚本
            """, script, script);
        
        return CommandResult.success("运行脚本：" + script)
                .withDisplayText(report);
    }
    
    private CommandResult buildProject() {
        String report = """
            ## 构建项目
            
            ### 执行命令
            
            ```bash
            npm run build
            ```
            
            ### 构建流程
            
            1. 清理输出目录
            2. 编译 TypeScript/JSX
            3. 打包资源文件
            4. 优化代码
            5. 生成构建产物
            
            ### 常见构建工具
            
            | 工具 | 说明 |
            |------|------|
            | Webpack | 模块打包 |
            | Vite | 快速构建 |
            | Rollup | 库打包 |
            | Parcel | 零配置 |
            
            ⚠️ 需要配置 build 脚本
            """;
        
        return CommandResult.success("构建项目")
                .withDisplayText(report);
    }
    
    private CommandResult testProject() {
        String report = """
            ## 运行测试
            
            ### 执行命令
            
            ```bash
            npm test
            npm run test
            npm run test -- --coverage    # 覆盖率
            ```
            
            ### 常见测试框架
            
            | 框架 | 说明 |
            |------|------|
            | Jest | 全能测试 |
            | Mocha | 灵活测试 |
            | Vitest | Vite 测试 |
            | Cypress | E2E 测试 |
            
            ⚠️ 需要配置 test 脚本
            """;
        
        return CommandResult.success("运行测试")
                .withDisplayText(report);
    }
    
    private CommandResult initProject() {
        String report = """
            ## 初始化项目
            
            ### 执行命令
            
            ```bash
            npm init          # 交互式初始化
            npm init -y       # 快速初始化
            ```
            
            ### 生成的 package.json
            
            ```json
            {
              "name": "my-project",
              "version": "1.0.0",
              "description": "",
              "main": "index.js",
              "scripts": {
                "test": "echo \"Error\" && exit 1"
              },
              "keywords": [],
              "author": "",
              "license": "ISC"
            }
            ```
            
            ### 现代项目模板
            
            ```bash
            # React
            npx create-react-app my-app
            
            # Vue
            npm create vue@latest
            
            # Next.js
            npx create-next-app@latest
            
            # Vite
            npm create vite@latest
            ```
            """;
        
        return CommandResult.success("初始化项目")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：npm
            别名：node, yarn
            描述：Node 包管理
            
            用法：
              npm                     # NPM 信息
              npm install <包>        # 安装包
              npm run <脚本>          # 运行脚本
              npm build               # 构建项目
              npm test                # 运行测试
            
            示例：
              npm install express
              npm run build
              npm test
            """;
    }
}
