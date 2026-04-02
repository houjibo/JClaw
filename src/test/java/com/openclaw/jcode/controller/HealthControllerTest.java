package com.openclaw.jcode.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * 健康检查控制器测试
 */
@DisplayName("健康检查控制器测试")
class HealthControllerTest {
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HealthController()).build();
    }
    
    @Test
    @DisplayName("测试健康检查端点")
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/health")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.version").value("1.0.0"))
            .andExpect(jsonPath("$.components").exists())
            .andDo(print());
    }
    
    @Test
    @DisplayName("测试就绪检查端点")
    void testReady() throws Exception {
        mockMvc.perform(get("/api/health/ready")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("READY"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andDo(print());
    }
    
    @Test
    @DisplayName("测试存活检查端点")
    void testLive() throws Exception {
        mockMvc.perform(get("/api/health/live")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ALIVE"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andDo(print());
    }
}
