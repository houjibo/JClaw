package com.jclaw.intent.service.impl;

import com.jclaw.intent.entity.Intent;
import com.jclaw.intent.mapper.IntentMapper;
import com.jclaw.intent.service.IntentRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Intent recognize(String userInput) {
        log.info("识别意图：{}", userInput);
        
        // 1. 调用大模型 API 识别意图
        // TODO: 集成大模型 API
        Intent intent = parseIntentWithAI(userInput);
        
        // 2. 保存意图
        if (intent != null) {
            createIntent(intent);
        }
        
        return intent;
    }

    @Override
    public List<String> generateClarificationQuestions(Intent intent) {
        // TODO: 调用大模型生成澄清问题
        return List.of(
            "能详细描述一下您的需求吗？",
            "这个功能的使用场景是什么？",
            "有什么特殊的约束条件吗？"
        );
    }

    @Override
    public Intent getIntent(String id) {
        return intentMapper.selectById(id);
    }

    @Override
    public List<Intent> listIntents(int page, int size) {
        // TODO: 实现分页
        return intentMapper.selectList(null);
    }

    @Override
    @Transactional
    public Intent createIntent(Intent intent) {
        intent.setCreatedAt(Instant.now());
        intent.setUpdatedAt(Instant.now());
        intentMapper.insert(intent);
        log.info("创建意图：{} - {}", intent.getId(), intent.getName());
        return intent;
    }

    @Override
    @Transactional
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

    /**
     * AI 意图识别（待实现）
     */
    private Intent parseIntentWithAI(String userInput) {
        // TODO: 调用大模型 API
        return Intent.builder()
            .name("示例意图")
            .type("task")
            .description(userInput)
            .status("pending")
            .priority(1)
            .context(Map.of("input", userInput))
            .build();
    }
}
