package com.jclaw.controller;

import com.jclaw.services.ProgressiveLoader;
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
 * 渐进式加载控制器测试
 */
@DisplayName("渐进式加载控制器测试")
class FeatureControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private ProgressiveLoader progressiveLoader;
    
    @InjectMocks
    private FeatureController featureController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(featureController).build();
    }
    
    @Test
    @DisplayName("测试列出所有特性")
    void testListFeatures() throws Exception {
        // Arrange
        List<Map<String, Object>> mockFeatures = Arrays.asList(
            Map.of("name", "feature1", "enabled", true),
            Map.of("name", "feature2", "enabled", false)
        );
        when(progressiveLoader.listFeatures()).thenReturn(mockFeatures);
        
        // Act & Assert
        mockMvc.perform(get("/api/features")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("feature1"))
            .andExpect(jsonPath("$[0].enabled").value(true))
            .andExpect(jsonPath("$[1].name").value("feature2"))
            .andExpect(jsonPath("$[1].enabled").value(false))
            .andDo(print());
        
        verify(progressiveLoader, times(1)).listFeatures();
    }
    
    @Test
    @DisplayName("测试启用特性")
    void testToggleFeature_Enable() throws Exception {
        // Arrange
        doNothing().when(progressiveLoader).setFeatureEnabled("feature1", true);
        
        String requestBody = "{\"enabled\":true}";
        
        // Act & Assert
        mockMvc.perform(put("/api/features/feature1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.feature").value("feature1"))
            .andExpect(jsonPath("$.enabled").value(true))
            .andDo(print());
        
        verify(progressiveLoader, times(1)).setFeatureEnabled("feature1", true);
    }
    
    @Test
    @DisplayName("测试禁用特性")
    void testToggleFeature_Disable() throws Exception {
        // Arrange
        doNothing().when(progressiveLoader).setFeatureEnabled("feature1", false);
        
        String requestBody = "{\"enabled\":false}";
        
        // Act & Assert
        mockMvc.perform(put("/api/features/feature1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.feature").value("feature1"))
            .andExpect(jsonPath("$.enabled").value(false))
            .andDo(print());
        
        verify(progressiveLoader, times(1)).setFeatureEnabled("feature1", false);
    }
    
    @Test
    @DisplayName("测试切换特性 - enabled 为空")
    void testToggleFeature_NullEnabled() throws Exception {
        // Arrange
        String requestBody = "{}";
        
        // Act & Assert
        mockMvc.perform(put("/api/features/feature1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("enabled 参数不能为空"))
            .andDo(print());
        
        verify(progressiveLoader, never()).setFeatureEnabled(anyString(), anyBoolean());
    }
    
    @Test
    @DisplayName("测试获取启动指标")
    void testGetStartupMetrics() throws Exception {
        // Arrange
        Map<String, Object> mockMetrics = Map.of(
            "startupTime", 865,
            "phase", "RUNNING",
            "loadedComponents", 10
        );
        when(progressiveLoader.getStartupMetrics()).thenReturn(mockMetrics);
        
        // Act & Assert
        mockMvc.perform(get("/api/features/startup/metrics")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startupTime").value(865))
            .andExpect(jsonPath("$.phase").value("RUNNING"))
            .andExpect(jsonPath("$.loadedComponents").value(10))
            .andDo(print());
        
        verify(progressiveLoader, times(1)).getStartupMetrics();
    }
    
    @Test
    @DisplayName("测试预加载组件")
    void testPreloadComponents() throws Exception {
        // Arrange
        doNothing().when(progressiveLoader).preloadComponents(anyList());
        
        String requestBody = "[\"component1\",\"component2\"]";
        
        // Act & Assert
        mockMvc.perform(post("/api/features/startup/preload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.components[0]").value("component1"))
            .andExpect(jsonPath("$.components[1]").value("component2"))
            .andDo(print());
        
        verify(progressiveLoader, times(1)).preloadComponents(anyList());
    }
    
    @Test
    @DisplayName("测试获取当前启动阶段")
    void testGetCurrentPhase() throws Exception {
        // Arrange
        when(progressiveLoader.getCurrentPhase()).thenReturn(ProgressiveLoader.StartupPhase.READY);
        
        // Act & Assert
        mockMvc.perform(get("/api/features/startup/phase")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.phase").value("READY"))
            .andDo(print());
        
        verify(progressiveLoader, times(1)).getCurrentPhase();
    }
}
