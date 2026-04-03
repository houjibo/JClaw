package com.jclaw.memory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jclaw.memory.entity.Knowledge;
import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.KnowledgeMapper;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.KnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 知识萃取服务实现
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceImpl.class);
    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private MemoryMapper memoryMapper;

    @Override
    public Knowledge extractFromMemory(String memoryId) {
        // 1. 获取记忆
        Memory memory = memoryMapper.selectById(memoryId);
        if (memory == null) {
            return null;
        }

        // 2. AI 萃取知识（调用大模型）
        // TODO: 集成大模型 API 进行知识萃取
        String extractedContent = extractKnowledgeWithAI(memory);

        // 3. 创建知识
        Knowledge knowledge = Knowledge.builder()
            .title(memory.getTitle())
            .category("extracted")
            .content(extractedContent)
            .metadata(Map.of("source", "memory", "sourceId", memoryId))
            .tags(memory.getTags())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        return createKnowledge(knowledge);
    }

    @Override
    @Transactional
    public Knowledge createKnowledge(Knowledge knowledge) {
        knowledge.setCreatedAt(Instant.now());
        knowledge.setUpdatedAt(Instant.now());
        knowledgeMapper.insert(knowledge);
        log.info("创建知识：{} - {}", knowledge.getId(), knowledge.getTitle());
        return knowledge;
    }

    @Override
    public List<Knowledge> listKnowledge(int page, int size) {
        // TODO: 实现分页查询
        return knowledgeMapper.selectList(
            new QueryWrapper<Knowledge>().orderByDesc("created_at")
        );
    }

    @Override
    public List<Knowledge> searchKnowledge(String query) {
        // TODO: 实现 PostgreSQL 全文搜索
        return knowledgeMapper.selectList(
            new QueryWrapper<Knowledge>()
                .like("title", query)
                .or()
                .like("content", query)
        );
    }

    @Override
    @Transactional
    public void updateKnowledge(Knowledge knowledge) {
        knowledge.setUpdatedAt(Instant.now());
        knowledgeMapper.updateById(knowledge);
        log.info("更新知识：{}", knowledge.getId());
    }

    @Override
    @Transactional
    public void deleteKnowledge(String id) {
        knowledgeMapper.deleteById(id);
        log.info("删除知识：{}", id);
    }

    @Override
    public Knowledge extractFromDailyLog(String logId) {
        // TODO: 实现从日志萃取知识
        return null;
    }

    /**
     * AI 知识萃取（辅助方法）
     */
    private String extractKnowledgeWithAI(Memory memory) {
        // TODO: 调用大模型 API 萃取知识
        return memory.getContent().toString();
    }
}
