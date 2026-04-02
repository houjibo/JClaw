package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 上下文管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ContextCommand extends Command {
    
    // 模拟上下文存储
    private static final List<String> CONTEXT_MESSAGES = new ArrayList<>();
    private static int maxContextSize = 100;
    
    static {
        // 添加示例上下文
        CONTEXT_MESSAGES.add("系统初始化完成");
        CONTEXT_MESSAGES.add("加载配置成功");
    }
    
    public ContextCommand() {
        this.name = "context";
        this.description = "上下文管理";
        this.aliases = Arrays.asList("ctx", "cx");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showContext();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "show", "list" -> showContext();
            case "clear" -> clearContext();
            case "add" -> addToContext(parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)) : null);
            case "remove" -> removeFromContext(parts.length > 1 ? parts[1] : null);
            case "export" -> exportContext(parts.length > 1 ? parts[1] : null);
            case "size" -> setContextSize(parts.length > 1 ? parts[1] : null);
            case "info" -> contextInfo();
            default -> showContext();
        };
    }
    
    private CommandResult showContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 上下文内容\n\n");
        
        if (CONTEXT_MESSAGES.isEmpty()) {
            sb.append("上下文为空\n");
        } else {
            sb.append(String.format("共 %d 条消息\n\n", CONTEXT_MESSAGES.size()));
            for (int i = 0; i < CONTEXT_MESSAGES.size(); i++) {
                sb.append(String.format("%d. %s\n", i + 1, CONTEXT_MESSAGES.get(i)));
            }
        }
        
        sb.append(String.format("\n最大上下文大小：%d\n", maxContextSize));
        
        return CommandResult.success("上下文内容")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult clearContext() {
        int count = CONTEXT_MESSAGES.size();
        CONTEXT_MESSAGES.clear();
        
        return CommandResult.success("上下文已清空")
                .withDisplayText(String.format("✅ 已清空 %d 条上下文消息", count));
    }
    
    private CommandResult addToContext(String message) {
        if (message == null) {
            return CommandResult.error("请指定消息内容");
        }
        
        // 如果超出最大大小，移除最早的消息
        while (CONTEXT_MESSAGES.size() >= maxContextSize) {
            CONTEXT_MESSAGES.remove(0);
        }
        
        CONTEXT_MESSAGES.add(message);
        
        return CommandResult.success("消息已添加")
                .withDisplayText(String.format("✅ 已添加消息到上下文:\n> %s", message));
    }
    
    private CommandResult removeFromContext(String index) {
        if (index == null) {
            return CommandResult.error("请指定消息序号");
        }
        
        try {
            int idx = Integer.parseInt(index) - 1;
            if (idx >= 0 && idx < CONTEXT_MESSAGES.size()) {
                String removed = CONTEXT_MESSAGES.remove(idx);
                return CommandResult.success("消息已删除")
                        .withDisplayText(String.format("✅ 已删除消息:\n> %s", removed));
            }
        } catch (NumberFormatException e) {
            // 忽略
        }
        
        return CommandResult.error("无效的消息序号：" + index);
    }
    
    private CommandResult exportContext(String format) {
        if (CONTEXT_MESSAGES.isEmpty()) {
            return CommandResult.error("上下文为空，无法导出");
        }
        
        StringBuilder sb = new StringBuilder();
        
        if ("json".equals(format)) {
            sb.append("[\n");
            for (int i = 0; i < CONTEXT_MESSAGES.size(); i++) {
                sb.append("  \"").append(CONTEXT_MESSAGES.get(i).replace("\"", "\\\"")).append("\"");
                if (i < CONTEXT_MESSAGES.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("]");
        } else {
            for (String msg : CONTEXT_MESSAGES) {
                sb.append(msg).append("\n");
            }
        }
        
        return CommandResult.success("上下文已导出")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult setContextSize(String size) {
        if (size == null) {
            return CommandResult.error("请指定大小");
        }
        
        try {
            int newSize = Integer.parseInt(size);
            if (newSize > 0 && newSize <= 1000) {
                maxContextSize = newSize;
                return CommandResult.success("上下文大小已设置")
                        .withDisplayText(String.format("✅ 最大上下文大小已设置为：%d", newSize));
            }
        } catch (NumberFormatException e) {
            // 忽略
        }
        
        return CommandResult.error("无效的大小：" + size + "（1-1000）");
    }
    
    private CommandResult contextInfo() {
        String report = String.format("""
            ## 上下文信息
            
            ### 基本统计
            
            | 属性 | 值 |
            |------|------|
            | 消息数 | %d |
            | 最大容量 | %d |
            | 使用率 | %.1f%% |
            
            ### 内存使用
            
            | 属性 | 值 |
            |------|------|
            | 估算大小 | %.2f KB |
            | 平均每消息 | %.2f KB |
            
            ### 操作建议
            
            - 消息数超过 %d 时建议清理
            - 使用 `context clear` 清空上下文
            - 使用 `context export` 导出备份
            """,
                CONTEXT_MESSAGES.size(),
                maxContextSize,
                (CONTEXT_MESSAGES.size() * 100.0) / maxContextSize,
                estimateSize() / 1024.0,
                CONTEXT_MESSAGES.isEmpty() ? 0 : estimateSize() / CONTEXT_MESSAGES.size() / 1024.0,
                maxContextSize * 8 / 10);
        
        return CommandResult.success("上下文信息")
                .withDisplayText(report);
    }
    
    private int estimateSize() {
        return CONTEXT_MESSAGES.stream().mapToInt(String::length).sum();
    }
    
    @Override
    public String getHelp() {
        return """
            命令：context
            别名：ctx, cx
            描述：上下文管理
            
            用法：
              context                 # 显示上下文
              context show            # 显示上下文
              context clear           # 清空上下文
              context add <消息>      # 添加消息
              context remove <序号>   # 删除消息
              context export [json]   # 导出上下文
              context size <大小>     # 设置最大大小
              context info            # 查看信息
            
            示例：
              context
              context add "重要信息"
              context export json
              context size 200
            """;
    }
}
