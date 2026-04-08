package com.jclaw.intent.service.impl;

import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.service.TaskDecompositionService;
import com.jclaw.ai.service.AiTaskDecompositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务分解服务实现
 */
@Service
@Slf4j
public class TaskDecompositionServiceImpl implements TaskDecompositionService {

    @Autowired
    private AiTaskDecompositionService aiTaskDecompositionService;

    @Override
    public List<Map<String, Object>> decompose(Intent intent) {
        log.info("分解意图为任务：{}", intent.getName());
        
        // 优先使用 AI 分解
        try {
            List<Map<String, Object>> tasks = aiTaskDecompositionService.decomposeWithAI(intent);
            if (tasks != null && !tasks.isEmpty()) {
                log.info("AI 分解成功：{} 个任务", tasks.size());
                return tasks;
            }
        } catch (Exception e) {
            log.warn("AI 分解失败，使用规则分解", e);
        }
        
        // 降级方案：规则分解
        return decomposeWithDefault(intent);
    }

    @Override
    public int estimateComplexity(Map<String, Object> task) {
        // 优先使用 AI 评估
        try {
            return aiTaskDecompositionService.estimateComplexityWithAI(task);
        } catch (Exception e) {
            log.warn("AI 评估失败，使用默认算法", e);
        }
        
        // 降级方案：默认算法
        return estimateComplexityWithDefault(task);
    }

    @Override
    public String assignAgent(Map<String, Object> task) {
        // 优先使用 AI 分配
        try {
            return aiTaskDecompositionService.assignAgentWithAI(task);
        } catch (Exception e) {
            log.warn("AI 分配失败，使用规则分配", e);
        }
        
        // 降级方案：规则分配
        return assignAgentWithDefault(task);
    }

    /**
     * 默认任务分解（降级方案）
     */
    private List<Map<String, Object>> decomposeWithDefault(Intent intent) {
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        tasks.add(Map.of(
            "title", "设计数据库 schema",
            "description", "设计相关数据库表结构",
            "type", "design",
            "priority", 1,
            "agent", "architect"
        ));
        
        tasks.add(Map.of(
            "title", "实现核心功能",
            "description", "实现主要业务逻辑",
            "type", "coding",
            "priority", 2,
            "agent", "fullstack"
        ));
        
        tasks.add(Map.of(
            "title", "编写测试用例",
            "description", "编写单元测试和集成测试",
            "type", "testing",
            "priority", 3,
            "agent", "qa"
        ));
        
        return tasks;
    }

    /**
     * 默认复杂度评估（降级方案）
     */
    private int estimateComplexityWithDefault(Map<String, Object> task) {
        String description = (String) task.getOrDefault("description", "");
        int baseScore = description.length() / 50;
        
        String type = (String) task.getOrDefault("type", "general");
        int typeMultiplier = switch (type) {
            case "coding", "deploy" -> 2;
            default -> 1;
        };
        
        return baseScore * typeMultiplier;
    }

    /**
     * 默认 Agent 分配（降级方案）
     */
    private String assignAgentWithDefault(Map<String, Object> task) {
        String type = (String) task.getOrDefault("type", "general");
        
        return switch (type) {
            case "coding", "api", "frontend" -> "fullstack";
            case "design", "architecture" -> "architect";
            case "testing", "qa" -> "qa";
            case "deploy", "devops" -> "devops";
            case "analysis", "research" -> "analyst";
            default -> "fullstack";
        };
    }
}
