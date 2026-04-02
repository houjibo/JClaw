package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 显示 API 调用成本统计
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CostCommand extends Command {
    
    // 模拟成本数据（实际应连接成本追踪服务）
    private static final List<Map<String, Object>> API_CALLS = new ArrayList<>();
    private static double totalCost = 0.0;
    
    static {
        // 添加一些模拟数据
        addMockCall("qwen3.5-plus", 1000, 500, 0.002);
        addMockCall("glm-4", 2000, 800, 0.005);
        addMockCall("kimi-k2.5", 1500, 600, 0.003);
    }
    
    private static void addMockCall(String model, int inputTokens, int outputTokens, double cost) {
        Map<String, Object> call = new HashMap<>();
        call.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        call.put("model", model);
        call.put("inputTokens", inputTokens);
        call.put("outputTokens", outputTokens);
        call.put("cost", cost);
        API_CALLS.add(call);
        totalCost += cost;
    }
    
    public CostCommand() {
        this.name = "cost";
        this.description = "查看 API 调用成本统计";
        this.aliases = Arrays.asList("costs", "expense");
        this.category = CommandCategory.COST;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("period", new CommandParameter("period", "统计周期 (today, week, month, all)", false)
                .choices("today", "week", "month", "all").defaultValue("all"));
        
        this.parameters.put("detail", new CommandParameter("detail", "是否显示详细信息", false)
                .choices("true", "false").defaultValue("false"));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        // 解析参数
        String period = "all";
        boolean detail = false;
        
        if (args != null && !args.isBlank()) {
            String[] parts = args.trim().split("\\s+");
            for (String part : parts) {
                if (part.startsWith("--period=")) {
                    period = part.substring(9);
                } else if (part.startsWith("--detail")) {
                    detail = true;
                } else if (List.of("today", "week", "month", "all").contains(part)) {
                    period = part;
                }
            }
        }
        
        // 生成统计报告
        Map<String, Object> stats = generateStats(period);
        
        StringBuilder sb = new StringBuilder();
        sb.append("## API 成本统计 (").append(getPeriodName(period)).append(")\n\n");
        sb.append("### 总览\n");
        sb.append(String.format("- **总调用次数**: %d\n", stats.get("totalCalls")));
        sb.append(String.format("- **总输入 Token**: %,d\n", stats.get("totalInputTokens")));
        sb.append(String.format("- **总输出 Token**: %,d\n", stats.get("totalOutputTokens")));
        sb.append(String.format("- **总成本**: $%.4f (¥%.2f)\n\n", 
                stats.get("totalCost"), (Double) stats.get("totalCost") * 7.2));
        
        // 按模型统计
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> byModel = (Map<String, Map<String, Object>>) stats.get("byModel");
        if (byModel != null && !byModel.isEmpty()) {
            sb.append("### 按模型统计\n\n");
            sb.append("| 模型 | 调用次数 | Token | 成本 |\n");
            sb.append("|------|---------|-------|------|\n");
            
            for (Map.Entry<String, Map<String, Object>> entry : byModel.entrySet()) {
                Map<String, Object> modelStats = entry.getValue();
                sb.append(String.format("| %s | %d | %,d | $%.4f |\n",
                        entry.getKey(),
                        modelStats.get("calls"),
                        (Integer) modelStats.get("inputTokens") + (Integer) modelStats.get("outputTokens"),
                        (Double) modelStats.get("cost")));
            }
        }
        
        // 详细信息
        if (detail) {
            sb.append("\n### 详细调用记录\n\n");
            sb.append("```\n");
            for (Map<String, Object> call : API_CALLS) {
                sb.append(String.format("[%s] %s: %d→%d tokens, $%.4f\n",
                        call.get("timestamp"),
                        call.get("model"),
                        call.get("inputTokens"),
                        call.get("outputTokens"),
                        call.get("cost")));
            }
            sb.append("```");
        }
        
        return CommandResult.success("成本统计完成")
                .withData("stats", stats)
                .withDisplayText(sb.toString());
    }
    
    /**
     * 生成统计数据
     */
    private Map<String, Object> generateStats(String period) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("period", period);
        stats.put("totalCalls", API_CALLS.size());
        stats.put("totalInputTokens", API_CALLS.stream().mapToInt(c -> (Integer) c.get("inputTokens")).sum());
        stats.put("totalOutputTokens", API_CALLS.stream().mapToInt(c -> (Integer) c.get("outputTokens")).sum());
        stats.put("totalCost", totalCost);
        
        // 按模型分组
        Map<String, Map<String, Object>> byModel = new HashMap<>();
        for (Map<String, Object> call : API_CALLS) {
            String model = (String) call.get("model");
            byModel.computeIfAbsent(model, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("calls", 0);
                m.put("inputTokens", 0);
                m.put("outputTokens", 0);
                m.put("cost", 0.0);
                return m;
            });
            
            Map<String, Object> modelStats = byModel.get(model);
            modelStats.put("calls", (Integer) modelStats.get("calls") + 1);
            modelStats.put("inputTokens", (Integer) modelStats.get("inputTokens") + (Integer) call.get("inputTokens"));
            modelStats.put("outputTokens", (Integer) modelStats.get("outputTokens") + (Integer) call.get("outputTokens"));
            modelStats.put("cost", (Double) modelStats.get("cost") + (Double) call.get("cost"));
        }
        
        stats.put("byModel", byModel);
        return stats;
    }
    
    /**
     * 获取周期名称
     */
    private String getPeriodName(String period) {
        return switch (period) {
            case "today" -> "今日";
            case "week" -> "本周";
            case "month" -> "本月";
            default -> "全部";
        };
    }
    
    @Override
    public String getHelp() {
        return """
            命令：cost
            别名：costs, expense
            描述：查看 API 调用成本统计
            
            用法：
              cost [period] [--detail]
            
            参数：
              period    统计周期 (today, week, month, all)，默认 all
              --detail  显示详细调用记录
            
            示例：
              cost                      # 查看全部统计
              cost today                # 查看今日统计
              cost week --detail        # 查看本周详细记录
              cost month                # 查看本月统计
            """;
    }
}
