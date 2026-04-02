package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * 计划模式命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class PlanCommand extends Command {
    
    // 模拟计划存储
    private static final Map<String, PlanInfo> PLANS = new ConcurrentHashMap<>();
    
    static {
        // 添加示例计划
        addPlan("project-init", "项目初始化", "规划中");
    }
    
    private static void addPlan(String id, String title, String status) {
        PlanInfo plan = new PlanInfo();
        plan.id = id;
        plan.title = title;
        plan.status = status;
        plan.createdAt = new Date().toString();
        plan.steps = new ArrayList<>();
        PLANS.put(id, plan);
    }
    
    public PlanCommand() {
        this.name = "plan";
        this.description = "计划模式";
        this.aliases = Arrays.asList("plans", "planning");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listPlans();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> listPlans();
            case "create", "new" -> createPlan(parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)) : null);
            case "add" -> addStep(parts.length > 2 ? parts[1] : null, parts.length > 2 ? String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)) : null);
            case "start" -> startPlan(parts.length > 1 ? parts[1] : null);
            case "complete" -> completeStep(parts.length > 2 ? parts[1] : null, parts.length > 2 ? parts[2] : null);
            case "delete", "rm" -> deletePlan(parts.length > 1 ? parts[1] : null);
            case "info" -> planInfo(parts.length > 1 ? parts[1] : null);
            default -> listPlans();
        };
    }
    
    private CommandResult listPlans() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 计划列表\n\n");
        sb.append("| ID | 标题 | 状态 | 步骤 | 创建时间 |\n");
        sb.append("|-----|------|------|------|----------|\n");
        
        for (PlanInfo plan : PLANS.values()) {
            String icon = switch (plan.status) {
                case "active" -> "🟢";
                case "completed" -> "✅";
                case "planning" -> "📝";
                default -> "⚪";
            };
            sb.append(String.format("| %s | %s | %s %s | %d | %s |\n",
                    plan.id, plan.title, icon, plan.status, plan.steps.size(), plan.createdAt));
        }
        
        sb.append(String.format("\n共 %d 个计划\n", PLANS.size()));
        
        return CommandResult.success("计划列表")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult createPlan(String title) {
        if (title == null) {
            return CommandResult.error("请指定计划标题");
        }
        
        String id = title.toLowerCase().replaceAll("\\s+", "-");
        
        if (PLANS.containsKey(id)) {
            id = id + "-" + System.currentTimeMillis();
        }
        
        addPlan(id, title, "planning");
        
        String report = String.format("""
            ## 计划已创建
            
            **ID**: %s
            **标题**: %s
            **状态**: planning
            **时间**: %s
            
            添加步骤：plan add %s <步骤描述>
            开始计划：plan start %s
            查看详情：plan info %s
            """, id, title, new Date(), id, id, id);
        
        return CommandResult.success("计划已创建：" + id)
                .withDisplayText(report);
    }
    
    private CommandResult addStep(String planId, String step) {
        if (planId == null || step == null) {
            return CommandResult.error("用法：plan add <计划 ID> <步骤描述>");
        }
        
        PlanInfo plan = PLANS.get(planId);
        if (plan == null) {
            return CommandResult.error("计划不存在：" + planId);
        }
        
        plan.steps.add(new StepInfo(step, "pending"));
        
        return CommandResult.success("步骤已添加")
                .withDisplayText(String.format("✅ 已添加步骤到计划 `%s`:\n- %s", planId, step));
    }
    
    private CommandResult startPlan(String planId) {
        if (planId == null) {
            return CommandResult.error("请指定计划 ID");
        }
        
        PlanInfo plan = PLANS.get(planId);
        if (plan == null) {
            return CommandResult.error("计划不存在：" + planId);
        }
        
        plan.status = "active";
        
        String report = String.format("""
            ## 计划已启动
            
            **计划**: %s
            **标题**: %s
            **状态**: 🟢 active
            **步骤数**: %d
            
            ### 步骤列表
            
            %s
            
            完成步骤：plan complete %s <步骤序号>
            """,
                planId, plan.title, plan.steps.size(),
                formatSteps(plan.steps),
                planId);
        
        return CommandResult.success("计划已启动：" + planId)
                .withDisplayText(report);
    }
    
    private CommandResult completeStep(String planId, String stepIndex) {
        if (planId == null || stepIndex == null) {
            return CommandResult.error("用法：plan complete <计划 ID> <步骤序号>");
        }
        
        PlanInfo plan = PLANS.get(planId);
        if (plan == null) {
            return CommandResult.error("计划不存在：" + planId);
        }
        
        try {
            int index = Integer.parseInt(stepIndex) - 1;
            if (index >= 0 && index < plan.steps.size()) {
                plan.steps.get(index).status = "completed";
                return CommandResult.success("步骤已完成")
                        .withDisplayText(String.format("✅ 步骤 %d 已完成", index + 1));
            }
        } catch (NumberFormatException e) {
            // 忽略
        }
        
        return CommandResult.error("无效的步骤序号：" + stepIndex);
    }
    
    private CommandResult deletePlan(String planId) {
        if (planId == null) {
            return CommandResult.error("请指定计划 ID");
        }
        
        if (PLANS.remove(planId) == null) {
            return CommandResult.error("计划不存在：" + planId);
        }
        
        return CommandResult.success("计划已删除：" + planId)
                .withDisplayText("✅ 计划 `" + planId + "` 已删除");
    }
    
    private CommandResult planInfo(String planId) {
        if (planId == null) {
            return CommandResult.error("请指定计划 ID");
        }
        
        PlanInfo plan = PLANS.get(planId);
        if (plan == null) {
            return CommandResult.error("计划不存在：" + planId);
        }
        
        int completed = (int) plan.steps.stream().filter(s -> "completed".equals(s.status)).count();
        int total = plan.steps.size();
        
        String report = String.format("""
            ## 计划详情：%s
            
            ### 基本信息
            
            | 属性 | 值 |
            |------|------|
            | ID | %s |
            | 标题 | %s |
            | 状态 | %s |
            | 创建时间 | %s |
            | 进度 | %d/%d |
            
            ### 步骤列表
            
            %s
            
            ### 操作
            
            - `plan add %s <步骤>` - 添加步骤
            - `plan complete %s <序号>` - 完成步骤
            - `plan delete %s` - 删除计划
            """,
                planId, plan.id, plan.title, plan.status, plan.createdAt,
                completed, total,
                formatSteps(plan.steps),
                planId, planId, planId);
        
        return CommandResult.success("计划详情")
                .withDisplayText(report);
    }
    
    private String formatSteps(List<StepInfo> steps) {
        if (steps.isEmpty()) {
            return "暂无步骤";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            StepInfo step = steps.get(i);
            String icon = switch (step.status) {
                case "completed" -> "✅";
                case "active" -> "🔄";
                default -> "⏳";
            };
            sb.append(String.format("%d. %s %s\n", i + 1, icon, step.description));
        }
        return sb.toString();
    }
    
    @Override
    public String getHelp() {
        return """
            命令：plan
            别名：plans, planning
            描述：计划模式
            
            用法：
              plan                    # 列出计划
              plan create <标题>      # 创建计划
              plan add <ID> <步骤>    # 添加步骤
              plan start <ID>         # 开始计划
              plan complete <ID> <序号> # 完成步骤
              plan delete <ID>        # 删除计划
              plan info <ID>          # 查看详情
            
            示例：
              plan create "项目开发"
              plan add project-init "需求分析"
              plan start project-init
              plan complete project-init 1
            """;
    }
    
    // 计划信息类
    public static class PlanInfo {
        public String id;
        public String title;
        public String status;
        public String createdAt;
        public List<StepInfo> steps;
    }
    
    // 步骤信息类
    public static class StepInfo {
        public String description;
        public String status;
        
        public StepInfo(String description, String status) {
            this.description = description;
            this.status = status;
        }
    }
}
