package com.jclaw.impact.controller;

import com.jclaw.impact.service.ImpactAnalysisService;
import com.jclaw.trace.service.TraceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 影响分析控制器集成测试
 * TODO: 需要配置完整的 Spring 上下文和数据库环境
 */
@Disabled("需要集成测试环境")
@WebMvcTest(ImpactAnalysisController.class)
class ImpactAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImpactAnalysisService impactService;

    @MockBean
    private TraceService traceService;

    @Test
    void testAnalyzeImpact() throws Exception {
        mockMvc.perform(post("/api/impact/code_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetRiskScore() throws Exception {
        mockMvc.perform(get("/api/impact/risk/code_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isNumber());
    }
}
