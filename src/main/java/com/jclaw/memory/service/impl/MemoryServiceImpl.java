package com.jclaw.memory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.MemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 记忆服务实现
 */
@Service
@Slf4j
public class MemoryServiceImpl implements MemoryService {

    @Autowired
    private MemoryMapper memoryMapper;

    @Override
    public Memory getMemory(String id) {
        return memoryMapper.selectById(id);
    }

    @Override
    public List<Memory> listMemories(int page, int size) {
        Page<Memory> mpPage = new Page<>(page, size);
        Page<Memory> result = memoryMapper.selectPage(
            mpPage,
            new QueryWrapper<Memory>().orderByDesc("created_at")
        );
        return result.getRecords();
    }

    @Override
    public List<Memory> searchMemories(String query) {
        log.info("全文搜索记忆：{}", query);
        
        // 使用 PostgreSQL 全文搜索
        List<Memory> results = memoryMapper.fullTextSearch(query);
        
        if (results.isEmpty()) {
            log.warn("全文搜索无结果，回退到 LIKE 查询");
            // 降级方案：使用 LIKE 查询
            return memoryMapper.selectList(
                new QueryWrapper<Memory>()
                    .like("title", query)
                    .or()
                    .like("content", query)
            );
        }
        
        log.info("全文搜索找到 {} 条结果", results.size());
        return results;
    }

    @Override
    @Transactional
    public Memory createMemory(Memory memory) {
        memory.setCreatedAt(Instant.now());
        memory.setUpdatedAt(Instant.now());
        memoryMapper.insert(memory);
        log.info("创建记忆：{} - {}", memory.getId(), memory.getTitle());
        return memory;
    }

    @Override
    @Transactional
    public void updateMemory(Memory memory) {
        memory.setUpdatedAt(Instant.now());
        memoryMapper.updateById(memory);
        log.info("更新记忆：{}", memory.getId());
    }

    @Override
    @Transactional
    public void deleteMemory(String id) {
        memoryMapper.deleteById(id);
        log.info("删除记忆：{}", id);
    }
}
