package com.jclaw.controller;

import com.jclaw.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 工具控制器测试
 */
@DisplayName("工具控制器测试")
class ToolControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private ToolRegistry toolRegistry;
    
    @InjectMocks
    private ToolController toolController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(toolController).build();
    }
    
    @Test
    @DisplayName("测试列出所有工具")
    void testListTools() throws Exception {
        // Arrange
        Tool mockTool1 = Mockito.mock(Tool.class);
        when(mockTool1.getName()).thenReturn("file_read");
        when(mockTool1.getDescription()).thenReturn("读取文件内容");
        when(mockTool1.getCategory()).thenReturn(ToolCategory.FILE);
        when(mockTool1.isRequiresConfirmation()).thenReturn(false);
        
        Tool mockTool2 = Mockito.mock(Tool.class);
        when(mockTool2.getName()).thenReturn("web_search");
        when(mockTool2.getDescription()).thenReturn("搜索网络");
        when(mockTool2.getCategory()).thenReturn(ToolCategory.NETWORK);
        when(mockTool2.isRequiresConfirmation()).thenReturn(true);
        
        when(toolRegistry.listTools()).thenReturn(Arrays.asList(mockTool1, mockTool2));
        
        // Act & Assert
        mockMvc.perform(get("/api/tools")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.count").value(2))
            .andExpect(jsonPath("$.tools[0].name").value("file_read"))
            .andExpect(jsonPath("$.tools[0].description").value("读取文件内容"))
            .andExpect(jsonPath("$.tools[0].category").value("FILE"))
            .andExpect(jsonPath("$.tools[1].name").value("web_search"))
            .andExpect(jsonPath("$.tools[1].category").value("NETWORK"))
            .andDo(print());
        
        verify(toolRegistry, times(1)).listTools();
    }
    
    @Test
    @DisplayName("测试获取工具详情 - 存在")
    void testGetTool_Exists() throws Exception {
        // Arrange
        Tool mockTool = Mockito.mock(Tool.class);
        when(mockTool.getName()).thenReturn("file_read");
        when(mockTool.getDescription()).thenReturn("读取文件内容");
        when(mockTool.getCategory()).thenReturn(ToolCategory.FILE);
        when(mockTool.isRequiresConfirmation()).thenReturn(false);
        when(mockTool.getHelp()).thenReturn("帮助信息");
        
        when(toolRegistry.getTool("file_read")).thenReturn(Optional.of(mockTool));
        
        // Act & Assert
        mockMvc.perform(get("/api/tools/file_read")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.tool.name").value("file_read"))
            .andExpect(jsonPath("$.tool.description").value("读取文件内容"))
            .andExpect(jsonPath("$.tool.category").value("FILE"))
            .andExpect(jsonPath("$.tool.help").value("帮助信息"))
            .andDo(print());
        
        verify(toolRegistry, times(1)).getTool("file_read");
    }
    
    @Test
    @DisplayName("测试获取工具详情 - 不存在")
    void testGetTool_NotExists() throws Exception {
        // Arrange
        when(toolRegistry.getTool("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/tools/nonexistent")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("工具不存在：nonexistent"))
            .andDo(print());
        
        verify(toolRegistry, times(1)).getTool("nonexistent");
    }
    
    @Test
    @DisplayName("测试执行工具 - 成功")
    void testExecuteTool_Success() throws Exception {
        // Arrange
        ToolResult mockResult = Mockito.mock(ToolResult.class);
        when(mockResult.isSuccess()).thenReturn(true);
        when(mockResult.getMessage()).thenReturn("执行成功");
        when(mockResult.getDurationMs()).thenReturn(150L);
        when(mockResult.getOutput()).thenReturn("输出内容");
        when(mockResult.getData()).thenReturn(Map.of("key", "value"));
        
        when(toolRegistry.execute(eq("file_read"), any(Map.class), any(ToolContext.class)))
            .thenReturn(mockResult);
        
        String requestBody = "{\"path\":\"/test/file.txt\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/tools/file_read/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("执行成功"))
            .andExpect(jsonPath("$.durationMs").value(150))
            .andExpect(jsonPath("$.output").value("输出内容"))
            .andExpect(jsonPath("$.data.key").value("value"))
            .andDo(print());
        
        verify(toolRegistry, times(1)).execute(eq("file_read"), any(Map.class), any(ToolContext.class));
    }
    
    @Test
    @DisplayName("测试执行工具 - 失败")
    void testExecuteTool_Failure() throws Exception {
        // Arrange
        ToolResult mockResult = Mockito.mock(ToolResult.class);
        when(mockResult.isSuccess()).thenReturn(false);
        when(mockResult.getMessage()).thenReturn("执行失败");
        when(mockResult.getDurationMs()).thenReturn(50L);
        when(mockResult.getError()).thenReturn("文件不存在");
        
        when(toolRegistry.execute(eq("file_read"), any(Map.class), any(ToolContext.class)))
            .thenReturn(mockResult);
        
        String requestBody = "{\"path\":\"/nonexistent/file.txt\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/tools/file_read/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("执行失败"))
            .andExpect(jsonPath("$.error").value("文件不存在"))
            .andDo(print());
        
        verify(toolRegistry, times(1)).execute(eq("file_read"), any(Map.class), any(ToolContext.class));
    }
    
    @Test
    @DisplayName("测试执行工具 - 无参数")
    void testExecuteTool_NoParams() throws Exception {
        // Arrange
        ToolResult mockResult = Mockito.mock(ToolResult.class);
        when(mockResult.isSuccess()).thenReturn(true);
        when(mockResult.getMessage()).thenReturn("执行成功");
        when(mockResult.getDurationMs()).thenReturn(100L);
        
        when(toolRegistry.execute(eq("health_check"), any(Map.class), any(ToolContext.class)))
            .thenReturn(mockResult);
        
        // Act & Assert
        mockMvc.perform(post("/api/tools/health_check/execute"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.durationMs").value(100))
            .andDo(print());
        
        verify(toolRegistry, times(1)).execute(eq("health_check"), any(Map.class), any(ToolContext.class));
    }
}
