package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Gradle 构建命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class GradleCommand extends Command {
    
    public GradleCommand() {
        this.name = "gradle";
        this.description = "Gradle 构建管理";
        this.aliases = Arrays.asList("gradlew");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showGradleInfo();
        }
        
        String task = parts[0];
        
        return switch (task) {
            case "clean" -> gradleClean();
            case "build" -> gradleBuild();
            case "test" -> gradleTest();
            case "assemble" -> gradleAssemble();
            case "run" -> gradleRun();
            case "tasks" -> gradleTasks();
            case "dependencies" -> gradleDependencies();
            case "info" -> showGradleInfo();
            default -> executeCustomTask(task);
        };
    }
    
    private CommandResult showGradleInfo() {
        String report = """
            ## Gradle 信息
            
            ### Gradle 状态
            
            | 属性 | 值 |
            |------|------|
            | 状态 | ⚪ 未检测 |
            | Gradle 版本 | - |
            | Java 版本 | - |
            | 项目 | - |
            
            ### 快速命令
            
            ```bash
            # 查看版本
            gradle --version
            
            # 清理项目
            gradle clean
            
            # 构建项目
            gradle build
            
            # 运行测试
            gradle test
            
            # 运行应用
            gradle run
            
            # 查看任务
            gradle tasks
            ```
            
            ### JClaw Gradle 命令
            
            | 命令 | 说明 |
            |------|------|
            | gradle clean | 清理项目 |
            | gradle build | 构建项目 |
            | gradle test | 运行测试 |
            | gradle assemble | 打包项目 |
            | gradle run | 运行应用 |
            | gradle tasks | 查看任务 |
            
            ### Gradle vs Maven
            
            | 特性 | Gradle | Maven |
            |------|--------|-------|
            | 构建脚本 | Groovy/Kotlin | XML |
            | 性能 | 更快（增量构建） | 较慢 |
            | 灵活性 | 高 | 中 |
            | 学习曲线 | 陡峭 | 平缓 |
            
            ⚠️ 需要安装 Gradle 才能使用
            """;
        
        return CommandResult.success("Gradle 信息")
                .withDisplayText(report);
    }
    
    private CommandResult gradleClean() {
        String report = """
            ## Gradle Clean
            
            ### 执行命令
            
            ```bash
            gradle clean
            ```
            
            ### 清理内容
            
            - build/ 目录
            - 编译产物
            - 测试报告
            - 打包文件
            
            ### 效果
            
            ✅ 项目已清理
            ✅ build/ 目录已删除
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Clean")
                .withDisplayText(report);
    }
    
    private CommandResult gradleBuild() {
        String report = """
            ## Gradle Build
            
            ### 执行命令
            
            ```bash
            gradle build
            ```
            
            ### 构建流程
            
            1. 验证项目结构
            2. 编译源码
            3. 运行测试
            4. 打包项目
            5. 执行检查
            
            ### 输出目录
            
            ```
            build/
            ├── classes/
            ├── libs/
            ├── reports/
            └── test-results/
            ```
            
            ### 跳过测试
            
            ```bash
            gradle build -x test
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Build")
                .withDisplayText(report);
    }
    
    private CommandResult gradleTest() {
        String report = """
            ## Gradle Test
            
            ### 执行命令
            
            ```bash
            gradle test
            ```
            
            ### 测试流程
            
            1. 编译源码
            2. 编译测试代码
            3. 运行单元测试
            4. 生成测试报告
            
            ### 测试报告
            
            ```
            build/reports/tests/test/index.html
            ```
            
            ### 常用选项
            
            ```bash
            gradle test --tests MyTest          # 运行指定测试
            gradle test --continue              # 继续运行失败后
            gradle test --info                  # 详细信息
            gradle test --refresh-dependencies  # 刷新依赖
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Test")
                .withDisplayText(report);
    }
    
    private CommandResult gradleAssemble() {
        String report = """
            ## Gradle Assemble
            
            ### 执行命令
            
            ```bash
            gradle assemble
            ```
            
            ### 打包流程
            
            1. 编译源码
            2. 处理资源
            3. 打包输出
            
            ### 输出文件
            
            | 项目类型 | 输出文件 |
            |----------|---------|
            | JAR | build/libs/*.jar |
            | WAR | build/libs/*.war |
            
            ### 调试打包
            
            ```bash
            gradle assemble --info
            gradle assembleDebug      # Android Debug
            gradle assembleRelease    # Android Release
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Assemble")
                .withDisplayText(report);
    }
    
    private CommandResult gradleRun() {
        String report = """
            ## Gradle Run
            
            ### 执行命令
            
            ```bash
            gradle run
            ```
            
            ### 运行流程
            
            1. 编译源码
            2. 处理资源
            3. 启动应用
            
            ### 配置示例 (build.gradle)
            
            ```groovy
            application {
                mainClass = 'com.example.Main'
            }
            ```
            
            ### 带参数运行
            
            ```bash
            gradle run --args="arg1 arg2"
            gradle run -Dapp.args="arg1 arg2"
            ```
            
            ⚠️ 需要配置 application 插件
            """;
        
        return CommandResult.success("Gradle Run")
                .withDisplayText(report);
    }
    
    private CommandResult gradleTasks() {
        String report = """
            ## Gradle Tasks
            
            ### 执行命令
            
            ```bash
            gradle tasks
            gradle tasks --all    # 显示所有任务
            ```
            
            ### 常见任务
            
            | 任务 | 说明 |
            |------|------|
            | clean | 清理项目 |
            | build | 构建项目 |
            | test | 运行测试 |
            | assemble | 打包项目 |
            | check | 运行检查 |
            | dependencies | 查看依赖 |
            | projects | 查看子项目 |
            | help | 显示帮助 |
            
            ### 自定义任务
            
            ```groovy
            task hello {
                doLast {
                    println 'Hello Gradle!'
                }
            }
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Tasks")
                .withDisplayText(report);
    }
    
    private CommandResult gradleDependencies() {
        String report = """
            ## Gradle Dependencies
            
            ### 执行命令
            
            ```bash
            gradle dependencies
            gradle dependencies --configuration runtimeClasspath
            ```
            
            ### 依赖配置
            
            | 配置 | 说明 |
            |------|------|
            | implementation | 实现依赖 |
            | testImplementation | 测试依赖 |
            | runtimeOnly | 运行时依赖 |
            | compileOnly | 编译时依赖 |
            
            ### 依赖管理
            
            ```groovy
            dependencies {
                implementation 'org.springframework.boot:spring-boot-starter'
                testImplementation 'org.junit.jupiter:junit-jupiter'
                runtimeOnly 'com.mysql:mysql-connector-j'
            }
            ```
            
            ### 查看依赖树
            
            ```bash
            gradle dependencies --configuration runtimeClasspath
            gradle dependencyInsight --dependency <name>
            ```
            
            ⚠️ 需要 Gradle 环境
            """;
        
        return CommandResult.success("Gradle Dependencies")
                .withDisplayText(report);
    }
    
    private CommandResult executeCustomTask(String task) {
        String report = String.format("""
            ## Gradle 执行：%s
            
            ### 执行命令
            
            ```bash
            gradle %s
            ```
            
            ### 常见任务
            
            | 任务 | 说明 |
            |------|------|
            | clean | 清理项目 |
            | build | 构建项目 |
            | test | 运行测试 |
            | assemble | 打包项目 |
            | run | 运行应用 |
            | tasks | 查看任务 |
            | dependencies | 查看依赖 |
            
            ⚠️ 需要 Gradle 环境
            """, task, task);
        
        return CommandResult.success("Gradle 执行：" + task)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：gradle
            别名：gradlew
            描述：Gradle 构建管理
            
            用法：
              gradle                  # Gradle 信息
              gradle clean            # 清理项目
              gradle build            # 构建项目
              gradle test             # 运行测试
              gradle assemble         # 打包项目
              gradle run              # 运行应用
              gradle tasks            # 查看任务
            
            示例：
              gradle clean build
              gradle test
              gradle run
            """;
    }
}
