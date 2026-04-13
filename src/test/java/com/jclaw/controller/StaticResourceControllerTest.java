package com.jclaw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 静态资源控制器测试
 */
@DisplayName("静态资源控制器测试")
class StaticResourceControllerTest {
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StaticResourceController()).build();
    }
    
    @Test
    @DisplayName("测试 favicon.ico 请求 - 返回 404")
    void testFavicon() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/favicon.ico"))
            .andExpect(status().isNotFound())
            .andDo(print());
    }
    
    @Test
    @DisplayName("测试根路径请求 - 返回欢迎页面")
    void testRoot() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("JClaw API Server")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Swagger UI")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/health")))
            .andDo(print());
    }
}
