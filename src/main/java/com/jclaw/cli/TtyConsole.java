package com.jclaw.cli;

import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TTY 交互式命令行
 */
@Slf4j
@Component
public class TtyConsole {
    
    private final LineReader reader;
    
    public TtyConsole() {
        try {
            Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();
            this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("TTY 初始化失败", e);
        }
    }
    
    /**
     * 启动交互式控制台
     */
    public void start() {
        System.out.println("\n🥤 JClaw TTY 控制台");
        System.out.println("输入 'help' 查看帮助，'exit' 退出\n");
        
        while (true) {
            try {
                String line = reader.readLine("👤 > ");
                
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                
                if ("exit".equals(line.trim().toLowerCase())) {
                    System.out.println("再见！");
                    break;
                }
                
                if ("help".equals(line.trim().toLowerCase())) {
                    printHelp();
                    continue;
                }
                
                // 执行命令
                executeCommand(line.trim());
                
            } catch (UserInterruptException e) {
                System.out.println("\n按 Ctrl+D 或输入 'exit' 退出");
            } catch (Exception e) {
                log.error("命令执行失败", e);
                System.out.println("错误：" + e.getMessage());
            }
        }
    }
    
    /**
     * 执行命令
     */
    private void executeCommand(String line) {
        String[] parts = line.split("\\s+", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";
        
        switch (command) {
            case "chat":
                System.out.println("对话功能需要连接 AI 服务...");
                break;
            case "skill":
                System.out.println("技能列表:");
                System.out.println("  - bash: 执行命令");
                System.out.println("  - file_read: 读取文件");
                System.out.println("  - file_write: 写入文件");
                System.out.println("  - git: Git 操作");
                System.out.println("  - todo_write: 任务管理");
                break;
            case "status":
                System.out.println("✅ JClaw 运行中");
                break;
            default:
                System.out.println("未知命令：" + command);
                System.out.println("输入 'help' 查看帮助");
        }
    }
    
    /**
     * 打印帮助
     */
    private void printHelp() {
        System.out.println("""
            
            🥤 JClaw TTY 控制台命令:
            
              chat              交互式对话
              skill             查看技能列表
              status            查看状态
              help              显示帮助
              exit              退出控制台
            
            示例:
              skill
              status
            """);
    }
}
