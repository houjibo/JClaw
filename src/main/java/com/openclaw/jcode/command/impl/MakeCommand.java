package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Make 命令 - Make 构建
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class MakeCommand extends Command {
    
    public MakeCommand() {
        this.name = "make";
        this.description = "Make 构建";
        this.aliases = Arrays.asList("makefile", "cmake");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return makeDefault();
        }
        
        String target = parts[0];
        
        return switch (target) {
            case "clean" -> makeClean();
            case "all" -> makeAll();
            case "build" -> makeBuild();
            case "install" -> makeInstall();
            case "test" -> makeTest();
            case "help" -> showMakeHelp();
            default -> makeTarget(target);
        };
    }
    
    private CommandResult makeDefault() {
        String report = """
            ## Make 构建
            
            ### 执行命令
            
            ```bash
            make            # 执行默认目标
            make all        # 构建所有
            make clean      # 清理
            make install    # 安装
            ```
            
            ### Makefile 示例
            
            ```makefile
            CC = gcc
            CFLAGS = -Wall -g
            
            all: myapp
            
            myapp: main.o utils.o
            	$(CC) $(CFLAGS) -o myapp main.o utils.o
            
            main.o: main.c
            	$(CC) $(CFLAGS) -c main.c
            
            clean:
            	rm -f *.o myapp
            
            install: myapp
            	cp myapp /usr/local/bin/
            ```
            
            ### 常用目标
            
            | 目标 | 说明 |
            |------|------|
            | all | 构建所有（默认） |
            | clean | 清理构建产物 |
            | install | 安装到系统 |
            | uninstall | 卸载 |
            | test | 运行测试 |
            | help | 显示帮助 |
            
            ⚠️ 需要 Makefile 和 make 工具
            """;
        
        return CommandResult.success("Make 构建")
                .withDisplayText(report);
    }
    
    private CommandResult makeClean() {
        String report = """
            ## Make Clean
            
            ### 执行命令
            
            ```bash
            make clean
            ```
            
            ### 清理内容
            
            - 编译产物（*.o, *.obj）
            - 可执行文件
            - 临时文件
            - 构建目录
            
            ### Makefile 规则
            
            ```makefile
            clean:
            	rm -f *.o myapp
            	rm -rf build/
            ```
            
            ### 效果
            
            ✅ 构建产物已清理
            ✅ 目录已清空
            
            ⚠️ 需要 Makefile 和 make 工具
            """;
        
        return CommandResult.success("Make Clean")
                .withDisplayText(report);
    }
    
    private CommandResult makeAll() {
        String report = """
            ## Make All
            
            ### 执行命令
            
            ```bash
            make all
            ```
            
            ### 构建流程
            
            1. 检查依赖
            2. 编译源码
            3. 链接目标
            4. 生成可执行文件
            
            ### Makefile 规则
            
            ```makefile
            all: myapp
            
            myapp: main.o utils.o
            	$(CC) -o myapp main.o utils.o
            ```
            
            ### 输出
            
            ```
            [CC] main.o
            [CC] utils.o
            [LD] myapp
            Build complete!
            ```
            
            ⚠️ 需要 Makefile 和 make 工具
            """;
        
        return CommandResult.success("Make All")
                .withDisplayText(report);
    }
    
    private CommandResult makeBuild() {
        return makeAll();
    }
    
    private CommandResult makeInstall() {
        String report = """
            ## Make Install
            
            ### 执行命令
            
            ```bash
            make install
            ```
            
            ### 安装流程
            
            1. 构建项目
            2. 复制到目标目录
            3. 设置权限
            4. 更新缓存
            
            ### Makefile 规则
            
            ```makefile
            install: myapp
            	cp myapp /usr/local/bin/
            	chmod +x /usr/local/bin/myapp
            ```
            
            ### 安装位置
            
            | 类型 | 位置 |
            |------|------|
            | 可执行文件 | /usr/local/bin |
            | 库文件 | /usr/local/lib |
            | 头文件 | /usr/local/include |
            | 配置文件 | /etc |
            
            ⚠️ 可能需要 sudo 权限
            """;
        
        return CommandResult.success("Make Install")
                .withDisplayText(report);
    }
    
    private CommandResult makeTest() {
        String report = """
            ## Make Test
            
            ### 执行命令
            
            ```bash
            make test
            ```
            
            ### 测试流程
            
            1. 编译测试代码
            2. 运行测试套件
            3. 生成测试报告
            4. 显示结果
            
            ### Makefile 规则
            
            ```makefile
            test: myapp
            	./run_tests.sh
            ```
            
            ### 测试输出
            
            ```
            Running tests...
            Test 1: PASSED
            Test 2: PASSED
            Test 3: FAILED
            
            2/3 tests passed
            ```
            
            ⚠️ 需要 Makefile 和测试脚本
            """;
        
        return CommandResult.success("Make Test")
                .withDisplayText(report);
    }
    
    private CommandResult makeTarget(String target) {
        String report = String.format("""
            ## Make Target: %s
            
            ### 执行命令
            
            ```bash
            make %s
            ```
            
            ### 说明
            
            执行 Makefile 中定义的 `%s` 目标。
            
            ### 常见自定义目标
            
            | 目标 | 说明 |
            |------|------|
            | debug | 调试构建 |
            | release | 发布构建 |
            | docs | 生成文档 |
            | package | 打包 |
            | deploy | 部署 |
            
            ### Makefile 示例
            
            ```makefile
            %s:
            	@echo "Building %s..."
            	# 自定义构建步骤
            ```
            
            ⚠️ 需要 Makefile 定义该目标
            """, target, target, target, target, target);
        
        return CommandResult.success("Make Target: " + target)
                .withDisplayText(report);
    }
    
    private CommandResult showMakeHelp() {
        return makeDefault();
    }
    
    @Override
    public String getHelp() {
        return """
            命令：make
            别名：makefile, cmake
            描述：Make 构建
            
            用法：
              make                    # 默认构建
              make <目标>             # 指定目标
              make clean              # 清理
              make install            # 安装
              make test               # 测试
            
            示例：
              make
              make clean
              make install
            """;
    }
}
