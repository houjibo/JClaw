package com.jclaw.agent.service;

import com.jclaw.agent.entity.Subagent;

import java.util.List;

/**
 * Subagent 调度服务接口
 */
public interface SubagentService {
    
    /**
     * 创建 Subagent
     */
    Subagent createSubagent(String parentAgentId, String role, String task);
    
    /**
     * 获取 Subagent 状态
     */
    Subagent getSubagent(String id);
    
    /**
     * 列出所有 Subagent
     */
    List<Subagent> listSubagents(String parentAgentId);
    
    /**
     * 更新 Subagent 状态
     */
    void updateStatus(String id, String status);
    
    /**
     * 提交 Subagent 结果
     */
    void submitResult(String id, String output);
    
    /**
     * 等待 Subagent 完成
     */
    Subagent waitForCompletion(String id, long timeoutMs);
}
