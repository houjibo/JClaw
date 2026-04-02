package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Port 命令 - 端口占用检查
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class PortCommand extends Command {
    
    public PortCommand() {
        this.name = "port";
        this.description = "端口占用检查";
        this.aliases = Arrays.asList("netstat");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showListeningPorts();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "check", "test" -> checkPort(parts.length > 1 ? parts[1] : null);
            case "scan" -> scanPorts(parts.length > 2 ? parts[1] : "localhost", parts.length > 2 ? parts[2] : null);
            case "listening" -> showListeningPorts();
            default -> checkPort(parts[0]);
        };
    }
    
    private CommandResult showListeningPorts() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 监听端口\n\n");
        sb.append("### 常见端口检查\n\n");
        sb.append("| 端口 | 服务 | 状态 |\n");
        sb.append("|------|------|------|\n");
        
        int[] commonPorts = {22, 80, 443, 3000, 3306, 5432, 6379, 8080, 8443, 9000};
        
        for (int port : commonPorts) {
            String status = isPortListening(port) ? "🟢 开放" : "⚪ 关闭";
            String service = getServiceName(port);
            sb.append(String.format("| %d | %s | %s |\n", port, service, status));
        }
        
        sb.append("\n### 说明\n\n");
        sb.append("🟢 开放 - 端口可访问\n");
        sb.append("⚪ 关闭 - 端口不可访问\n");
        
        return CommandResult.success("监听端口")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult checkPort(String portStr) {
        if (portStr == null) {
            return CommandResult.error("请指定端口号");
        }
        
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return CommandResult.error("无效端口号：" + portStr);
        }
        
        boolean isOpen = isPortListening(port);
        
        String report = String.format("""
            ## 端口检查：%d
            
            **状态**: %s
            
            ### 详情
            
            | 属性 | 值 |
            |------|------|
            | 端口 | %d |
            | 协议 | TCP |
            | 服务 | %s |
            
            ### 测试方法
            
            ```bash
            # Telnet 测试
            telnet localhost %d
            
            # Netcat 测试
            nc -zv localhost %d
            
            # Curl 测试（HTTP）
            curl http://localhost:%d
            ```
            """,
            port,
            isOpen ? "🟢 开放" : "⚪ 关闭",
            port,
            getServiceName(port),
            port, port, port);
        
        return CommandResult.success("端口检查：" + port)
                .withDisplayText(report);
    }
    
    private CommandResult scanPorts(String host, String range) {
        if (range == null) {
            range = "1-1024";
        }
        
        String[] parts = range.split("-");
        int start = 1;
        int end = 1024;
        
        try {
            start = Integer.parseInt(parts[0]);
            end = parts.length > 1 ? Integer.parseInt(parts[1]) : start;
        } catch (NumberFormatException e) {
            return CommandResult.error("无效端口范围：" + range);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 端口扫描\n\n");
        sb.append(String.format("**主机**: %s  **范围**: %d-%d\n\n", host, start, end));
        
        List<Integer> openPorts = new ArrayList<>();
        
        for (int port = start; port <= end; port++) {
            if (isPortOpen(host, port)) {
                openPorts.add(port);
                sb.append(String.format("🟢 %d (%s)\n", port, getServiceName(port)));
            }
        }
        
        if (openPorts.isEmpty()) {
            sb.append("\n未发现开放端口\n");
        } else {
            sb.append(String.format("\n共发现 %d 个开放端口\n", openPorts.size()));
        }
        
        return CommandResult.success("端口扫描完成")
                .withDisplayText(sb.toString());
    }
    
    private boolean isPortListening(int port) {
        return isPortOpen("localhost", port);
    }
    
    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private String getServiceName(int port) {
        return switch (port) {
            case 20 -> "FTP-Data";
            case 21 -> "FTP";
            case 22 -> "SSH";
            case 23 -> "Telnet";
            case 25 -> "SMTP";
            case 53 -> "DNS";
            case 80 -> "HTTP";
            case 110 -> "POP3";
            case 143 -> "IMAP";
            case 443 -> "HTTPS";
            case 3306 -> "MySQL";
            case 5432 -> "PostgreSQL";
            case 6379 -> "Redis";
            case 8080 -> "HTTP-Alt";
            case 8443 -> "HTTPS-Alt";
            case 9000 -> "PHP-FPM";
            case 27017 -> "MongoDB";
            default -> "Unknown";
        };
    }
    
    @Override
    public String getHelp() {
        return """
            命令：port
            别名：netstat
            描述：端口占用检查
            
            用法：
              port                    # 检查常见端口
              port <端口>             # 检查指定端口
              port check <端口>       # 检查端口
              port scan [主机] <范围>  # 扫描端口
              port listening          # 监听端口
            
            示例：
              port
              port 8080
              port scan localhost 1-1024
            """;
    }
}
