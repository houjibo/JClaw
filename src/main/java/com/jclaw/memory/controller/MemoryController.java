package com.jclaw.memory.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.service.MemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 记忆 REST API 控制器
 */
@RestController
@RequestMapping("/api/memories")
@Slf4j
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    /**
     * 查询记忆列表
     */
    @GetMapping
    public Result<List<Memory>> listMemories(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<Memory> memories = memoryService.listMemories(page, size);
        return Result.success(memories);
    }

    /**
     * 获取记忆详情
     */
    @GetMapping("/{id}")
    public Result<Memory> getMemory(@PathVariable String id) {
        Memory memory = memoryService.getMemory(id);
        if (memory == null) {
            return Result.error("记忆不存在");
        }
        return Result.success(memory);
    }

    /**
     * 搜索记忆
     */
    @GetMapping("/search")
    public Result<List<Memory>> searchMemories(
        @RequestParam String query
    ) {
        List<Memory> memories = memoryService.searchMemories(query);
        return Result.success(memories);
    }

    /**
     * 创建记忆
     */
    @PostMapping
    public Result<Memory> createMemory(@RequestBody Memory memory) {
        Memory created = memoryService.createMemory(memory);
        return Result.success(created);
    }

    /**
     * 更新记忆
     */
    @PutMapping("/{id}")
    public Result<Void> updateMemory(
        @PathVariable String id,
        @RequestBody Memory memory
    ) {
        memory.setId(id);
        memoryService.updateMemory(memory);
        return Result.success();
    }

    /**
     * 删除记忆
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMemory(@PathVariable String id) {
        memoryService.deleteMemory(id);
        return Result.success();
    }
}
