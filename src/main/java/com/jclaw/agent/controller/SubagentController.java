package com.jclaw.agent.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.agent.entity.Subagent;
import com.jclaw.agent.service.SubagentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Subagent REST API 控制器
 */
@RestController
@RequestMapping("/api/subagents")
@Slf4j
public class SubagentController {

    @Autowired
    private SubagentService subagentService;

    /**
     * 创建 Subagent
     */
    @PostMapping
    public Result<Subagent> createSubagent(@RequestBody Map<String, String> request) {
        String parentAgentId = request.get("parentAgentId");
        String role = request.get("role");
        String task = request.get("task");
        
        Subagent subagent = subagentService.createSubagent(parentAgentId, role, task);
        return Result.success(subagent);
    }

    /**
     * 获取 Subagent 状态
     */
    @GetMapping("/{id}")
    public Result<Subagent> getSubagent(@PathVariable String id) {
        Subagent subagent = subagentService.getSubagent(id);
        if (subagent == null) {
            return Result.error("Subagent 不存在");
        }
        return Result.success(subagent);
    }

    /**
     * 列出 Subagent
     */
    @GetMapping
    public Result<List<Subagent>> listSubagents(
        @RequestParam(required = false) String parentAgentId
    ) {
        List<Subagent> subagents = subagentService.listSubagents(parentAgentId);
        return Result.success(subagents);
    }

    /**
     * 更新状态
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(
        @PathVariable String id,
        @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        subagentService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 提交结果
     */
    @PostMapping("/{id}/result")
    public Result<Void> submitResult(
        @PathVariable String id,
        @RequestBody Map<String, String> request
    ) {
        String output = request.get("output");
        subagentService.submitResult(id, output);
        return Result.success();
    }

    /**
     * 等待完成
     */
    @PostMapping("/{id}/wait")
    public Result<Subagent> waitForCompletion(
        @PathVariable String id,
        @RequestParam(defaultValue = "300000") long timeoutMs
    ) {
        Subagent subagent = subagentService.waitForCompletion(id, timeoutMs);
        if (subagent == null) {
            return Result.error("等待超时");
        }
        return Result.success(subagent);
    }
}
