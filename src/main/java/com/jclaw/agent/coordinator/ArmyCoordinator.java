package com.jclaw.agent.coordinator;

import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.service.SubagentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联军协调器 - 5 大 Agent 角色调度
 */
@Component
public class ArmyCoordinator {

    private static final Logger log = LoggerFactory.getLogger(ArmyCoordinator.class);
    @Autowired
    private SubagentService subagentService;

    /**
     * 5 大核心角色
     */
    public static final String[] ROLES = {
        "pm-qa",      // 军政官 - 需求分析 + 质量测试
        "architect",  // 总架构师 - 技术决策
        "fullstack",  // 全栈工程师 - 编码实现
        "devops",     // 运维官 - CI/CD + 部署
        "analyst"     // 情报官 - 技术调研
    };

    /**
     * 创建联军团队
     */
    public List<Subagent> createArmy(String parentAgentId, String mission) {
        log.info("创建联军团队执行任务：{}", mission);
        
        List<Subagent> team = new ArrayList<>();
        
        // 分解任务并分配给各角色
        Map<String, String> taskDecomposition = decomposeMission(mission);
        
        for (String role : ROLES) {
            String task = taskDecomposition.getOrDefault(role, "支持任务执行");
            Subagent subagent = subagentService.createSubagent(parentAgentId, role, task);
            team.add(subagent);
        }
        
        log.info("联军团队创建完成，共 {} 个成员", team.size());
        return team;
    }

    /**
     * 等待所有成员完成
     */
    public boolean waitForArmy(List<Subagent> team, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        
        for (Subagent subagent : team) {
            long remaining = timeoutMs - (System.currentTimeMillis() - startTime);
            if (remaining <= 0) {
                log.error("等待联军完成超时");
                return false;
            }
            
            Subagent result = subagentService.waitForCompletion(subagent.getId(), remaining);
            if (result == null || "failed".equals(result.getStatus())) {
                log.error("联军成员 {} 执行失败", subagent.getRole());
                return false;
            }
        }
        
        log.info("联军任务完成");
        return true;
    }

    /**
     * 汇总所有成员结果
     */
    public String collectResults(List<Subagent> team) {
        StringBuilder results = new StringBuilder();
        results.append("=== 联军任务结果 ===\n\n");
        
        for (Subagent subagent : team) {
            results.append(String.format("【%s】%s\n输出：%s\n\n",
                subagent.getRole(),
                subagent.getTask(),
                subagent.getOutput()
            ));
        }
        
        return results.toString();
    }

    /**
     * 任务分解
     */
    private Map<String, String> decomposeMission(String mission) {
        // TODO: 调用大模型进行任务分解
        // 当前简化实现
        
        return Map.of(
            "pm-qa", "分析需求：" + mission + "，编写测试用例",
            "architect", "设计技术架构和数据库 schema",
            "fullstack", "实现核心功能和 API",
            "devops", "配置 CI/CD 和部署脚本",
            "analyst", "调研相关技术和最佳实践"
        );
    }
}
