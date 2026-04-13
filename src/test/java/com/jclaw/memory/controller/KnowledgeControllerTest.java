package com.jclaw.memory.controller;

import com.jclaw.memory.entity.Knowledge;
import com.jclaw.memory.service.KnowledgeService;
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
 * 知识控制器测试
 */
@DisplayName("知识控制器测试")
class KnowledgeControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private KnowledgeService knowledgeService;
    
    @InjectMocks
    private KnowledgeController knowledgeController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(knowledgeController).build();
    }
    
    @Test
    @DisplayName("测试查询知识列表")
    void testListKnowledge() throws Exception {
        // Arrange
        Knowledge knowledge1 = Knowledge.builder()
            .id("know-1")
            .title("知识 1")
            .category("技术")
            .content("内容 1")
            .tags(new String[]{"Java", "测试"})
            .createdAt(Instant.now())
            .build();
        
        Knowledge knowledge2 = Knowledge.builder()
            .id("know-2")
            .title("知识 2")
            .category("产品")
            .content("内容 2")
            .tags(new String[]{"需求"})
            .createdAt(Instant.now())
            .build();
        
        when(knowledgeService.listKnowledge(1, 20)).thenReturn(Arrays.asList(knowledge1, knowledge2));
        
        // Act & Assert
        mockMvc.perform(get("/api/knowledge")
                .param("page", "1")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("know-1"))
            .andExpect(jsonPath("$.data[0].title").value("知识 1"))
            .andExpect(jsonPath("$.data[0].category").value("技术"))
            .andExpect(jsonPath("$.data[1].category").value("产品"))
            .andDo(print());
        
        verify(knowledgeService, times(1)).listKnowledge(1, 20);
    }
    
    @Test
    @DisplayName("测试搜索知识")
    void testSearchKnowledge() throws Exception {
        // Arrange
        Knowledge knowledge = Knowledge.builder()
            .id("know-search")
            .title("搜索结果")
            .category("技术")
            .content("匹配内容")
            .tags(new String[]{"测试"})
            .createdAt(Instant.now())
            .build();
        
        when(knowledgeService.searchKnowledge("关键词")).thenReturn(Arrays.asList(knowledge));
        
        // Act & Assert
        mockMvc.perform(get("/api/knowledge/search")
                .param("query", "关键词")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("know-search"))
            .andExpect(jsonPath("$.data[0].title").value("搜索结果"))
            .andDo(print());
        
        verify(knowledgeService, times(1)).searchKnowledge("关键词");
    }
    
    @Test
    @DisplayName("测试从记忆萃取知识 - 成功")
    void testExtractFromMemory_Success() throws Exception {
        // Arrange
        Knowledge knowledge = Knowledge.builder()
            .id("know-extract")
            .title("萃取的知识")
            .category("技术")
            .content("从记忆萃取的内容")
            .tags(new String[]{"自动萃取"})
            .createdAt(Instant.now())
            .build();
        
        when(knowledgeService.extractFromMemory("mem-123")).thenReturn(knowledge);
        
        // Act & Assert
        mockMvc.perform(post("/api/knowledge/extract/memory/mem-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("know-extract"))
            .andExpect(jsonPath("$.data.title").value("萃取的知识"))
            .andDo(print());
        
        verify(knowledgeService, times(1)).extractFromMemory("mem-123");
    }
    
    @Test
    @DisplayName("测试从记忆萃取知识 - 记忆不存在")
    void testExtractFromMemory_NotExists() throws Exception {
        // Arrange
        when(knowledgeService.extractFromMemory("nonexistent")).thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(post("/api/knowledge/extract/memory/nonexistent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andDo(print());
        
        verify(knowledgeService, times(1)).extractFromMemory("nonexistent");
    }
    
    @Test
    @DisplayName("测试创建知识")
    void testCreateKnowledge() throws Exception {
        // Arrange
        Knowledge inputKnowledge = Knowledge.builder()
            .title("新知识")
            .category("技术")
            .content("新内容")
            .tags(new String[]{"新建"})
            .build();
        
        Knowledge createdKnowledge = Knowledge.builder()
            .id("know-new")
            .title("新知识")
            .category("技术")
            .content("新内容")
            .tags(new String[]{"新建"})
            .createdAt(Instant.now())
            .build();
        
        when(knowledgeService.createKnowledge(any(Knowledge.class))).thenReturn(createdKnowledge);
        
        String requestBody = "{\"title\":\"新知识\",\"category\":\"技术\",\"content\":\"新内容\",\"tags\":[\"新建\"]}";
        
        // Act & Assert
        mockMvc.perform(post("/api/knowledge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("know-new"))
            .andExpect(jsonPath("$.data.title").value("新知识"))
            .andExpect(jsonPath("$.data.category").value("技术"))
            .andDo(print());
        
        verify(knowledgeService, times(1)).createKnowledge(any(Knowledge.class));
    }
    
    @Test
    @DisplayName("测试更新知识")
    void testUpdateKnowledge() throws Exception {
        // Arrange
        doNothing().when(knowledgeService).updateKnowledge(any(Knowledge.class));
        
        String requestBody = "{\"title\":\"更新后的标题\",\"content\":\"更新后的内容\"}";
        
        // Act & Assert
        mockMvc.perform(put("/api/knowledge/know-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andDo(print());
        
        verify(knowledgeService, times(1)).updateKnowledge(any(Knowledge.class));
    }
    
    @Test
    @DisplayName("测试删除知识")
    void testDeleteKnowledge() throws Exception {
        // Arrange
        doNothing().when(knowledgeService).deleteKnowledge("know-123");
        
        // Act & Assert
        mockMvc.perform(delete("/api/knowledge/know-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andDo(print());
        
        verify(knowledgeService, times(1)).deleteKnowledge("know-123");
    }
}
