package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.lang.management.*;
import java.net.*;
import java.util.*;

/**
 * Netstat 命令 - 网络状态查看
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class NetstatCommand extends Command {
    
    public NetstatCommand() {
        this.name = "netstat";
        this.description = "网络状态查看";
        this.aliases = Arrays.asList("ss", "network");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showNetworkStatus();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "status" -> showNetworkStatus();
            case "interfaces" -> showInterfaces();
            case "routes" -> showRoutes();
            case "info" -> showHelp();
            default -> showNetworkStatus();
        };
    }
    
    private CommandResult showNetworkStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 网络状态\n\n");
        
        // 获取网络接口信息
        sb.append("### 网络接口\n\n");
        sb.append("| 接口 | IP 地址 | 状态 |\n");
        sb.append("|------|--------|------|\n");
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                
                if (!ni.isUp()) continue;
                
                String name = ni.getName();
                String displayName = ni.getDisplayName();
                String status = ni.isUp() ? "🟢 UP" : "⚪ DOWN";
                
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    sb.append(String.format("| %s | %s | %s |\n", 
                            name, addr.getHostAddress(), status));
                }
            }
        } catch (Exception e) {
            sb.append("| - | 获取失败 | - |\n");
        }
        
        // 系统网络统计
        sb.append("\n### 系统网络信息\n\n");
        
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        
        sb.append("| 属性 | 值 |\n");
        sb.append("|------|------|\n");
        sb.append(String.format("| 系统 | %s |\n", os.getName()));
        sb.append(String.format("| 架构 | %s |\n", os.getArch()));
        
        // JVM 网络相关
        sb.append("\n### JVM 网络属性\n\n");
        
        sb.append("| 属性 | 值 |\n");
        sb.append("|------|------|\n");
        sb.append(String.format("| IPv4 可用 | %s |\n", System.getProperty("java.net.ipv4Stack", "true")));
        sb.append(String.format("| 网络地址缓存 TTL | %s |\n", 
                System.getProperty("networkaddress.cache.ttl", "默认")));
        
        return CommandResult.success("网络状态")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult showInterfaces() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 网络接口详情\n\n");
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                
                sb.append(String.format("### %s (%s)\n\n", ni.getName(), ni.getDisplayName()));
                sb.append(String.format("**状态**: %s\n", ni.isUp() ? "🟢 UP" : "⚪ DOWN"));
                sb.append(String.format("**MAC**: %s\n\n", formatMac(ni.getHardwareAddress())));
                
                sb.append("| IP 地址 | 类型 |\n");
                sb.append("|--------|------|\n");
                
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String type = addr instanceof Inet4Address ? "IPv4" : "IPv6";
                    sb.append(String.format("| %s | %s |\n", addr.getHostAddress(), type));
                }
                sb.append("\n");
            }
        } catch (Exception e) {
            return CommandResult.error("获取接口信息失败：" + e.getMessage());
        }
        
        return CommandResult.success("网络接口")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult showRoutes() {
        String report = """
            ## 路由表
            
            ### 系统命令
            
            ```bash
            # Linux/Mac
            netstat -rn
            ip route show
            
            # Windows
            route print
            ```
            
            ### 默认路由
            
            | 目标 | 网关 | 接口 |
            |------|------|------|
            | default | 192.168.1.1 | eth0 |
            | 192.168.1.0/24 | - | eth0 |
            
            ⚠️ 实际路由表需要系统命令查看
            """;
        
        return CommandResult.success("路由表")
                .withDisplayText(report);
    }
    
    private String formatMac(byte[] mac) {
        if (mac == null) {
            return "N/A";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i > 0) sb.append(":");
            sb.append(String.format("%02X", mac[i]));
        }
        return sb.toString();
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Netstat 帮助
            
            ### 用法
            
            ```
            netstat                   # 网络状态
            netstat interfaces        # 接口详情
            netstat routes            # 路由表
            ```
            
            ### 系统命令
            
            | 命令 | 说明 |
            |------|------|
            | netstat -an | 所有连接 |
            | netstat -rn | 路由表 |
            | netstat -i | 接口统计 |
            | ss -tuln | 监听端口 |
            
            ### 常见场景
            
            1. **查看监听端口**
               ```
               netstat -tuln
               ```
            
            2. **查看连接状态**
               ```
               netstat -an | grep ESTABLISHED
               ```
            
            3. **查看路由表**
               ```
               netstat -rn
               ```
            """;
        
        return CommandResult.success("Netstat 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：netstat
            别名：ss, network
            描述：网络状态查看
            
            用法：
              netstat                   # 网络状态
              netstat interfaces        # 接口详情
              netstat routes            # 路由表
            
            示例：
              netstat
              netstat interfaces
            """;
    }
}
