package com.jclaw.code.controller;

import com.jclaw.code.service.CodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

class CodeControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private CodeService codeService;
    
    @InjectMocks
    private CodeController codeController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(codeController).build();
    }
    
    @Test
    void testExplainCode() throws Exception {
        mockMvc.perform(post("/api/code/explain")
                .param("file", "test.java")
                .param("language", "java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testOptimizeCode() throws Exception {
        mockMvc.perform(post("/api/code/optimize")
                .param("file", "test.java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testSecurityScan() throws Exception {
        mockMvc.perform(post("/api/code/security")
                .param("file", "test.java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testGenerateDocs() throws Exception {
        mockMvc.perform(post("/api/code/docs")
                .param("file", "test.java"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testBuildProject() throws Exception {
        mockMvc.perform(post("/api/code/build")
                .param("path", ".")
                .param("tool", "maven"))
            .andExpect(status().isOk());
    }
    
    @Test
    void testDebugCode() throws Exception {
        mockMvc.perform(post("/api/code/debug")
                .param("file", "test.java")
                .param("line", "10"))
            .andExpect(status().isOk());
    }
}
