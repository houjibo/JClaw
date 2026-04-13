package com.jclaw.agent.service.impl;

import com.jclaw.agent.dto.AgentDTO;
import com.jclaw.agent.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AgentServiceImpl implements AgentService {
    
    private static final Map<String, AgentDTO> AGENTS = new ConcurrentHashMap<>();
    
    @Override
    public AgentDTO createAgent(AgentDTO agent) {
        String agentId = "agent-" + UUID.randomUUID().toString().substring(0, 6);
        agent.setId(agentId);
        agent.setStatus("idle");
        agent.setCreatedAt(LocalDateTime.now());
        
        AGENTS.put(agentId, agent);
        log.info("创建 Agent: {} ({})", agentId, agent.getName());
        return agent;
    }
    
    @Override
    public List<AgentDTO> listAgents() {
        return new ArrayList<>(AGENTS.values());
    }
    
    @Override
    public void sendMessage(String agentId, String message) {
        AgentDTO agent = AGENTS.get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent 不存在：" + agentId);
        }
        agent.setStatus("busy");
        log.info("发送消息给 Agent {}: {}", agentId, message);
        // TODO: 实际实现消息处理
    }
    
    @Override
    public void stopAgent(String agentId) {
        AgentDTO agent = AGENTS.get(agentId);
        if (agent != null) {
            agent.setStatus("stopped");
            log.info("停止 Agent: {}", agentId);
        }
    }
}
