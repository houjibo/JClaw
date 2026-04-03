package com.jclaw.intent.service;

import com.jclaw.intent.entity.Intent;

import java.util.List;
import java.util.Map;

/**
 * 任务分解服务接口
 */
public interface TaskDecompositionService {
    
    /**
     * 分解意图为任务
     */
    List<Map<String, Object>> decompose(Intent intent);
    
    /**
     * 评估任务复杂度
     */
    int estimateComplexity(Map<String, Object> task);
    
    /**
     * 分配 Agent 角色
     */
    String assignAgent(Map<String, Object> task);
}
