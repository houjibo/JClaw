package com.jclaw.memory.service;

import com.jclaw.memory.entity.Knowledge;
import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.KnowledgeMapper;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.impl.KnowledgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * KnowledgeService 单元测试
 */
class KnowledgeServiceTest {

    @Mock
    private KnowledgeMapper knowledgeMapper;

    @Mock
    private MemoryMapper memoryMapper;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractFromMemory_Success() {
        // 准备测试数据
        Memory memory = new Memory();
        memory.setId("mem-001");
        memory.setTitle("测试记忆");
        memory.setContent("这是测试内容");

        when(memoryMapper.selectById("mem-001")).thenReturn(memory);
        when(knowledgeMapper.insert((Knowledge) any())).thenReturn(1);

        // 执行测试（AI 失败会降级到原始内容）
        Knowledge result = knowledgeService.extractFromMemory("mem-001");

        // 验证结果（降级后仍然成功）
        assertNotNull(result);
        assertEquals("测试记忆", result.getTitle());
        verify(memoryMapper, times(1)).selectById("mem-001");
        verify(knowledgeMapper, times(1)).insert((Knowledge) any());
    }

    @Test
    void testExtractFromMemory_NotFound() {
        // 准备测试数据
        when(memoryMapper.selectById("not-exist")).thenReturn(null);

        // 执行测试
        Knowledge result = knowledgeService.extractFromMemory("not-exist");

        // 验证结果
        assertNull(result);
        verify(memoryMapper, times(1)).selectById("not-exist");
        verify(knowledgeMapper, never()).insert((Knowledge) any());
    }

    @Test
    void testExtractFromMemory_AIFailure_Fallback() {
        // 准备测试数据
        Memory memory = new Memory();
        memory.setId("mem-002");
        memory.setTitle("测试记忆");
        memory.setContent("原始内容");

        when(memoryMapper.selectById("mem-002")).thenReturn(memory);

        // 执行测试（AI 失败会回退到原始内容）
        Knowledge result = knowledgeService.extractFromMemory("mem-002");

        // 验证结果（仍然成功，降级到原始内容）
        assertNotNull(result);
        assertEquals("测试记忆", result.getTitle());
        verify(memoryMapper, times(1)).selectById("mem-002");
    }

    @Test
    void testCreateKnowledge() {
        // 准备测试数据
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle("新知识");
        knowledge.setContent("知识内容");
        knowledge.setCategory("manual");

        when(knowledgeMapper.insert((Knowledge) any())).thenReturn(1);

        // 执行测试
        Knowledge result = knowledgeService.createKnowledge(knowledge);

        // 验证结果
        assertNotNull(result);
        assertEquals("新知识", result.getTitle());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(knowledgeMapper, times(1)).insert((Knowledge) any());
    }

    @Test
    void testListKnowledge() {
        // 准备测试数据
        when(knowledgeMapper.selectList(any())).thenReturn(java.util.Arrays.asList());

        // 执行测试
        var result = knowledgeService.listKnowledge(1, 10);

        // 验证结果
        assertNotNull(result);
        verify(knowledgeMapper, times(1)).selectList(any());
    }

    @Test
    void testSearchKnowledge() {
        // 准备测试数据
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle("搜索结果");

        when(knowledgeMapper.selectList(any())).thenReturn(java.util.Arrays.asList(knowledge));

        // 执行测试
        var result = knowledgeService.searchKnowledge("关键词");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(knowledgeMapper, times(1)).selectList(any());
    }

    @Test
    void testUpdateKnowledge() {
        // 准备测试数据
        Knowledge knowledge = new Knowledge();
        knowledge.setId("know-001");
        knowledge.setTitle("更新后的标题");

        when(knowledgeMapper.updateById(any(Knowledge.class))).thenReturn(1);

        // 执行测试
        knowledgeService.updateKnowledge(knowledge);

        // 验证
        verify(knowledgeMapper, times(1)).updateById(any(Knowledge.class));
    }

    @Test
    void testDeleteKnowledge() {
        // 执行测试
        knowledgeService.deleteKnowledge("know-001");

        // 验证
        verify(knowledgeMapper, times(1)).deleteById("know-001");
    }

    @Test
    void testExtractFromDailyLog() {
        // 准备测试数据
        Memory memory = new Memory();
        memory.setId("log-001");
        memory.setTitle("每日日志");
        memory.setContent("日志内容");

        when(memoryMapper.selectById("log-001")).thenReturn(memory);
        when(knowledgeMapper.insert((Knowledge) any())).thenReturn(1);

        // 执行测试
        Knowledge result = knowledgeService.extractFromDailyLog("log-001");

        // 验证结果（降级后仍然成功）
        assertNotNull(result);
        assertEquals("每日日志", result.getTitle());
        // extractFromDailyLog 调用 extractFromMemory，所以 selectById 被调用 2 次
        verify(memoryMapper, times(2)).selectById("log-001");
        verify(knowledgeMapper, times(1)).insert((Knowledge) any());
    }

    @Test
    void testExtractFromDailyLog_NotFound() {
        // 准备测试数据
        when(memoryMapper.selectById("not-exist")).thenReturn(null);

        // 执行测试
        Knowledge result = knowledgeService.extractFromDailyLog("not-exist");

        // 验证结果
        assertNull(result);
    }
}
