package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * 记忆管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class MemoryCommand extends Command {
    
    // 模拟记忆存储
    private static final Map<String, MemoryInfo> MEMORIES = new ConcurrentHashMap<>();
    
    static {
        // 添加示例记忆
        addMemory("pref-1", "用户偏好", "喜欢简洁的代码风格");
        addMemory("fact-1", "项目信息", "JClaw 是基于 Java 的 AI 编码助手");
    }
    
    private static void addMemory(String id, String category, String content) {
        MemoryInfo memory = new MemoryInfo();
        memory.id = id;
        memory.category = category;
        memory.content = content;
        memory.createdAt = new Date().toString();
        memory.accessCount = 0;
        MEMORIES.put(id, memory);
    }
    
    public MemoryCommand() {
        this.name = "memory";
        this.description = "记忆管理";
        this.aliases = Arrays.asList("mem", "me");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listMemories();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> listMemories();
            case "add" -> addMemory(parts.length > 2 ? parts[1] : "general", parts.length > 2 ? String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)) : null);
            case "get" -> getMemory(parts.length > 1 ? parts[1] : null);
            case "delete", "rm" -> deleteMemory(parts.length > 1 ? parts[1] : null);
            case "search" -> searchMemory(parts.length > 1 ? parts[1] : null);
            case "clear" -> clearMemories();
            case "info" -> memoryInfo(parts.length > 1 ? parts[1] : null);
            default -> listMemories();
        };
    }
    
    private CommandResult listMemories() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 记忆列表\n\n");
        sb.append("| ID | 分类 | 内容 | 访问次数 | 创建时间 |\n");
        sb.append("|-----|------|------|---------|----------|\n");
        
        for (MemoryInfo memory : MEMORIES.values()) {
            sb.append(String.format("| %s | %s | %s | %d | %s |\n",
                    memory.id, memory.category, truncate(memory.content, 20),
                    memory.accessCount, memory.createdAt));
        }
        
        sb.append(String.format("\n共 %d 条记忆\n", MEMORIES.size()));
        
        return CommandResult.success("记忆列表")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult addMemory(String category, String content) {
        if (content == null) {
            content = category;
            category = "general";
        }
        
        String id = category.toLowerCase().replaceAll("\\s+", "-") + "-" + System.currentTimeMillis();
        
        addMemory(id, category, content);
        
        String report = String.format("""
            ## 记忆已添加
            
            **ID**: %s
            **分类**: %s
            **内容**: %s
            **时间**: %s
            
            查看记忆：memory get %s
            搜索记忆：memory search <关键词>
            """, id, category, content, new Date(), id);
        
        return CommandResult.success("记忆已添加：" + id)
                .withDisplayText(report);
    }
    
    private CommandResult getMemory(String id) {
        if (id == null) {
            return CommandResult.error("请指定记忆 ID");
        }
        
        MemoryInfo memory = MEMORIES.get(id);
        if (memory == null) {
            return CommandResult.error("记忆不存在：" + id);
        }
        
        memory.accessCount++;
        
        String report = String.format("""
            ## 记忆详情：%s
            
            ### 基本信息
            
            | 属性 | 值 |
            |------|------|
            | ID | %s |
            | 分类 | %s |
            | 访问次数 | %d |
            | 创建时间 | %s |
            
            ### 内容
            
            %s
            
            ### 操作
            
            - `memory delete %s` - 删除记忆
            - `memory search <关键词>` - 搜索相关记忆
            """,
                id, memory.id, memory.category, memory.accessCount, memory.createdAt,
                memory.content, id);
        
        return CommandResult.success("记忆详情")
                .withDisplayText(report);
    }
    
    private CommandResult deleteMemory(String id) {
        if (id == null) {
            return CommandResult.error("请指定记忆 ID");
        }
        
        if (MEMORIES.remove(id) == null) {
            return CommandResult.error("记忆不存在：" + id);
        }
        
        return CommandResult.success("记忆已删除：" + id)
                .withDisplayText("✅ 记忆 `" + id + "` 已删除");
    }
    
    private CommandResult searchMemory(String query) {
        if (query == null) {
            return CommandResult.error("请指定搜索关键词");
        }
        
        List<MemoryInfo> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (MemoryInfo memory : MEMORIES.values()) {
            if (memory.content.toLowerCase().contains(lowerQuery) ||
                memory.category.toLowerCase().contains(lowerQuery)) {
                results.add(memory);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 搜索结果\n\n");
        sb.append(String.format("**关键词**: %s\n\n", query));
        
        if (results.isEmpty()) {
            sb.append("未找到匹配的记忆\n");
        } else {
            sb.append(String.format("找到 %d 条匹配的记忆\n\n", results.size()));
            for (MemoryInfo memory : results) {
                sb.append(String.format("### %s (%s)\n\n", memory.id, memory.category));
                sb.append(memory.content).append("\n\n");
                sb.append(String.format("--- 访问次数：%d | 创建时间：%s ---\n\n",
                        memory.accessCount, memory.createdAt));
            }
        }
        
        return CommandResult.success("搜索完成")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult clearMemories() {
        int count = MEMORIES.size();
        MEMORIES.clear();
        
        return CommandResult.success("记忆已清空")
                .withDisplayText(String.format("✅ 已清空 %d 条记忆", count));
    }
    
    private CommandResult memoryInfo(String id) {
        if (id == null) {
            return memoryStats();
        }
        
        return getMemory(id);
    }
    
    private CommandResult memoryStats() {
        int total = MEMORIES.size();
        int totalAccess = MEMORIES.values().stream().mapToInt(m -> m.accessCount).sum();
        
        Map<String, Long> byCategory = MEMORIES.values().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        m -> m.category,
                        java.util.stream.Collectors.counting()));
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 记忆统计\n\n");
        sb.append(String.format("**总记忆数**: %d\n", total));
        sb.append(String.format("**总访问次数**: %d\n\n", totalAccess));
        
        sb.append("### 按分类统计\n\n");
        sb.append("| 分类 | 数量 |\n");
        sb.append("|------|------|\n");
        for (Map.Entry<String, Long> entry : byCategory.entrySet()) {
            sb.append(String.format("| %s | %d |\n", entry.getKey(), entry.getValue()));
        }
        
        return CommandResult.success("记忆统计")
                .withDisplayText(sb.toString());
    }
    
    private String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen) + "...";
    }
    
    @Override
    public String getHelp() {
        return """
            命令：memory
            别名：mem, me
            描述：记忆管理
            
            用法：
              memory                  # 列出记忆
              memory list             # 列出记忆
              memory add <分类> <内容> # 添加记忆
              memory get <ID>         # 查看记忆
              memory search <关键词>  # 搜索记忆
              memory delete <ID>      # 删除记忆
              memory clear            # 清空记忆
              memory info             # 查看统计
            
            示例：
              memory add 偏好 "喜欢简洁代码"
              memory search 代码
              memory get pref-1
            """;
    }
    
    // 记忆信息类
    public static class MemoryInfo {
        public String id;
        public String category;
        public String content;
        public String createdAt;
        public int accessCount;
    }
}
