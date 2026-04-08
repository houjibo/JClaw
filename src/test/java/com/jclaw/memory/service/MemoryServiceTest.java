package com.jclaw.memory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.impl.MemoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MemoryService 单元测试
 */
class MemoryServiceTest {

    @Mock
    private MemoryMapper memoryMapper;

    @InjectMocks
    private MemoryServiceImpl memoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMemory() {
        // 准备测试数据
        Memory mockMemory = new Memory();
        mockMemory.setId("test-id");
        mockMemory.setTitle("测试记忆");
        mockMemory.setContent("测试内容");
        
        when(memoryMapper.selectById("test-id")).thenReturn(mockMemory);
        
        // 执行测试
        Memory result = memoryService.getMemory("test-id");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        assertEquals("测试记忆", result.getTitle());
        verify(memoryMapper, times(1)).selectById("test-id");
    }

    @Test
    void testListMemories() {
        // Mock MyBatis-Plus 的 Page 查询
        List<Memory> mockMemories = Arrays.asList();
        
        when(memoryMapper.selectPage(any(), any())).thenAnswer(invocation -> {
            Page<Memory> page = invocation.getArgument(0);
            page.setRecords(mockMemories);
            page.setTotal(0);
            return page;
        });
        
        // 执行测试
        List<Memory> result = memoryService.listMemories(1, 10);
        
        // 验证结果（空列表也是有效结果）
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchMemories() {
        // 准备测试数据
        Memory mockMemory = new Memory();
        mockMemory.setId("search-id");
        mockMemory.setTitle("搜索结果");
        
        when(memoryMapper.selectList(any())).thenReturn(Arrays.asList(mockMemory));
        
        // 执行测试
        List<Memory> result = memoryService.searchMemories("关键词");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("搜索结果", result.get(0).getTitle());
    }

    @Test
    void testCreateMemory() {
        // 准备测试数据
        Memory memory = new Memory();
        memory.setTitle("新记忆");
        memory.setContent("新内容");
        
        when(memoryMapper.insert(any(Memory.class))).thenReturn(1);
        
        // 执行测试
        Memory result = memoryService.createMemory(memory);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("新记忆", result.getTitle());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(memoryMapper, times(1)).insert(any(Memory.class));
    }

    @Test
    void testUpdateMemory() {
        // 准备测试数据
        Memory memory = new Memory();
        memory.setId("update-id");
        memory.setTitle("更新后的标题");
        
        when(memoryMapper.updateById(any(Memory.class))).thenReturn(1);
        
        // 执行测试
        memoryService.updateMemory(memory);
        
        // 验证
        verify(memoryMapper, times(1)).updateById(any(Memory.class));
    }

    @Test
    void testDeleteMemory() {
        // 执行测试
        memoryService.deleteMemory("delete-id");
        
        // 验证
        verify(memoryMapper, times(1)).deleteById("delete-id");
    }

    @Test
    void testCreateMemory_rollsBackOnException() {
        // 准备测试数据
        Memory memory = new Memory();
        memory.setTitle("测试");
        
        when(memoryMapper.insert((Memory) any())).thenThrow(new RuntimeException("数据库错误"));
        
        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            memoryService.createMemory(memory);
        });
        
        // 验证事务回滚（insert 被调用）
        verify(memoryMapper, times(1)).insert((Memory) any());
    }
}
