package com.jclaw.agent.service.impl;

import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.mapper.SubagentMapper;
import com.jclaw.agent.service.SubagentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Subagent 调度服务实现
 */
@Service
public class SubagentServiceImpl implements SubagentService {

    private static final Logger log = LoggerFactory.getLogger(SubagentServiceImpl.class);
    @Autowired
    private SubagentMapper subagentMapper;

    @Override
    public Subagent createSubagent(String parentAgentId, String role, String task) {
        log.info("创建 Subagent: {} - {}", role, task);
        
        Subagent subagent = Subagent.builder()
            .parentAgentId(parentAgentId)
            .role(role)
            .task(task)
            .status("pending")
            .context(null)
            .createdAt(Instant.now())
            .build();
        
        subagentMapper.insert(subagent);
        log.info("Subagent 创建成功：{}", subagent.getId());
        
        // TODO: 实际启动 Subagent 进程
        // 这里预留接口，实际可以调用 sessions_spawn
        
        return subagent;
    }

    @Override
    public Subagent getSubagent(String id) {
        return subagentMapper.selectById(id);
    }

    @Override
    public List<Subagent> listSubagents(String parentAgentId) {
        return subagentMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Subagent>()
                .eq("parent_agent_id", parentAgentId)
        );
    }

    @Override
    public void updateStatus(String id, String status) {
        Subagent subagent = subagentMapper.selectById(id);
        if (subagent != null) {
            subagent.setStatus(status);
            if ("completed".equals(status) || "failed".equals(status)) {
                subagent.setCompletedAt(Instant.now());
            }
            subagentMapper.updateById(subagent);
            log.info("Subagent 状态更新：{} -> {}", id, status);
        }
    }

    @Override
    public void submitResult(String id, String output) {
        Subagent subagent = subagentMapper.selectById(id);
        if (subagent != null) {
            subagent.setOutput(output);
            subagentMapper.updateById(subagent);
            log.info("Subagent 结果提交：{}", id);
        }
    }

    @Override
    public Subagent waitForCompletion(String id, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            Subagent subagent = subagentMapper.selectById(id);
            if (subagent == null) {
                return null;
            }
            
            if ("completed".equals(subagent.getStatus()) || "failed".equals(subagent.getStatus())) {
                return subagent;
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        return null; // 超时
    }
}
