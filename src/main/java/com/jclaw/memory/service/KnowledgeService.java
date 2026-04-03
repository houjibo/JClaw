package com.jclaw.memory.service;

import com.jclaw.memory.entity.Knowledge;

import java.util.List;
import java.util.Map;

/**
 * 知识萃取服务接口
 */
public interface KnowledgeService {
    
    /**
     * 从记忆萃取知识
     */
    Knowledge extractFromMemory(String memoryId);
    
    /**
     * 从每日日志萃取知识
     */
    Knowledge extractFromDailyLog(String logId);
    
    /**
     * 手动创建知识
     */
    Knowledge createKnowledge(Knowledge knowledge);
    
    /**
     * 查询知识列表
     */
    List<Knowledge> listKnowledge(int page, int size);
    
    /**
     * 搜索知识
     */
    List<Knowledge> searchKnowledge(String query);
    
    /**
     * 更新知识
     */
    void updateKnowledge(Knowledge knowledge);
    
    /**
     * 删除知识
     */
    void deleteKnowledge(String id);
}
