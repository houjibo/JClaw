package com.jclaw.memory.controller;

import com.jclaw.memory.entity.Memory;
import com.jclaw.memory.service.MemoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 记忆控制器集成测试
 */
@WebMvcTest(MemoryController.class)
class MemoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemoryService memoryService;

    @Test
    void testListMemories() throws Exception {
        when(memoryService.listMemories(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/memories")
                .param("page", "1")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testCreateMemory() throws Exception {
        Memory mockMemory = Memory.builder()
            .id("mem_001")
            .title("测试记忆")
            .build();
        
        when(memoryService.createMemory(any())).thenReturn(mockMemory);

        mockMvc.perform(post("/api/memories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"long_term\",\"title\":\"测试\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value("mem_001"));
    }

    @Test
    void testDeleteMemory() throws Exception {
        doNothing().when(memoryService).deleteMemory(any());

        mockMvc.perform(delete("/api/memories/mem_001"))
            .andExpect(status().isOk());
    }
}
