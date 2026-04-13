package com.jclaw.memory.controller;

import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.service.MemoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 记忆控制器测试
 */
@DisplayName("记忆控制器测试")
class MemoryControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private MemoryService memoryService;
    
    @InjectMocks
    private MemoryController memoryController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(memoryController).build();
    }
    
    @Test
    @DisplayName("测试查询记忆列表")
    void testListMemories() throws Exception {
        // Arrange
        Memory memory1 = Memory.builder()
            .id("mem-1")
            .type("long_term")
            .title("记忆 1")
            .content("内容 1")
            .tags(new String[]{"重要", "学习"})
            .createdAt(Instant.now())
            .build();
        
        Memory memory2 = Memory.builder()
            .id("mem-2")
            .type("daily_log")
            .title("记忆 2")
            .content("内容 2")
            .tags(new String[]{"日常"})
            .createdAt(Instant.now())
            .build();
        
        when(memoryService.listMemories(1, 20)).thenReturn(Arrays.asList(memory1, memory2));
        
        // Act & Assert
        mockMvc.perform(get("/api/memories")
                .param("page", "1")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("mem-1"))
            .andExpect(jsonPath("$.data[0].title").value("记忆 1"))
            .andExpect(jsonPath("$.data[0].type").value("long_term"))
            .andExpect(jsonPath("$.data[1].id").value("mem-2"))
            .andDo(print());
        
        verify(memoryService, times(1)).listMemories(1, 20);
    }
    
    @Test
    @DisplayName("测试获取记忆详情 - 存在")
    void testGetMemory_Exists() throws Exception {
        // Arrange
        Memory memory = Memory.builder()
            .id("mem-123")
            .type("long_term")
            .title("重要记忆")
            .content("详细内容")
            .tags(new String[]{"重要"})
            .createdAt(Instant.now())
            .build();
        
        when(memoryService.getMemory("mem-123")).thenReturn(memory);
        
        // Act & Assert
        mockMvc.perform(get("/api/memories/mem-123")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("mem-123"))
            .andExpect(jsonPath("$.data.title").value("重要记忆"))
            .andDo(print());
        
        verify(memoryService, times(1)).getMemory("mem-123");
    }
    
    @Test
    @DisplayName("测试获取记忆详情 - 不存在")
    void testGetMemory_NotExists() throws Exception {
        // Arrange
        when(memoryService.getMemory("nonexistent")).thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/memories/nonexistent")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andDo(print());
        
        verify(memoryService, times(1)).getMemory("nonexistent");
    }
    
    @Test
    @DisplayName("测试搜索记忆")
    void testSearchMemories() throws Exception {
        // Arrange
        Memory memory = Memory.builder()
            .id("mem-search")
            .type("long_term")
            .title("搜索结果")
            .content("匹配内容")
            .tags(new String[]{"测试"})
            .createdAt(Instant.now())
            .build();
        
        when(memoryService.searchMemories("关键词")).thenReturn(Arrays.asList(memory));
        
        // Act & Assert
        mockMvc.perform(get("/api/memories/search")
                .param("query", "关键词")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("mem-search"))
            .andExpect(jsonPath("$.data[0].title").value("搜索结果"))
            .andDo(print());
        
        verify(memoryService, times(1)).searchMemories("关键词");
    }
    
    @Test
    @DisplayName("测试创建记忆")
    void testCreateMemory() throws Exception {
        // Arrange
        Memory inputMemory = Memory.builder()
            .type("long_term")
            .title("新记忆")
            .content("新内容")
            .tags(new String[]{"新建"})
            .build();
        
        Memory createdMemory = Memory.builder()
            .id("mem-new")
            .type("long_term")
            .title("新记忆")
            .content("新内容")
            .tags(new String[]{"新建"})
            .createdAt(Instant.now())
            .build();
        
        when(memoryService.createMemory(any(Memory.class))).thenReturn(createdMemory);
        
        String requestBody = "{\"type\":\"long_term\",\"title\":\"新记忆\",\"content\":\"新内容\",\"tags\":[\"新建\"]}";
        
        // Act & Assert
        mockMvc.perform(post("/api/memories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("mem-new"))
            .andExpect(jsonPath("$.data.title").value("新记忆"))
            .andDo(print());
        
        verify(memoryService, times(1)).createMemory(any(Memory.class));
    }
    
    @Test
    @DisplayName("测试更新记忆")
    void testUpdateMemory() throws Exception {
        // Arrange
        doNothing().when(memoryService).updateMemory(any(Memory.class));
        
        String requestBody = "{\"title\":\"更新后的标题\",\"content\":\"更新后的内容\"}";
        
        // Act & Assert
        mockMvc.perform(put("/api/memories/mem-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andDo(print());
        
        verify(memoryService, times(1)).updateMemory(any(Memory.class));
    }
    
    @Test
    @DisplayName("测试删除记忆")
    void testDeleteMemory() throws Exception {
        // Arrange
        doNothing().when(memoryService).deleteMemory("mem-123");
        
        // Act & Assert
        mockMvc.perform(delete("/api/memories/mem-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andDo(print());
        
        verify(memoryService, times(1)).deleteMemory("mem-123");
    }
}
