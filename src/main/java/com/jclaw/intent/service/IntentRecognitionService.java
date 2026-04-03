package com.jclaw.intent.service;

import com.jclaw.intent.entity.Intent;

import java.util.List;

/**
 * 意图识别服务接口
 */
public interface IntentRecognitionService {
    
    /**
     * 识别意图
     */
    Intent recognize(String userInput);
    
    /**
     * AI 澄清对话
     */
    List<String> generateClarificationQuestions(Intent intent);
    
    /**
     * 获取意图详情
     */
    Intent getIntent(String id);
    
    /**
     * 查询意图列表
     */
    List<Intent> listIntents(int page, int size);
    
    /**
     * 创建意图
     */
    Intent createIntent(Intent intent);
    
    /**
     * 更新意图
     */
    void updateIntent(Intent intent);
    
    /**
     * 删除意图
     */
    void deleteIntent(String id);
}
