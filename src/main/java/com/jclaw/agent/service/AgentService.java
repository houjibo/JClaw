package com.jclaw.agent.service;

import com.jclaw.agent.dto.AgentDTO;

import java.util.List;

/**
 * Agent 管理服务接口
 */
public interface AgentService {
    
    /**
     * 创建 Agent
     */
    AgentDTO createAgent(AgentDTO agent);
    
    /**
     * 获取 Agent 列表
     */
    List<AgentDTO> listAgents();
    
    /**
     * 发送消息给 Agent
     */
    void sendMessage(String agentId, String message);
    
    /**
     * 停止 Agent
     */
    void stopAgent(String agentId);
}
