package com.jclaw.memory.service;

import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.impl.MemoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MemoryService 全文搜索测试
 */
class MemoryServiceFullTextSearchTest {

    @Mock
    private MemoryMapper memoryMapper;

    @InjectMocks
    private MemoryServiceImpl memoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchMemories_FullTextSearch_Success() {
        // 准备测试数据
        Memory memory1 = new Memory();
        memory1.setId("fts-1");
        memory1.setTitle("人工智能研究");
        memory1.setContent("关于深度学习和机器学习的研究报告");
        
        Memory memory2 = new Memory();
        memory2.setId("fts-2");
        memory2.setTitle("AI 应用案例");
        memory2.setContent("人工智能在医疗领域的应用");
        
        List<Memory> searchResults = Arrays.asList(memory1, memory2);
        
        // Mock 全文搜索返回结果
        when(memoryMapper.fullTextSearch("人工智能")).thenReturn(searchResults);
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("人工智能");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getTitle().contains("人工智能") || 
                   result.get(0).getContent().contains("人工智能"));
        
        // 验证调用了全文搜索
        verify(memoryMapper, times(1)).fullTextSearch("人工智能");
    }

    @Test
    void testSearchMemories_FullTextSearch_Empty_Fallback() {
        // 准备测试数据（全文搜索无结果）
        when(memoryMapper.fullTextSearch("不存在的关键词")).thenReturn(Arrays.asList());
        
        // Mock LIKE 查询返回结果（降级方案）
        Memory fallbackMemory = new Memory();
        fallbackMemory.setId("fallback-1");
        fallbackMemory.setTitle("包含关键词的标题");
        
        when(memoryMapper.selectList(any())).thenReturn(Arrays.asList(fallbackMemory));
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("不存在的关键词");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("fallback-1", result.get(0).getId());
        
        // 验证调用了全文搜索和 LIKE 查询（降级）
        verify(memoryMapper, times(1)).fullTextSearch("不存在的关键词");
        verify(memoryMapper, times(1)).selectList(any());
    }

    @Test
    void testSearchMemories_FullTextSearch_WithResults_NoFallback() {
        // 准备测试数据（全文搜索有结果）
        Memory memory = new Memory();
        memory.setId("result-1");
        memory.setTitle("搜索结果");
        
        when(memoryMapper.fullTextSearch("测试")).thenReturn(Arrays.asList(memory));
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("测试");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("result-1", result.get(0).getId());
        
        // 验证只调用了全文搜索，没有调用 LIKE 查询
        verify(memoryMapper, times(1)).fullTextSearch("测试");
        verify(memoryMapper, never()).selectList(any());
    }

    @Test
    void testSearchMemories_EmptyQuery() {
        // 执行测试
        List<Memory> result = memoryService.searchMemories("");
        
        // 验证调用了全文搜索（即使查询为空）
        verify(memoryMapper, times(1)).fullTextSearch("");
    }

    @Test
    void testSearchMemories_SpecialCharacters() {
        // 测试特殊字符处理
        Memory memory = new Memory();
        memory.setId("special-1");
        memory.setTitle("Java & Spring");
        
        when(memoryMapper.fullTextSearch("Java & Spring")).thenReturn(Arrays.asList(memory));
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("Java & Spring");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchMemories_ChineseCharacters() {
        // 测试中文字符搜索
        Memory memory1 = new Memory();
        memory1.setId("chinese-1");
        memory1.setTitle("人工智能");
        
        Memory memory2 = new Memory();
        memory2.setId("chinese-2");
        memory2.setTitle("机器学习");
        
        when(memoryMapper.fullTextSearch("深度学习")).thenReturn(Arrays.asList(memory1, memory2));
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("深度学习");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchMemories_CaseInsensitive() {
        // 测试大小写不敏感
        Memory memory = new Memory();
        memory.setId("case-1");
        memory.setTitle("AI Research");
        
        when(memoryMapper.fullTextSearch("ai")).thenReturn(Arrays.asList(memory));
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("ai");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
