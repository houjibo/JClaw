package com.jclaw.trace.controller;

import com.jclaw.trace.mapper.CodeUnitMapper;
import com.jclaw.trace.mapper.CallRelationshipMapper;
import com.jclaw.trace.service.TraceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 代码追溯控制器集成测试
 * TODO: 需要配置完整的 Spring 上下文和数据库环境
 */
@Disabled("需要集成测试环境")
@WebMvcTest(TraceController.class)
class TraceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraceService traceService;

    @MockBean
    private CodeUnitMapper codeUnitMapper;

    @MockBean
    private CallRelationshipMapper callRelationshipMapper;

    @Test
    void testListCodeUnits() throws Exception {
        mockMvc.perform(get("/api/trace/code-units")
                .param("page", "1")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetCallChain() throws Exception {
        mockMvc.perform(get("/api/trace/callchain/code_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testAnalyzeImpact() throws Exception {
        mockMvc.perform(post("/api/trace/impact/code_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists());
    }
}
