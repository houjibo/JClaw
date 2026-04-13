package com.jclaw.controller;

import com.jclaw.services.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 流式输出控制器测试
 * 注：SSE 连接测试需要完整的 Spring 环境，这里只测试简单的 REST 端点
 */
@DisplayName("流式输出控制器测试")
class StreamControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private SseService sseService;
    
    @InjectMocks
    private StreamController streamController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(streamController).build();
    }
    
    @Test
    @DisplayName("测试获取连接状态")
    void testStatus() throws Exception {
        // Arrange
        when(sseService.getActiveConnections()).thenReturn(5);
        
        // Act & Assert
        mockMvc.perform(get("/api/stream/status")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeConnections").value(5))
            .andExpect(jsonPath("$.status").value("running"))
            .andDo(print());
        
        verify(sseService, times(1)).getActiveConnections();
    }
    
    @Test
    @DisplayName("测试关闭客户端连接")
    void testDisconnect() throws Exception {
        // Arrange
        doNothing().when(sseService).closeConnection("client-123");
        
        // Act & Assert
        mockMvc.perform(post("/api/stream/disconnect")
                .param("clientId", "client-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.clientId").value("client-123"))
            .andDo(print());
        
        verify(sseService, times(1)).closeConnection("client-123");
    }
}
