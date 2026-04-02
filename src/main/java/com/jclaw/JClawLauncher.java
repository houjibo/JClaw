package com.jclaw;

import com.jclaw.ui.TerminalUI;
import picocli.CommandLine;

/**
 * JClaw 终端启动器
 * 
 * 提供两种启动模式：
 * 1. 终端交互模式（默认）
 * 2. 服务器模式（--server）
 */
public class JClawLauncher {
    
    public static void main(String[] args) {
        // 检查是否为服务器模式
        boolean serverMode = false;
        for (String arg : args) {
            if ("--server".equals(arg) || "-s".equals(arg)) {
                serverMode = true;
                break;
            }
        }
        
        if (serverMode) {
            // 服务器模式 - 启动 Spring Boot
            System.out.println("启动 JClaw 服务器模式...");
            JClawApplication.main(args);
        } else {
            // 终端交互模式
            startTerminalMode(args);
        }
    }
    
    /**
     * 启动终端交互模式
     */
    private static void startTerminalMode(String[] args) {
        try {
            TerminalUI terminalUI = new TerminalUI();
            int exitCode = new CommandLine(terminalUI).execute(args);
            System.exit(exitCode);
        } catch (Exception e) {
            System.err.println("启动失败：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
