package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 支持：list, add, remove, resources 等
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class McpCommand extends Command {
    
    // 模拟 MCP 服务器
    private static final Map<String, Map<String, Object>> MCP_SERVERS = new HashMap<>();
    static {
        Map<String, Object> server1 = new HashMap<>();
        server1.put("name", "filesystem");
        server1.put("type", "stdio");
        server1.put("status", "connected");
        server1.put("resources", 5);
        MCP_SERVERS.put("filesystem", server1);
        
        Map<String, Object> server2 = new HashMap<>();
        server2.put("name", "github");
        server2.put("type", "sse");
        server2.put("status", "connected");
        server2.put("resources", 8);
        MCP_SERVERS.put("github", server2);
    }
    
    public McpCommand() {
        this.name = "mcp";
        this.description = "MCP 服务器管理";
        this.aliases = Arrays.asList("m");
        this.category = CommandCategory.PLUGIN;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("action", new CommandParameter("action", 
            "操作类型 (list, add, remove, resources, auth)", true)
            .choices("list", "add", "remove", "resources", "auth"));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return listServers();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list" -> listServers();
            case "resources" -> listResources(parts.length > 1 ? parts[1] : null);
            case "add" -> addServer(parts.length > 1 ? parts[1] : "unknown");
            case "remove" -> removeServer(parts.length > 1 ? parts[1] : null);
            case "auth" -> authServer(parts.length > 1 ? parts[1] : null);
            default -> CommandResult.error("未知操作：" + action);
        };
    }
    
    private CommandResult listServers() {
        StringBuilder sb = new StringBuilder();
        sb.append("## MCP 服务器列表\n\n");
        sb.append("| 名称 | 类型 | 状态 | 资源数 |\n");
        sb.append("|------|------|------|--------|\n");
        
        for (Map<String, Object> server : MCP_SERVERS.values()) {
            sb.append(String.format("| %s | %s | %s | %d |\n",
                    server.get("name"),
                    server.get("type"),
                    server.get("status"),
                    server.get("resources")));
        }
        
        return CommandResult.success("MCP 服务器列表")
                .withData("servers", new ArrayList<>(MCP_SERVERS.values()))
                .withDisplayText(sb.toString());
    }
    
    private CommandResult listResources(String serverName) {
        StringBuilder sb = new StringBuilder();
        sb.append("## MCP 资源列表\n\n");
        
        if (serverName != null) {
            Map<String, Object> server = MCP_SERVERS.get(serverName);
            if (server == null) {
                return CommandResult.error("服务器不存在：" + serverName);
            }
            sb.append("服务器：").append(serverName).append("\n\n");
            sb.append(String.format("资源数：%d\n", server.get("resources")));
        } else {
            int totalResources = MCP_SERVERS.values().stream()
                    .mapToInt(s -> (Integer) s.get("resources"))
                    .sum();
            sb.append("总资源数：").append(totalResources).append("\n\n");
            
            for (Map<String, Object> server : MCP_SERVERS.values()) {
                sb.append("- **").append(server.get("name")).append("**: ")
                        .append(server.get("resources")).append(" 个资源\n");
            }
        }
        
        return CommandResult.success("MCP 资源列表")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult addServer(String name) {
        if (MCP_SERVERS.containsKey(name)) {
            return CommandResult.error("服务器已存在：" + name);
        }
        
        Map<String, Object> server = new HashMap<>();
        server.put("name", name);
        server.put("type", "unknown");
        server.put("status", "disconnected");
        server.put("resources", 0);
        
        MCP_SERVERS.put(name, server);
        
        return CommandResult.success("MCP 服务器已添加：" + name)
                .withData("server", server);
    }
    
    private CommandResult removeServer(String name) {
        if (name == null) {
            return CommandResult.error("请指定服务器名称");
        }
        
        if (MCP_SERVERS.remove(name) == null) {
            return CommandResult.error("服务器不存在：" + name);
        }
        
        return CommandResult.success("MCP 服务器已移除：" + name);
    }
    
    private CommandResult authServer(String name) {
        if (name == null) {
            return CommandResult.error("请指定服务器名称");
        }
        
        Map<String, Object> server = MCP_SERVERS.get(name);
        if (server == null) {
            return CommandResult.error("服务器不存在：" + name);
        }
        
        server.put("status", "connected");
        
        return CommandResult.success("MCP 服务器认证成功：" + name)
                .withData("server", server);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：mcp
            别名：m
            描述：MCP 服务器管理
            
            用法：
              mcp list                      # 列出所有服务器
              mcp resources [server]        # 列出资源
              mcp add <name>                # 添加服务器
              mcp remove <name>             # 移除服务器
              mcp auth <name>               # 认证服务器
            
            示例：
              mcp list
              mcp resources
              mcp resources filesystem
              mcp add myserver
              mcp auth myserver
            """;
    }
}
