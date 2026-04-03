package com.jclaw.memory.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.memory.entity.Knowledge;
import com.jclaw.memory.service.KnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识 REST API 控制器
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeController.class);
    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * 查询知识列表
     */
    @GetMapping
    public Result<List<Knowledge>> listKnowledge(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<Knowledge> knowledge = knowledgeService.listKnowledge(page, size);
        return Result.success(knowledge);
    }

    /**
     * 搜索知识
     */
    @GetMapping("/search")
    public Result<List<Knowledge>> searchKnowledge(
        @RequestParam String query
    ) {
        List<Knowledge> knowledge = knowledgeService.searchKnowledge(query);
        return Result.success(knowledge);
    }

    /**
     * 从记忆萃取知识
     */
    @PostMapping("/extract/memory/{memoryId}")
    public Result<Knowledge> extractFromMemory(@PathVariable String memoryId) {
        Knowledge knowledge = knowledgeService.extractFromMemory(memoryId);
        if (knowledge == null) {
            return Result.error("记忆不存在");
        }
        return Result.success(knowledge);
    }

    /**
     * 创建知识
     */
    @PostMapping
    public Result<Knowledge> createKnowledge(@RequestBody Knowledge knowledge) {
        Knowledge created = knowledgeService.createKnowledge(knowledge);
        return Result.success(created);
    }

    /**
     * 更新知识
     */
    @PutMapping("/{id}")
    public Result<Void> updateKnowledge(
        @PathVariable String id,
        @RequestBody Knowledge knowledge
    ) {
        knowledge.setId(id);
        knowledgeService.updateKnowledge(knowledge);
        return Result.success();
    }

    /**
     * 删除知识
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledge(@PathVariable String id) {
        knowledgeService.deleteKnowledge(id);
        return Result.success();
    }
}
