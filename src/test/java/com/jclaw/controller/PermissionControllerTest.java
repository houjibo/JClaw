package com.jclaw.controller;

import com.jclaw.services.PermissionTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限追踪控制器测试
 */
@DisplayName("权限追踪控制器测试")
class PermissionControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private PermissionTracker permissionTracker;
    
    @InjectMocks
    private PermissionController permissionController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();
    }
    
    @Test
    @DisplayName("测试获取权限状态")
    void testGetPermissions() throws Exception {
        // Arrange
        PermissionTracker.UserPreferences mockPrefs = new PermissionTracker.UserPreferences();
        mockPrefs.allowedPermissions = Map.of("file_read", true, "web_search", true);
        mockPrefs.deniedPermissions = Map.of("file_write", false);
        mockPrefs.permissionUsage = Map.of("file_read", 10, "web_search", 5);
        mockPrefs.lastUpdated = LocalDateTime.now();
        
        when(permissionTracker.getPreferences()).thenReturn(mockPrefs);
        
        // Act & Assert
        mockMvc.perform(get("/api/permissions")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.allowed.file_read").value(true))
            .andExpect(jsonPath("$.denied.file_write").value(false))
            .andExpect(jsonPath("$.usage.file_read").value(10))
            .andDo(print());
        
        verify(permissionTracker, times(1)).getPreferences();
    }
    
    @Test
    @DisplayName("测试允许权限")
    void testAllowPermission() throws Exception {
        // Arrange
        doNothing().when(permissionTracker).recordAllow("file_write", "file_tool", "write");
        
        // Act & Assert
        mockMvc.perform(post("/api/permissions/file_write/allow")
                .param("tool", "file_tool")
                .param("action", "write"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.permission").value("file_write"))
            .andExpect(jsonPath("$.status").value("allowed"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).recordAllow("file_write", "file_tool", "write");
    }
    
    @Test
    @DisplayName("测试允许权限 - 无工具和动作")
    void testAllowPermission_NoToolAction() throws Exception {
        // Arrange
        doNothing().when(permissionTracker).recordAllow("file_write", null, null);
        
        // Act & Assert
        mockMvc.perform(post("/api/permissions/file_write/allow"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.permission").value("file_write"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).recordAllow("file_write", null, null);
    }
    
    @Test
    @DisplayName("测试拒绝权限 - 带原因")
    void testDenyPermission_WithReason() throws Exception {
        // Arrange
        doNothing().when(permissionTracker).recordDenial("file_write", "file_tool", "write", "敏感操作");
        
        String requestBody = "{\"reason\":\"敏感操作\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/permissions/file_write/deny")
                .param("tool", "file_tool")
                .param("action", "write")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.permission").value("file_write"))
            .andExpect(jsonPath("$.status").value("denied"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).recordDenial("file_write", "file_tool", "write", "敏感操作");
    }
    
    @Test
    @DisplayName("测试拒绝权限 - 无原因")
    void testDenyPermission_NoReason() throws Exception {
        // Arrange
        doNothing().when(permissionTracker).recordDenial("file_write", null, null, null);
        
        // Act & Assert
        mockMvc.perform(post("/api/permissions/file_write/deny"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.permission").value("file_write"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).recordDenial("file_write", null, null, null);
    }
    
    @Test
    @DisplayName("测试检查权限 - 允许")
    void testCheckPermission_Allowed() throws Exception {
        // Arrange
        when(permissionTracker.isPermissionAllowed("file_read")).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(get("/api/permissions/file_read/check")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.permission").value("file_read"))
            .andExpect(jsonPath("$.allowed").value(true))
            .andDo(print());
        
        verify(permissionTracker, times(1)).isPermissionAllowed("file_read");
    }
    
    @Test
    @DisplayName("测试检查权限 - 拒绝")
    void testCheckPermission_Denied() throws Exception {
        // Arrange
        when(permissionTracker.isPermissionAllowed("file_write")).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(get("/api/permissions/file_write/check")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.permission").value("file_write"))
            .andExpect(jsonPath("$.allowed").value(false))
            .andDo(print());
        
        verify(permissionTracker, times(1)).isPermissionAllowed("file_write");
    }
    
    @Test
    @DisplayName("测试列出拒绝记录")
    void testListDenials() throws Exception {
        // Arrange
        List<Map<String, Object>> mockDenials = Arrays.asList(
            Map.of("permission", "file_write", "reason", "敏感操作", "timestamp", "2026-04-10T09:00:00Z")
        );
        when(permissionTracker.listDenials()).thenReturn(mockDenials);
        
        // Act & Assert
        mockMvc.perform(get("/api/permissions/denials")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.count").value(1))
            .andExpect(jsonPath("$.denials[0].permission").value("file_write"))
            .andExpect(jsonPath("$.denials[0].reason").value("敏感操作"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).listDenials();
    }
    
    @Test
    @DisplayName("测试清除拒绝记录")
    void testClearDenials() throws Exception {
        // Arrange
        doNothing().when(permissionTracker).clearDenials();
        
        // Act & Assert
        mockMvc.perform(post("/api/permissions/denials/clear"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("已清除所有拒绝记录"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).clearDenials();
    }
    
    @Test
    @DisplayName("测试重置用户偏好")
    void testResetPreferences() throws Exception {
        // Arrange
        doNothing().when(permissionTracker).resetPreferences();
        
        // Act & Assert
        mockMvc.perform(post("/api/permissions/reset"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("已重置用户偏好"))
            .andDo(print());
        
        verify(permissionTracker, times(1)).resetPreferences();
    }
    
    @Test
    @DisplayName("测试获取权限建议")
    void testGetSuggestion() throws Exception {
        // Arrange
        Map<String, Object> mockSuggestion = Map.of(
            "permission", "file_write",
            "suggestion", "allow",
            "confidence", 0.85,
            "reason", "用户经常允许此操作"
        );
        when(permissionTracker.getPermissionSuggestion("file_write", "file_tool", "write"))
            .thenReturn(mockSuggestion);
        
        // Act & Assert
        mockMvc.perform(get("/api/permissions/suggestion")
                .param("permission", "file_write")
                .param("tool", "file_tool")
                .param("action", "write")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.suggestion.permission").value("file_write"))
            .andExpect(jsonPath("$.suggestion.suggestion").value("allow"))
            .andExpect(jsonPath("$.suggestion.confidence").value(0.85))
            .andDo(print());
        
        verify(permissionTracker, times(1)).getPermissionSuggestion("file_write", "file_tool", "write");
    }
}
