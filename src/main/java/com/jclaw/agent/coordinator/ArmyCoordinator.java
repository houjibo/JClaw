package com.jclaw.agent.coordinator;

import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.service.SubagentService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 联军架构协调器
 * 
 * 五大核心角色：
 * 1. PM-QA (军政官) - 需求分析 + 质量测试
 * 2. Architect (总架构师) - 技术决策、代码审查
 * 3. FullStack (全栈工程师) - 编码实现
 * 4. DevOps (运维官) - CI/CD、部署、监控
 * 5. Analyst (情报官) - 技术调研、竞品分析
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArmyCoordinator {
    
    private final SubagentService subagentService;
    
    /**
     * 联军任务执行上下文
     */
    @Data
    @Builder
    public static class ArmyContext {
        private String taskId;
        private String description;
        private List<String> requiredRoles;
        private Map<String, Subagent> agents;
        private String status;
        private String result;
    }
    
    /**
     * 五大联军角色
     */
    public enum ArmyRole {
        PM_QA("pm-qa", "需求分析 + 质量测试"),
        ARCHITECT("architect", "技术决策、代码审查"),
        FULLSTACK("fullstack", "编码实现"),
        DEVOPS("devops", "CI/CD、部署、监控"),
        ANALYST("analyst", "技术调研、竞品分析");
        
        private final String code;
        private final String description;
        
        ArmyRole(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 活跃任务上下文
     */
    private final ConcurrentHashMap<String, ArmyContext> activeContexts = new ConcurrentHashMap<>();
    
    /**
     * 创建联军任务
     * 
     * @param taskId 任务 ID
     * @param description 任务描述
     * @param roles 需要的角色列表
     * @return 任务上下文
     */
    public ArmyContext createArmyTask(String taskId, String description, List<ArmyRole> roles) {
        log.info("创建联军任务：{} - {}", taskId, description);
        
        ArmyContext context = ArmyContext.builder()
            .taskId(taskId)
            .description(description)
            .requiredRoles(roles.stream().map(ArmyRole::getCode).collect(Collectors.toList()))
            .agents(new HashMap<>())
            .status("created")
            .build();
        
        activeContexts.put(taskId, context);
        
        // 为每个角色创建 Subagent
        for (ArmyRole role : roles) {
            String subagentTask = buildRoleTask(role, description);
            Subagent subagent = subagentService.createSubagent(
                "army-" + taskId,
                role.getCode(),
                subagentTask
            );
            context.getAgents().put(role.getCode(), subagent);
            log.info("创建 {} 角色 Agent: {}", role.getCode(), subagent.getId());
        }
        
        context.setStatus("running");
        log.info("联军任务启动：{}, 角色数：{}", taskId, roles.size());
        
        return context;
    }
    
    /**
     * 等待所有角色完成
     * 
     * @param taskId 任务 ID
     * @param timeoutMs 超时时间（毫秒）
     * @return 任务上下文
     */
    public ArmyContext waitForCompletion(String taskId, long timeoutMs) {
        ArmyContext context = activeContexts.get(taskId);
        if (context == null) {
            return null;
        }
        
        log.info("等待联军任务完成：{}", taskId);
        
        long startTime = System.currentTimeMillis();
        long remaining = timeoutMs;
        
        while (remaining > 0) {
            boolean allCompleted = true;
            StringBuilder results = new StringBuilder();
            
            for (Map.Entry<String, Subagent> entry : context.getAgents().entrySet()) {
                Subagent agent = entry.getValue();
                if (!"completed".equals(agent.getStatus()) && !"failed".equals(agent.getStatus())) {
                    allCompleted = false;
                }
                if (agent.getOutput() != null) {
                    results.append("[").append(entry.getKey()).append("]: ").append(agent.getOutput()).append("\n");
                }
            }
            
            if (allCompleted) {
                context.setStatus("completed");
                context.setResult(results.toString());
                log.info("联军任务完成：{}", taskId);
                return context;
            }
            
            try {
                Thread.sleep(1000);
                remaining = timeoutMs - (System.currentTimeMillis() - startTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                context.setStatus("interrupted");
                return context;
            }
        }
        
        context.setStatus("timeout");
        log.warn("联军任务超时：{}", taskId);
        return context;
    }
    
    /**
     * 获取任务状态
     * 
     * @param taskId 任务 ID
     * @return 任务上下文
     */
    public ArmyContext getTaskStatus(String taskId) {
        return activeContexts.get(taskId);
    }
    
    /**
     * 列出所有活跃任务
     * 
     * @return 任务列表
     */
    public List<ArmyContext> listActiveTasks() {
        return new ArrayList<>(activeContexts.values());
    }
    
    /**
     * 构建角色任务提示词
     */
    private String buildRoleTask(ArmyRole role, String description) {
        return switch (role) {
            case PM_QA -> String.format(
                "你是 PM-QA（军政官），负责需求分析和质量测试。\n\n" +
                "任务描述：%s\n\n" +
                "请：\n" +
                "1. 分析需求，明确功能边界\n" +
                "2. 设计测试用例\n" +
                "3. 验收标准定义",
                description
            );
            case ARCHITECT -> String.format(
                "你是 Architect（总架构师），负责技术决策和代码审查。\n\n" +
                "任务描述：%s\n\n" +
                "请：\n" +
                "1. 设计技术方案\n" +
                "2. 审查代码质量\n" +
                "3. 确保架构合理性",
                description
            );
            case FULLSTACK -> String.format(
                "你是 FullStack（全栈工程师），负责编码实现。\n\n" +
                "任务描述：%s\n\n" +
                "请：\n" +
                "1. 编写高质量代码\n" +
                "2. 遵循编码规范\n" +
                "3. 完成单元测试",
                description
            );
            case DEVOPS -> String.format(
                "你是 DevOps（运维官），负责 CI/CD、部署和监控。\n\n" +
                "任务描述：%s\n\n" +
                "请：\n" +
                "1. 配置 CI/CD 流程\n" +
                "2. 部署到目标环境\n" +
                "3. 设置监控告警",
                description
            );
            case ANALYST -> String.format(
                "你是 Analyst（情报官），负责技术调研和竞品分析。\n\n" +
                "任务描述：%s\n\n" +
                "请：\n" +
                "1. 调研相关技术\n" +
                "2. 分析竞品方案\n" +
                "3. 提供技术建议",
                description
            );
        };
    }
    
    /**
     * 标准作战流程：完整 5 角色协作
     * 
     * @param taskId 任务 ID
     * @param description 任务描述
     * @param timeoutMs 超时时间
     * @return 任务结果
     */
    public ArmyContext standardBattle(String taskId, String description, long timeoutMs) {
        log.info("启动标准作战流程：{}", taskId);
        
        // 创建完整 5 角色联军
        List<ArmyRole> roles = Arrays.asList(
            ArmyRole.ANALYST,    // 1. 情报先行
            ArmyRole.ARCHITECT,  // 2. 架构设计
            ArmyRole.PM_QA,      // 3. 需求确认
            ArmyRole.FULLSTACK,  // 4. 编码实现
            ArmyRole.DEVOPS      // 5. 部署上线
        );
        
        ArmyContext context = createArmyTask(taskId, description, roles);
        
        // 等待所有角色完成
        return waitForCompletion(taskId, timeoutMs);
    }
}
