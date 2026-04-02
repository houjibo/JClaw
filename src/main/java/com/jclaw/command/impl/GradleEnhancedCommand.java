package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Gradle 增强命令 - 补充功能
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class GradleEnhancedCommand extends Command {
    
    public GradleEnhancedCommand() {
        this.name = "gradle-enhanced";
        this.description = "Gradle 增强功能";
        this.aliases = Arrays.asList("gradle-ext");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showGradleEnhancedInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "wrapper" -> gradleWrapper();
            case "projects" -> gradleProjects();
            case "dependencies" -> gradleDependenciesEnhanced();
            case "buildScan" -> gradleBuildScan();
            default -> showGradleEnhancedInfo();
        };
    }
    
    private CommandResult showGradleEnhancedInfo() {
        String report = """
            ## Gradle 增强功能
            
            ### 可用命令
            
            | 命令 | 说明 |
            |------|------|
            | gradle-enhanced wrapper | 生成包装器 |
            | gradle-enhanced projects | 查看项目 |
            | gradle-enhanced dependencies | 依赖分析 |
            | gradle-enhanced buildScan | 构建扫描 |
            
            ### Gradle Wrapper
            
            Gradle Wrapper 确保使用正确的 Gradle 版本。
            
            ```bash
            gradle wrapper
            ./gradlew build
            ```
            
            ### 项目结构
            
            ```bash
            gradle projects
            ```
            
            ### 依赖分析
            
            ```bash
            gradle dependencies
            gradle dependencyInsight --dependency <name>
            ```
            
            ### 构建扫描
            
            ```bash
            gradle build --scan
            ```
            
            这会生成一个可共享的构建报告 URL。
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle 增强功能")
                .withDisplayText(report);
    }
    
    private CommandResult gradleWrapper() {
        String report = """
            ## Gradle Wrapper
            
            ### 生成包装器
            
            ```bash
            gradle wrapper
            ```
            
            ### 生成的文件
            
            ```
            gradlew          # Linux/Mac 脚本
            gradlew.bat      # Windows 脚本
            gradle/wrapper/  # 包装器配置
            ```
            
            ### 使用包装器
            
            ```bash
            ./gradlew build
            ./gradlew test
            ./gradlew clean
            ```
            
            ### 优点
            
            - ✅ 确保团队使用相同版本
            - ✅ 无需手动安装 Gradle
            - ✅ 自动下载所需版本
            - ✅ CI/CD 友好
            
            ### 配置版本 (gradle/wrapper/gradle-wrapper.properties)
            
            ```properties
            distributionUrl=https://services.gradle.org/distributions/gradle-8.5-bin.zip
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Wrapper")
                .withDisplayText(report);
    }
    
    private CommandResult gradleProjects() {
        String report = """
            ## Gradle 项目结构
            
            ### 查看项目
            
            ```bash
            gradle projects
            ```
            
            ### 示例输出
            
            ```
            Root project 'myapp'
            +--- Project ':api' - API 模块
            +--- Project ':core' - 核心模块
            +--- Project ':web' - Web 模块
            \\--- Project ':app' - 应用模块
            ```
            
            ### 多项目构建
            
            **settings.gradle**:
            ```groovy
            rootProject.name = 'myapp'
            include 'api', 'core', 'web', 'app'
            ```
            
            ### 构建特定项目
            
            ```bash
            gradle :api:build
            gradle :core:test
            ```
            
            ### 任务依赖
            
            ```groovy
            project(':app').tasks.build.dependsOn project(':api').tasks.build
            ```
            
            ⚠️ 需要多项目配置
            """;
        
        return CommandResult.success("Gradle 项目")
                .withDisplayText(report);
    }
    
    private CommandResult gradleDependenciesEnhanced() {
        String report = """
            ## Gradle 依赖分析
            
            ### 查看依赖树
            
            ```bash
            gradle dependencies
            gradle :app:dependencies
            ```
            
            ### 依赖洞察
            
            ```bash
            gradle dependencyInsight --dependency spring-boot
            ```
            
            ### 配置依赖
            
            | 配置 | 说明 |
            |------|------|
            | implementation | 实现依赖 |
            | testImplementation | 测试依赖 |
            | runtimeOnly | 运行时依赖 |
            | compileOnly | 编译时依赖 |
            | annotationProcessor | 注解处理器 |
            
            ### 示例配置
            
            ```groovy
            dependencies {
                implementation 'org.springframework.boot:spring-boot-starter'
                testImplementation 'org.junit.jupiter:junit-jupiter'
                runtimeOnly 'com.mysql:mysql-connector-j'
                compileOnly 'org.projectlombok:lombok'
                annotationProcessor 'org.projectlombok:lombok'
            }
            ```
            
            ### 解决冲突
            
            ```groovy
            configurations.all {
                resolutionStrategy {
                    force 'com.google.guava:guava:32.0.0-jre'
                }
            }
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle 依赖")
                .withDisplayText(report);
    }
    
    private CommandResult gradleBuildScan() {
        String report = """
            ## Gradle Build Scan
            
            ### 启用构建扫描
            
            ```bash
            gradle build --scan
            ```
            
            ### 配置插件 (settings.gradle)
            
            ```groovy
            plugins {
                id 'com.gradle.enterprise' version '3.15'
            }
            
            gradleEnterprise {
                buildScan {
                    termsOfServiceUrl = 'https://gradle.com/terms-of-service'
                    termsOfServiceAgree = 'yes'
                }
            }
            ```
            
            ### 输出
            
            ```
            Build scan URL: https://scans.gradle.com/s/abcdef123456
            ```
            
            ### 功能
            
            - 📊 构建时间分析
            - 📈 任务执行时间
            - 🔍 依赖解析详情
            - 🐌 慢任务识别
            - 📤 可共享的 URL
            
            ### 好处
            
            - 优化构建性能
            - 诊断构建问题
            - 团队共享构建信息
            - 持续改进
            
            ⚠️ 需要网络连接
            """;
        
        return CommandResult.success("Gradle Build Scan")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：gradle-enhanced
            别名：gradle-ext
            描述：Gradle 增强功能
            
            用法：
              gradle-enhanced                 # 显示信息
              gradle-enhanced wrapper         # 生成包装器
              gradle-enhanced projects        # 查看项目
              gradle-enhanced dependencies    # 依赖分析
              gradle-enhanced buildScan       # 构建扫描
            
            示例：
              gradle-enhanced wrapper
              gradle-enhanced projects
            """;
    }
}
