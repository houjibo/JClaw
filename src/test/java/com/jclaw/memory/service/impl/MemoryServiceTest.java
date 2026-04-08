package com.jclaw.memory.service.impl;

import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.mapper.MemoryMapper;
import com.jclaw.memory.service.MemoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 记忆服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class MemoryServiceTest {

    @Mock
    private MemoryMapper memoryMapper;

    @InjectMocks
    private MemoryServiceImpl memoryService;

    @Test
    void testGetMemory() {
        // Arrange
        Memory mockMemory = Memory.builder()
            .id("mem_001")
            .type("long_term")
            .title("测试记忆")
            .content("{\"key\":\"value\"}")
            .build();
        when(memoryMapper.selectById("mem_001")).thenReturn(mockMemory);

        // Act
        Memory result = memoryService.getMemory("mem_001");

        // Assert
        assertNotNull(result);
        assertEquals("mem_001", result.getId());
        assertEquals("测试记忆", result.getTitle());
    }

    @Test
    void testCreateMemory() {
        // Arrange
        Memory mockMemory = Memory.builder()
            .type("long_term")
            .title("新记忆")
            .content("{\"key\":\"value\"}")
            .build();
        when(memoryMapper.insert(any(Memory.class))).thenReturn(1);

        // Act
        Memory result = memoryService.createMemory(mockMemory);

        // Assert
        assertNotNull(result);
        assertEquals("新记忆", result.getTitle());
        verify(memoryMapper, times(1)).insert(any(Memory.class));
    }

    @Test
    void testDeleteMemory() {
        // Arrange
        when(memoryMapper.deleteById("mem_001")).thenReturn(1);

        // Act
        memoryService.deleteMemory("mem_001");

        // Assert
        verify(memoryMapper, times(1)).deleteById("mem_001");
    }
}
