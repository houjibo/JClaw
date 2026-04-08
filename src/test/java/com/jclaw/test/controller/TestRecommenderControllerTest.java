package com.jclaw.test.controller;

import com.jclaw.test.service.TestRecommenderService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 测试推荐控制器集成测试
 * TODO: 需要配置完整的 Spring 上下文和数据库环境
 */
@Disabled("需要集成测试环境")
@WebMvcTest(TestRecommenderController.class)
class TestRecommenderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestRecommenderService testService;

    // 服务层没有依赖 Mapper，不需要额外 Mock

    @Test
    void testRecommendTests() throws Exception {
        mockMvc.perform(post("/api/test/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"filePath\":\"/test/file.java\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testAnalyzeCoverage() throws Exception {
        mockMvc.perform(get("/api/test/coverage")
                .param("filePath", "/test/file.java"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists());
    }
}
