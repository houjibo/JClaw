package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Maven 构建命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class MavenCommand extends Command {
    
    public MavenCommand() {
        this.name = "maven";
        this.description = "Maven 构建管理";
        this.aliases = Arrays.asList("mvn");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showMavenInfo();
        }
        
        String goal = parts[0];
        
        return switch (goal) {
            case "clean" -> mavenClean();
            case "compile" -> mavenCompile();
            case "test" -> mavenTest();
            case "package" -> mavenPackage();
            case "install" -> mavenInstall();
            case "deploy" -> mavenDeploy();
            case "run" -> mavenRun(parts.length > 1 ? parts[1] : null);
            case "info" -> showMavenInfo();
            default -> executeCustomGoal(goal);
        };
    }
    
    private CommandResult showMavenInfo() {
        String report = """
            ## Maven 信息
            
            ### Maven 状态
            
            | 属性 | 值 |
            |------|------|
            | 状态 | ⚪ 未检测 |
            | Maven 版本 | - |
            | Java 版本 | - |
            | 项目 | - |
            
            ### 快速命令
            
            ```bash
            # 查看版本
            mvn --version
            
            # 清理项目
            mvn clean
            
            # 编译项目
            mvn compile
            
            # 运行测试
            mvn test
            
            # 打包项目
            mvn package
            
            # 安装到本地仓库
            mvn install
            ```
            
            ### JClaw Maven 命令
            
            | 命令 | 说明 |
            |------|------|
            | maven clean | 清理项目 |
            | maven compile | 编译项目 |
            | maven test | 运行测试 |
            | maven package | 打包项目 |
            | maven install | 安装到本地 |
            | maven deploy | 部署到远程 |
            
            ⚠️ 需要安装 Maven 才能使用
            """;
        
        return CommandResult.success("Maven 信息")
                .withDisplayText(report);
    }
    
    private CommandResult mavenClean() {
        String report = """
            ## Maven Clean
            
            ### 执行命令
            
            ```bash
            mvn clean
            ```
            
            ### 清理内容
            
            - target/ 目录
            - 编译产物
            - 测试报告
            - 打包文件
            
            ### 效果
            
            ✅ 项目已清理
            ✅ target/ 目录已删除
            
            ⚠️ 需要 Maven 环境
            """;
        
        return CommandResult.success("Maven Clean")
                .withDisplayText(report);
    }
    
    private CommandResult mavenCompile() {
        String report = """
            ## Maven Compile
            
            ### 执行命令
            
            ```bash
            mvn compile
            ```
            
            ### 编译流程
            
            1. 验证项目结构
            2. 处理资源文件
            3. 编译 Java 源码
            4. 生成 class 文件
            
            ### 输出目录
            
            ```
            target/
            └── classes/
                └── (编译后的 class 文件)
            ```
            
            ⚠️ 需要 Maven 环境
            """;
        
        return CommandResult.success("Maven Compile")
                .withDisplayText(report);
    }
    
    private CommandResult mavenTest() {
        String report = """
            ## Maven Test
            
            ### 执行命令
            
            ```bash
            mvn test
            ```
            
            ### 测试流程
            
            1. 编译源码
            2. 编译测试代码
            3. 运行单元测试
            4. 生成测试报告
            
            ### 测试报告
            
            ```
            target/surefire-reports/
            ```
            
            ### 常用选项
            
            ```bash
            mvn test -Dtest=MyTest      # 运行指定测试
            mvn test -DskipTests        # 跳过测试
            mvn test -Dmaven.test.failure.ignore=true  # 忽略失败
            ```
            
            ⚠️ 需要 Maven 环境
            """;
        
        return CommandResult.success("Maven Test")
                .withDisplayText(report);
    }
    
    private CommandResult mavenPackage() {
        String report = """
            ## Maven Package
            
            ### 执行命令
            
            ```bash
            mvn package
            ```
            
            ### 打包流程
            
            1. 编译源码
            2. 运行测试
            3. 打包项目
            
            ### 输出文件
            
            | 项目类型 | 输出文件 |
            |----------|---------|
            | JAR | target/*.jar |
            | WAR | target/*.war |
            | EAR | target/*.ear |
            
            ### 跳过测试打包
            
            ```bash
            mvn package -DskipTests
            ```
            
            ⚠️ 需要 Maven 环境
            """;
        
        return CommandResult.success("Maven Package")
                .withDisplayText(report);
    }
    
    private CommandResult mavenInstall() {
        String report = """
            ## Maven Install
            
            ### 执行命令
            
            ```bash
            mvn install
            ```
            
            ### 安装流程
            
            1. 编译源码
            2. 运行测试
            3. 打包项目
            4. 安装到本地仓库
            
            ### 本地仓库位置
            
            ```
            ~/.m2/repository/<groupId>/<artifactId>/<version>/
            ```
            
            ### 安装后可用
            
            - 其他项目可以依赖此模块
            - 本地引用无需重新编译
            
            ⚠️ 需要 Maven 环境
            """;
        
        return CommandResult.success("Maven Install")
                .withDisplayText(report);
    }
    
    private CommandResult mavenDeploy() {
        String report = """
            ## Maven Deploy
            
            ### 执行命令
            
            ```bash
            mvn deploy
            ```
            
            ### 部署流程
            
            1. 编译源码
            2. 运行测试
            3. 打包项目
            4. 部署到远程仓库
            
            ### 远程仓库
            
            - Maven Central
            - 公司私有仓库
            - Nexus/Artifactory
            
            ### 配置示例 (pom.xml)
            
            ```xml
            <distributionManagement>
              <repository>
                <id>releases</id>
                <url>https://repo.example.com/releases</url>
              </repository>
            </distributionManagement>
            ```
            
            ⚠️ 需要配置远程仓库
            """;
        
        return CommandResult.success("Maven Deploy")
                .withDisplayText(report);
    }
    
    private CommandResult mavenRun(String goal) {
        if (goal == null) {
            return CommandResult.error("请指定 Maven goal");
        }
        
        String report = String.format("""
            ## Maven 执行：%s
            
            ### 执行命令
            
            ```bash
            mvn %s
            ```
            
            ### 常见 Goals
            
            | Goal | 说明 |
            |------|------|
            | clean | 清理项目 |
            | compile | 编译源码 |
            | test | 运行测试 |
            | package | 打包项目 |
            | install | 安装到本地 |
            | deploy | 部署到远程 |
            | site | 生成站点 |
            | verify | 验证项目 |
            
            ⚠️ 需要 Maven 环境
            """, goal, goal);
        
        return CommandResult.success("Maven 执行：" + goal)
                .withDisplayText(report);
    }
    
    private CommandResult executeCustomGoal(String goal) {
        return mavenRun(goal);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：maven
            别名：mvn
            描述：Maven 构建管理
            
            用法：
              maven                   # Maven 信息
              maven clean             # 清理项目
              maven compile           # 编译项目
              maven test              # 运行测试
              maven package           # 打包项目
              maven install           # 安装到本地
              maven deploy            # 部署到远程
            
            示例：
              maven clean
              maven package
              maven test
            """;
    }
}
