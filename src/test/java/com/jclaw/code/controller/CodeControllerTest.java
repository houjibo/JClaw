package com.jclaw.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jclaw.code.service.CodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 代码工具控制器测试
 */
@WebMvcTest(CodeController.class)
class CodeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CodeService codeService;
    
    @Test
    void testExplainCode() throws Exception {
        mockMvc.perform(post("/api/code/explain")
                .param("file", "/path/to/file.java")
                .param("language", "java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testOptimizeCode() throws Exception {
        mockMvc.perform(post("/api/code/optimize")
                .param("file", "/path/to/file.java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testSecurityScan() throws Exception {
        mockMvc.perform(post("/api/code/security")
                .param("file", "/path/to/file.java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testGenerateDocs() throws Exception {
        mockMvc.perform(post("/api/code/docs")
                .param("file", "/path/to/file.java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testBuildProject() throws Exception {
        mockMvc.perform(post("/api/code/build")
                .param("path", "/path/to/project")
                .param("tool", "maven"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testDebugCode() throws Exception {
        mockMvc.perform(post("/api/code/debug")
                .param("file", "/path/to/file.java")
                .param("line", "42"))
            .andExpect(status().isOk());
    }
}
