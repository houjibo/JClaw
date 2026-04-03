package com.jclaw.memory.service;

import com.jclaw.memory.entity.Memory;

import java.util.List;

/**
 * 记忆服务接口
 */
public interface MemoryService {
    
    /**
     * 获取记忆详情
     */
    Memory getMemory(String id);
    
    /**
     * 查询记忆列表
     */
    List<Memory> listMemories(int page, int size);
    
    /**
     * 搜索记忆
     */
    List<Memory> searchMemories(String query);
    
    /**
     * 创建记忆
     */
    Memory createMemory(Memory memory);
    
    /**
     * 更新记忆
     */
    void updateMemory(Memory memory);
    
    /**
     * 删除记忆
     */
    void deleteMemory(String id);
}
