package com.jclaw.intent.service.impl;

import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.mapper.IntentMapper;
import com.jclaw.intent.service.IntentRecognitionService;
import com.jclaw.ai.service.AiIntentRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 意图识别服务实现
 */
@Service
@Slf4j
public class IntentRecognitionServiceImpl implements IntentRecognitionService {

    @Autowired
    private IntentMapper intentMapper;
    
    @Autowired
    private AiIntentRecognitionService aiIntentRecognitionService;

    @Override
    public Intent recognize(String userInput) {
        log.info("识别意图：{}", userInput);
        
        // 1. 使用 AI 识别意图
        Intent intent = aiIntentRecognitionService.recognizeWithAI(userInput);
        
        // 2. 保存意图
        if (intent != null) {
            createIntent(intent);
        }
        
        return intent;
    }

    @Override
    public List<String> generateClarificationQuestions(Intent intent) {
        log.info("生成澄清问题：{}", intent.getName());
        
        // 使用 AI 生成澄清问题
        return aiIntentRecognitionService.generateClarificationQuestions(intent);
    }

    @Override
    @Cacheable(value = "intents", key = "#id", unless = "#result == null")
    public Intent getIntent(String id) {
        log.debug("查询意图：{} (从数据库)", id);
        return intentMapper.selectById(id);
    }

    @Override
    public List<Intent> listIntents(int page, int size) {
        log.info("分页查询意图：page={}, size={}", page, size);
        
        // 实现分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Intent> mpPage = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Intent> result = 
            intentMapper.selectPage(
                mpPage,
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Intent>()
                    .orderByDesc("created_at")
            );
        
        return result.getRecords();
    }

    @Override
    @Transactional
    @CacheEvict(value = "intents", allEntries = true)
    public Intent createIntent(Intent intent) {
        intent.setCreatedAt(Instant.now());
        intent.setUpdatedAt(Instant.now());
        intentMapper.insert(intent);
        log.info("创建意图：{} - {}", intent.getId(), intent.getName());
        return intent;
    }

    @Override
    @Transactional
    @CacheEvict(value = "intents", key = "#intent.id")
    public void updateIntent(Intent intent) {
        intent.setUpdatedAt(Instant.now());
        intentMapper.updateById(intent);
        log.info("更新意图：{}", intent.getId());
    }

    @Override
    @Transactional
    public void deleteIntent(String id) {
        intentMapper.deleteById(id);
        log.info("删除意图：{}", id);
    }
}
