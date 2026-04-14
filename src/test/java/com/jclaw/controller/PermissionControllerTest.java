package com.jclaw.controller;

import com.jclaw.security.PermissionService;
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
 * 权限控制器测试
 */
@DisplayName("权限控制器测试")
class PermissionControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private PermissionService permissionService;
    
    @InjectMocks
    private PermissionController permissionController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();
    }
    
    @Test
    @DisplayName("测试检查权限")
    void testCheckPermission() throws Exception {
        when(permissionService.hasPermission("user1", "file_read")).thenReturn(true);
        
        mockMvc.perform(get("/api/permissions/check")
                .param("user_id", "user1")
                .param("permission", "file_read"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.has_permission").value(true))
            .andDo(print());
        
        verify(permissionService, times(1)).hasPermission("user1", "file_read");
    }
    
    @Test
    @DisplayName("测试获取用户权限")
    void testGetUserPermissions() throws Exception {
        when(permissionService.getUserPermissions("user1")).thenReturn(Set.of("file_read", "file_write"));
        
        mockMvc.perform(get("/api/permissions/users/user1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.permissions").isArray())
            .andDo(print());
        
        verify(permissionService, times(1)).getUserPermissions("user1");
    }
    
    @Test
    @DisplayName("测试列出用户")
    void testListUsers() throws Exception {
        when(permissionService.listUsers()).thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/permissions/users"))
            .andExpect(status().isOk())
            .andDo(print());
        
        verify(permissionService, times(1)).listUsers();
    }
    
    @Test
    @DisplayName("测试列出角色")
    void testListRoles() throws Exception {
        when(permissionService.listRoles()).thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/permissions/roles"))
            .andExpect(status().isOk())
            .andDo(print());
        
        verify(permissionService, times(1)).listRoles();
    }
}
