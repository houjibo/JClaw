package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * Agent 管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class AgentsCommand extends Command {
    
    // 模拟 Agent 存储
    private static final Map<String, AgentInfo> AGENTS = new ConcurrentHashMap<>();
    
    static {
        // 添加一些默认 Agent
        addAgent("main", "Main Agent", "idle");
        addAgent("architect", "Architecture Agent", "idle");
        addAgent("developer", "Developer Agent", "idle");
    }
    
    private static void addAgent(String id, String name, String status) {
        AgentInfo agent = new AgentInfo();
        agent.id = id;
        agent.name = name;
        agent.status = status;
        agent.createdAt = new Date().toString();
        agent.tasks = 0;
        AGENTS.put(id, agent);
    }
    
    public AgentsCommand() {
        this.name = "agents";
        this.description = "Agent 管理";
        this.aliases = Arrays.asList("agent", "ag");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listAgents();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> listAgents();
            case "create", "new" -> createAgent(parts.length > 1 ? parts[1] : null);
            case "start" -> startAgent(parts.length > 1 ? parts[1] : null);
            case "stop" -> stopAgent(parts.length > 1 ? parts[1] : null);
            case "delete", "rm" -> deleteAgent(parts.length > 1 ? parts[1] : null);
            case "info" -> agentInfo(parts.length > 1 ? parts[1] : null);
            case "status" -> agentStatus(parts.length > 1 ? parts[1] : null);
            default -> listAgents();
        };
    }
    
    private CommandResult listAgents() {
        StringBuilder sb = new StringBuilder();
        sb.append("## Agent 列表\n\n");
        sb.append("| ID | 名称 | 状态 | 任务数 | 创建时间 |\n");
        sb.append("|-----|------|------|--------|----------|\n");
        
        for (AgentInfo agent : AGENTS.values()) {
            String icon = switch (agent.status) {
                case "running" -> "🟢";
                case "busy" -> "🟡";
                case "error" -> "🔴";
                default -> "⚪";
            };
            sb.append(String.format("| %s | %s | %s %s | %d | %s |\n",
                    agent.id, agent.name, icon, agent.status, agent.tasks, agent.createdAt));
        }
        
        sb.append(String.format("\n共 %d 个 Agent\n", AGENTS.size()));
        
        return CommandResult.success("Agent 列表")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult createAgent(String name) {
        if (name == null) {
            return CommandResult.error("请指定 Agent 名称");
        }
        
        String id = name.toLowerCase().replaceAll("\\s+", "-");
        
        if (AGENTS.containsKey(id)) {
            return CommandResult.error("Agent 已存在：" + id);
        }
        
        addAgent(id, name, "idle");
        
        String report = String.format("""
            ## Agent 已创建
            
            **ID**: %s
            **名称**: %s
            **状态**: idle
            **时间**: %s
            
            启动 Agent：agents start %s
            查看状态：agents info %s
            """, id, name, new Date(), id, id);
        
        return CommandResult.success("Agent 已创建：" + id)
                .withDisplayText(report);
    }
    
    private CommandResult startAgent(String id) {
        if (id == null) {
            return CommandResult.error("请指定 Agent ID");
        }
        
        AgentInfo agent = AGENTS.get(id);
        if (agent == null) {
            return CommandResult.error("Agent 不存在：" + id);
        }
        
        agent.status = "running";
        
        return CommandResult.success("Agent 已启动：" + id)
                .withDisplayText("🟢 Agent `" + id + "` 已启动");
    }
    
    private CommandResult stopAgent(String id) {
        if (id == null) {
            return CommandResult.error("请指定 Agent ID");
        }
        
        AgentInfo agent = AGENTS.get(id);
        if (agent == null) {
            return CommandResult.error("Agent 不存在：" + id);
        }
        
        agent.status = "idle";
        
        return CommandResult.success("Agent 已停止：" + id)
                .withDisplayText("⚪ Agent `" + id + "` 已停止");
    }
    
    private CommandResult deleteAgent(String id) {
        if (id == null) {
            return CommandResult.error("请指定 Agent ID");
        }
        
        if (AGENTS.remove(id) == null) {
            return CommandResult.error("Agent 不存在：" + id);
        }
        
        return CommandResult.success("Agent 已删除：" + id)
                .withDisplayText("✅ Agent `" + id + "` 已删除");
    }
    
    private CommandResult agentInfo(String id) {
        if (id == null) {
            return CommandResult.error("请指定 Agent ID");
        }
        
        AgentInfo agent = AGENTS.get(id);
        if (agent == null) {
            return CommandResult.error("Agent 不存在：" + id);
        }
        
        String report = String.format("""
            ## Agent 详情：%s
            
            ### 基本信息
            
            | 属性 | 值 |
            |------|------|
            | ID | %s |
            | 名称 | %s |
            | 状态 | %s |
            | 创建时间 | %s |
            
            ### 统计
            
            | 属性 | 值 |
            |------|------|
            | 完成任务 | %d |
            | 运行时间 | - |
            | 内存使用 | - |
            
            ### 操作
            
            - `agents start %s` - 启动
            - `agents stop %s` - 停止
            - `agents delete %s` - 删除
            """,
                id, agent.id, agent.name, agent.status, agent.createdAt,
                agent.tasks,
                id, id, id);
        
        return CommandResult.success("Agent 详情")
                .withDisplayText(report);
    }
    
    private CommandResult agentStatus(String id) {
        if (id == null) {
            // 显示所有 Agent 状态
            return listAgents();
        }
        
        AgentInfo agent = AGENTS.get(id);
        if (agent == null) {
            return CommandResult.error("Agent 不存在：" + id);
        }
        
        String icon = switch (agent.status) {
            case "running" -> "🟢 运行中";
            case "busy" -> "🟡 忙碌";
            case "error" -> "🔴 错误";
            case "idle" -> "⚪ 空闲";
            default -> "⚪ " + agent.status;
        };
        
        String report = String.format("""
            ## Agent 状态：%s
            
            **状态**: %s
            **任务数**: %d
            **创建时间**: %s
            """, id, icon, agent.tasks, agent.createdAt);
        
        return CommandResult.success("Agent 状态")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：agents
            别名：agent, ag
            描述：Agent 管理
            
            用法：
              agents                  # 列出所有 Agent
              agents list             # 列出 Agent
              agents create <name>    # 创建 Agent
              agents start <id>       # 启动 Agent
              agents stop <id>        # 停止 Agent
              agents delete <id>      # 删除 Agent
              agents info <id>        # 查看详情
              agents status <id>      # 查看状态
            
            示例：
              agents
              agents create "Code Reviewer"
              agents start code-reviewer
              agents info code-reviewer
            """;
    }
    
    // Agent 信息类
    public static class AgentInfo {
        public String id;
        public String name;
        public String status;
        public String createdAt;
        public int tasks;
    }
}
