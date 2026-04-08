package com.jclaw.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.mapper.SubagentMapper;
import com.jclaw.agent.service.SubagentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Subagent 调度服务实现
 * 集成 OpenClaw sessions_spawn API
 */
@Service
@Slf4j
public class SubagentServiceImpl implements SubagentService {

    @Autowired
    private SubagentMapper subagentMapper;
    
    @Value("${openclaw.api.url:http://localhost:18789}")
    private String openclawApiUrl;
    
    @Value("${openclaw.api.token:}")
    private String openclawApiToken;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Subagent createSubagent(String parentAgentId, String role, String task) {
        log.info("创建 Subagent: {} - {}", role, task);
        
        // 1. 创建 Subagent 记录到数据库
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
        
        // 2. 实际调用 OpenClaw sessions_spawn API
        try {
            boolean started = startOpenclawSubagent(subagent);
            if (started) {
                updateStatus(subagent.getId(), "running");
                log.info("Subagent 已启动：{}", subagent.getId());
            } else {
                log.warn("Subagent 启动失败，状态保持 pending：{}", subagent.getId());
            }
        } catch (Exception e) {
            log.error("Subagent 启动失败：{}", subagent.getId(), e);
        }
        
        return subagent;
    }

    @Override
    public Subagent getSubagent(String id) {
        return subagentMapper.selectById(id);
    }

    @Override
    public List<Subagent> listSubagents(String parentAgentId) {
        return subagentMapper.selectList(
            new QueryWrapper<Subagent>()
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
        
        return null;
    }
    
    /**
     * 调用 OpenClaw sessions_spawn API 启动 Subagent
     */
    private boolean startOpenclawSubagent(Subagent subagent) {
        String url = openclawApiUrl + "/api/sessions/spawn";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("runtime", "subagent");
        requestBody.put("task", buildTaskPrompt(subagent));
        requestBody.put("model", getModelForRole(subagent.getRole()));
        requestBody.put("timeoutSeconds", 3600);
        requestBody.put("mode", "run");
        requestBody.put("cleanup", "delete");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (openclawApiToken != null && !openclawApiToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + openclawApiToken);
        }
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("OpenClaw Subagent 启动成功：{}", subagent.getId());
                return true;
            } else {
                log.warn("OpenClaw Subagent 启动失败：{} - {}", subagent.getId(), response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("调用 OpenClaw API 失败：{}", subagent.getId(), e);
            return false;
        }
    }
    
    /**
     * 根据角色选择模型
     */
    private String getModelForRole(String role) {
        return switch (role.toLowerCase()) {
            case "pm-qa", "qa" -> "modelstudio/kimi-k2.5";
            case "architect" -> "modelstudio/qwen3.5-plus";
            case "fullstack" -> "modelstudio/qwen3-coder-plus";
            case "devops" -> "modelstudio/qwen3.5-plus";
            case "analyst" -> "modelstudio/kimi-k2.5";
            default -> "modelstudio/qwen3.5-plus";
        };
    }
    
    /**
     * 构建任务提示词
     */
    private String buildTaskPrompt(Subagent subagent) {
        return String.format(
            "你是 JClaw 系统中的 %s 角色 Agent.\n\n" +
            "任务：%s\n\n" +
            "请完成这个任务，并将结果返回.",
            subagent.getRole(), subagent.getTask());
    }
}
