package com.jclaw.cli;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.jclaw.JClawApplication;
import com.jclaw.command.CommandRegistry;
import com.jclaw.command.Command;
import com.jclaw.command.CommandContext;
import com.jclaw.command.CommandResult;

import java.util.Arrays;
import java.util.List;

/**
 * JClaw 交互式命令行界面
 * 提供类似 Claude Code 的 REPL 体验
 */
public class JClawCli {
    
    private final CommandRegistry commandRegistry;
    private final Terminal terminal;
    private final LineReader reader;
    private boolean running = true;
    
    public JClawCli(ApplicationContext appContext) {
        this.commandRegistry = appContext.getBean(CommandRegistry.class);
        
        try {
            this.terminal = TerminalBuilder.builder()
                .system(true)
                .build();
            
            this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("初始化终端失败", e);
        }
    }
    
    public void start() {
        printBanner();
        printHelp();
        
        while (running) {
            try {
                String line = reader.readLine("\u001B[32mJClaw\u001B[0m\u001B[36m > \u001B[0m");
                
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.equals("exit") || line.equals("quit") || line.equals("/exit")) break;
                if (line.equals("help") || line.equals("/help")) { printHelp(); continue; }
                if (line.equals("clear") || line.equals("/clear")) {
                    terminal.writer().print("\u001B[2J\u001B[1;1H");
                    terminal.flush();
                    continue;
                }
                
                executeCommand(line);
                
            } catch (Exception e) {
                terminal.writer().println("\u001B[31m错误：\u001B[0m" + e.getMessage());
                terminal.flush();
            }
        }
        
        shutdown();
    }
    
    private void executeCommand(String line) {
        String[] parts = line.split("\\s+");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        
        Command command = commandRegistry.getCommand(commandName);
        if (command == null) {
            terminal.writer().println("\u001B[31m✗ 未知命令：\u001B[0m" + commandName);
            terminal.flush();
            return;
        }
        
        try {
            CommandContext context = new CommandContext(null, "cli-user", "cli-session", true);
            CommandResult result = command.execute(String.join(" ", args), context);
            
            if (result.getType() == CommandResult.ResultType.SUCCESS) {
                if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                    terminal.writer().println("\u001B[32m✓ " + result.getMessage() + "\u001B[0m");
                }
            } else {
                terminal.writer().println("\u001B[31m✗ " + (result.getMessage() != null ? result.getMessage() : "执行失败") + "\u001B[0m");
            }
        } catch (Exception e) {
            terminal.writer().println("\u001B[31m✗ " + e.getMessage() + "\u001B[0m");
        }
        
        terminal.flush();
    }
    
    private void printBanner() {
        terminal.writer().println();
        terminal.writer().println("\u001B[32m╔════════════════════════════════════════════════════════╗\u001B[0m");
        terminal.writer().println("\u001B[32m║\u001B[0m  \u001B[1mJClaw\u001B[0m - Java 编码智能体                           \u001B[32m║\u001B[0m");
        terminal.writer().println("\u001B[32m║\u001B[0m  版本：1.0.0                                      \u001B[32m║\u001B[0m");
        terminal.writer().println("\u001B[32m╚════════════════════════════════════════════════════════╝\u001B[0m");
        terminal.writer().println();
        terminal.flush();
    }
    
    private void printHelp() {
        terminal.writer().println("\u001B[1m常用命令：\u001B[0m");
        terminal.writer().println("  \u001B[36m/help\u001B[0m     - 显示帮助");
        terminal.writer().println("  \u001B[36m/clear\u001B[0m    - 清屏");
        terminal.writer().println("  \u001B[36m/exit\u001B[0m     - 退出");
        terminal.writer().println("  \u001B[36m/commands\u001B[0m  - 列出所有命令");
        terminal.writer().println("  \u001B[36m/tools\u001B[0m     - 列出所有工具");
        terminal.writer().println();
        terminal.writer().println("示例：\u001B[33m commit review config\u001B[0m");
        terminal.writer().println("\u001B[90m提示：Tab 键自动补全\u001B[0m");
        terminal.writer().println();
        terminal.flush();
    }
    
    private void shutdown() {
        try {
            if (terminal != null) terminal.close();
        } catch (Exception e) {}
    }
    
    public static void main(String[] args) {
        ApplicationContext appContext = new AnnotationConfigApplicationContext(JClawApplication.class);
        JClawCli cli = new JClawCli(appContext);
        cli.start();
        if (appContext instanceof AnnotationConfigApplicationContext) {
            ((AnnotationConfigApplicationContext) appContext).close();
        }
    }
}
