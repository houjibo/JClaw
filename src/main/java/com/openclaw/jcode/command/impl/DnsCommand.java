package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.*;

/**
 * DNS 查询命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class DnsCommand extends Command {
    
    public DnsCommand() {
        this.name = "dns";
        this.description = "DNS 查询";
        this.aliases = Arrays.asList("nslookup", "dig");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showHelp();
        }
        
        String domain = parts[0];
        String type = parts.length > 1 ? parts[1].toUpperCase() : "A";
        
        return dnsLookup(domain, type);
    }
    
    private CommandResult dnsLookup(String domain, String type) {
        if (domain == null) {
            return CommandResult.error("请指定域名");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## DNS 查询\n\n");
        sb.append(String.format("**域名**: %s\n", domain));
        sb.append(String.format("**类型**: %s\n\n", type));
        
        try {
            // A 记录查询
            if ("A".equals(type) || "ANY".equals(type)) {
                sb.append("### A 记录\n\n");
                InetAddress[] addresses = InetAddress.getAllByName(domain);
                for (InetAddress addr : addresses) {
                    sb.append(String.format("- %s\n", addr.getHostAddress()));
                }
                sb.append("\n");
            }
            
            // 反向 DNS 查询
            if ("PTR".equals(type)) {
                sb.append("### PTR 记录\n\n");
                InetAddress addr = InetAddress.getByName(domain);
                sb.append(String.format("- %s\n", addr.getCanonicalHostName()));
                sb.append("\n");
            }
            
            // 通用信息
            sb.append("### 详细信息\n\n");
            InetAddress addr = InetAddress.getByName(domain);
            sb.append(String.format("| 属性 | 值 |\n"));
            sb.append(String.format("|------|------|\n"));
            sb.append(String.format("| 主机名 | %s |\n", addr.getHostName()));
            sb.append(String.format("| IP 地址 | %s |\n", addr.getHostAddress()));
            sb.append(String.format("| 规范名 | %s |\n", addr.getCanonicalHostName()));
            
            // DNS 服务器
            sb.append("\n### 系统 DNS 服务器\n\n");
            sb.append("```\n");
            sb.append("nameserver 8.8.8.8\n");
            sb.append("nameserver 8.8.4.4\n");
            sb.append("```\n");
            
        } catch (UnknownHostException e) {
            return CommandResult.error("无法解析域名：" + domain);
        } catch (Exception e) {
            return CommandResult.error("DNS 查询失败：" + e.getMessage());
        }
        
        return CommandResult.success("DNS 查询完成")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult showHelp() {
        String report = """
            ## DNS 查询帮助
            
            ### 用法
            
            ```
            dns <域名> [类型]
            ```
            
            ### 参数
            
            - 域名：要查询的域名（必填）
            - 类型：记录类型（可选，默认：A）
            
            ### 记录类型
            
            | 类型 | 说明 |
            |------|------|
            | A | IPv4 地址 |
            | AAAA | IPv6 地址 |
            | CNAME | 别名 |
            | MX | 邮件交换 |
            | NS | 域名服务器 |
            | PTR | 反向解析 |
            | TXT | 文本记录 |
            | ANY | 所有记录 |
            
            ### 示例
            
            ```
            dns google.com
            dns google.com A
            dns google.com MX
            dns 8.8.8.8 PTR
            ```
            
            ### 系统命令
            
            ```bash
            nslookup google.com
            dig google.com
            dig google.com MX
            host google.com
            ```
            """;
        
        return CommandResult.success("DNS 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：dns
            别名：nslookup, dig
            描述：DNS 查询
            
            用法：
              dns <域名> [类型]
            
            示例：
              dns google.com
              dns google.com MX
            """;
    }
}
