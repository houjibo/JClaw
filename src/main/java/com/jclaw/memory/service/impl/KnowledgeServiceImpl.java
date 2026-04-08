package com.jclaw.memory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jclaw.memory.entity.Knowledge;
import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.KnowledgeMapper;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.KnowledgeService;
import com.jclaw.ai.service.AiIntentRecognitionService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private MemoryMapper memoryMapper;
    
    @Autowired
    private AiIntentRecognitionService aiIntentRecognitionService;

    @Override
    public Knowledge extractFromMemory(String memoryId) {
        log.info("从记忆萃取知识：{}", memoryId);
        
        // 1. 获取记忆
        Memory memory = memoryMapper.selectById(memoryId);
        if (memory == null) {
            log.warn("记忆不存在：{}", memoryId);
            return null;
        }

        // 2. AI 萃取知识
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
        log.info("分页查询知识：page={}, size={}", page, size);
        
        // 实现分页查询
        QueryWrapper<Knowledge> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        wrapper.last("LIMIT " + ((page - 1) * size) + ", " + size);
        
        return knowledgeMapper.selectList(wrapper);
    }

    @Override
    public List<Knowledge> searchKnowledge(String query) {
        log.info("搜索知识：{}", query);
        
        // 使用 LIKE 查询（可以升级为全文搜索）
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
        log.info("从每日日志萃取知识：{}", logId);
        
        // 从 Memory 中查找日志
        Memory memory = memoryMapper.selectById(logId);
        if (memory == null) {
            log.warn("日志不存在：{}", logId);
            return null;
        }
        
        // 复用从记忆萃取知识的逻辑
        return extractFromMemory(logId);
    }

    /**
     * AI 知识萃取（辅助方法）
     */
    private String extractKnowledgeWithAI(Memory memory) {
        log.info("AI 知识萃取：{}", memory.getTitle());
        
        // 构建提示词
        String prompt = String.format("""
            请从以下记忆中萃取核心知识点：
            
            标题：%s
            内容：%s
            
            请萃取：
            1. 核心概念
            2. 关键结论
            3. 可复用的经验
            
            返回结构化的知识内容（Markdown 格式）。
            """, memory.getTitle(), memory.getContent());
        
        try {
            // 调用 AI 萃取知识
            String response = aiIntentRecognitionService.callLLM(prompt);
            return response != null ? response : memory.getContent().toString();
        } catch (Exception e) {
            log.error("AI 知识萃取失败，使用原始内容", e);
            return memory.getContent().toString();
        }
    }
}
