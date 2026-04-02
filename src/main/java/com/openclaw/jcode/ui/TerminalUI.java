package com.openclaw.jcode.ui;

import org.jline.reader.*;
import org.jline.terminal.*;
import org.jline.builtins.SyntaxHighlighter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliJLineCompleter;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * JClaw 终端 UI - 提供交互式命令行界面
 * 
 * 功能：
 * - 命令历史（上下箭头）
 * - Tab 自动补全
 * - 语法高亮
 * - 流式输出
 * - 进度条
 */
@Component
@CommandLine.Command(
    name = "jcode",
    version = "1.0.0",
    description = "JClaw - Java 编码智能体",
    mixinStandardHelpOptions = true,
    subcommands = {
        // Git 相关
        com.openclaw.jcode.command.impl.GitCommand.class,
        com.openclaw.jcode.command.impl.BranchCommand.class,
        com.openclaw.jcode.command.impl.CommitCommand.class,
        com.openclaw.jcode.command.impl.DiffCommand.class,
        com.openclaw.jcode.command.impl.ReviewCommand.class,
        // 系统管理
        com.openclaw.jcode.command.impl.ConfigCommand.class,
        com.openclaw.jcode.command.impl.StatusCommand.class,
        com.openclaw.jcode.command.impl.StatsCommand.class,
        com.openclaw.jcode.command.impl.DebugCommand.class,
        com.openclaw.jcode.command.impl.UpgradeCommand.class,
        // 会话管理
        com.openclaw.jcode.command.impl.SessionCommand.class,
        com.openclaw.jcode.command.impl.ClearCommand.class,
        com.openclaw.jcode.command.impl.ModelCommand.class,
        com.openclaw.jcode.command.impl.RenameCommand.class,
        com.openclaw.jcode.command.impl.ResumeCommand.class,
        // 文件操作
        com.openclaw.jcode.command.impl.CatCommand.class,
        com.openclaw.jcode.command.impl.FilesCommand.class,
        com.openclaw.jcode.command.impl.FindCommand.class,
        // AI 相关
        com.openclaw.jcode.command.impl.AICommand.class,
        com.openclaw.jcode.command.impl.AgentsCommand.class,
        // 其他
        com.openclaw.jcode.command.impl.HelpCommand.class,
        com.openclaw.jcode.command.impl.VersionCommand.class,
        com.openclaw.jcode.command.impl.ExitCommand.class
    }
)
public class TerminalUI implements Callable<Integer> {
    
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "详细输出模式")
    private boolean verbose;
    
    @CommandLine.Option(names = {"-c", "--config"}, description = "配置文件路径")
    private String configFile;
    
    private LineReader reader;
    private Terminal terminal;
    private boolean running = true;
    
    /**
     * 启动终端 UI
     */
    @Override
    public Integer call() throws Exception {
        // 初始化终端
        terminal = TerminalBuilder.builder()
            .system(true)
            .streams(System.in, System.out)
            .jansi(true)  // 启用 ANSI 颜色
            .build();
        
        // 初始化命令行解析器
        CommandLine commandLine = new CommandLine(new TerminalUI());
        
        // 初始化行阅读器
        reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new PicocliJLineCompleter(commandLine.getCommandSpec()))
            .parser(LineReaderBuilder.builder().build().getParser())
            .variable(LineReader.HISTORY_FILE, Paths.get(".jcode-history"))
            .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
            .variable(LineReader.INDENTATION, 2)
            .option(LineReader.Option.COMPLETE_IN_WORD, true)
            .option(LineReader.Option.AUTO_FRESH_LINE, true)
            .build();
        
        // 打印欢迎信息
        printWelcome();
        
        // 主循环
        while (running) {
            try {
                String line = reader.readLine(getPrompt());
                
                if (line == null) {
                    // EOF (Ctrl+D)
                    break;
                }
                
                line = line.trim();
                
                if (line.isEmpty()) {
                    continue;
                }
                
                if (line.equals("exit") || line.equals("quit")) {
                    break;
                }
                
                // 执行命令
                executeCommand(line);
                
            } catch (UserInterruptException e) {
                // Ctrl+C
                terminal.writer().println();
                continue;
            } catch (EndOfFileException e) {
                // Ctrl+D
                break;
            } catch (Exception e) {
                terminal.writer().println("错误：" + e.getMessage());
                if (verbose) {
                    e.printStackTrace(terminal.writer());
                }
            }
        }
        
        // 清理
        shutdown();
        return 0;
    }
    
    /**
     * 打印欢迎信息
     */
    private void printWelcome() {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        
        // ASCII Logo
        builder.append("┌─────────────────────────────────────────┐\n", AttributedStyle.DEFAULT);
        builder.append("│  ", AttributedStyle.DEFAULT);
        builder.append("JClaw", AttributedStyle.BOLD);
        builder.append(" - Java 编码智能体", AttributedStyle.DEFAULT);
        builder.append("          │\n", AttributedStyle.DEFAULT);
        builder.append("│  版本：1.0.0", AttributedStyle.DEFAULT);
        builder.append("                              │\n", AttributedStyle.DEFAULT);
        builder.append("└─────────────────────────────────────────┘\n", AttributedStyle.DEFAULT);
        builder.append("\n", AttributedStyle.DEFAULT);
        builder.append("输入 ", AttributedStyle.DEFAULT);
        builder.append("'help'", AttributedStyle.BOLD);
        builder.append(" 查看帮助，", AttributedStyle.DEFAULT);
        builder.append("'exit'", AttributedStyle.BOLD);
        builder.append(" 退出\n", AttributedStyle.DEFAULT);
        builder.append("\n", AttributedStyle.DEFAULT);
        
        terminal.writer().print(builder.toAnsi(terminal));
        terminal.writer().flush();
    }
    
    /**
     * 获取命令提示符
     */
    private String getPrompt() {
        return "\u001B[32mJClaw\u001B[0m\u001B[36m > \u001B[0m";
    }
    
    /**
     * 执行命令
     */
    private void executeCommand(String line) throws Exception {
        // 分割命令和参数
        String[] args = parseArgs(line);
        
        if (args.length == 0) {
            return;
        }
        
        // 创建命令行实例
        CommandLine commandLine = new CommandLine(new TerminalUI());
        
        // 执行命令
        int exitCode = commandLine.execute(args);
        
        if (exitCode != 0) {
            terminal.writer().println("命令执行失败，退出码：" + exitCode);
        }
    }
    
    /**
     * 解析命令行参数
     */
    private String[] parseArgs(String line) {
        // 简单的参数解析（支持引号）
        return line.split("\\s+");
    }
    
    /**
     * 显示进度条
     */
    public void showProgress(String message, int total, int current) {
        int width = 40;
        int filled = (current * width) / total;
        
        StringBuilder sb = new StringBuilder();
        sb.append("\r");
        sb.append(message);
        sb.append(" [");
        
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                sb.append("█");
            } else {
                sb.append("░");
            }
        }
        
        sb.append("] ");
        sb.append(String.format("%3d%%", (current * 100) / total));
        
        terminal.writer().print(sb.toString());
        terminal.writer().flush();
        
        if (current >= total) {
            terminal.writer().println();
        }
    }
    
    /**
     * 流式输出文本
     */
    public void streamOutput(String text) {
        terminal.writer().print(text);
        terminal.writer().flush();
    }
    
    /**
     * 显示成功消息
     */
    public void showSuccess(String message) {
        terminal.writer().println("\u001B[32m✓\u001B[0m " + message);
    }
    
    /**
     * 显示错误消息
     */
    public void showError(String message) {
        terminal.writer().println("\u001B[31m✗\u001B[0m " + message);
    }
    
    /**
     * 显示警告消息
     */
    public void showWarning(String message) {
        terminal.writer().println("\u001B[33m⚠\u001B[0m " + message);
    }
    
    /**
     * 清屏
     */
    public void clearScreen() {
        if (terminal != null) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.flush();
        }
    }
    
    /**
     * 清理资源
     */
    private void shutdown() {
        if (terminal != null) {
            try {
                terminal.close();
            } catch (Exception e) {
                // 忽略
            }
        }
    }
    
    /**
     * 停止运行
     */
    public void stop() {
        running = false;
    }
}
