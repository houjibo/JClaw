package com.jclaw.controller;

import com.jclaw.security.entity.User;
import com.jclaw.security.service.UserService;
import com.jclaw.dto.LoginRequest;
import com.jclaw.dto.RegisterRequest;
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
 * 认证控制器测试
 */
@DisplayName("认证控制器测试")
class AuthControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private AuthController authController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }
    
    @Test
    @DisplayName("测试用户注册 - 成功")
    void testRegister_Success() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId("user-123");
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        
        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);
        
        String requestBody = "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("user-123"))
            .andExpect(jsonPath("$.data.username").value("testuser"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andDo(print());
        
        verify(userService, times(1)).register(any(RegisterRequest.class));
    }
    
    @Test
    @DisplayName("测试用户注册 - 失败")
    void testRegister_Failure() throws Exception {
        // Arrange
        when(userService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("用户名已存在"));
        
        String requestBody = "{\"username\":\"existinguser\",\"email\":\"test@example.com\",\"password\":\"password123\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andDo(print());
        
        verify(userService, times(1)).register(any(RegisterRequest.class));
    }
    
    @Test
    @DisplayName("测试用户登录 - 成功")
    void testLogin_Success() throws Exception {
        // Arrange
        User mockUser = new User();
        mockUser.setId("user-123");
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        
        when(userService.login(any(LoginRequest.class))).thenReturn(mockUser);
        
        String requestBody = "{\"username\":\"testuser\",\"password\":\"password123\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("user-123"))
            .andExpect(jsonPath("$.data.username").value("testuser"))
            .andExpect(jsonPath("$.data.token").value("mock_token_user-123"))
            .andDo(print());
        
        verify(userService, times(1)).login(any(LoginRequest.class));
    }
    
    @Test
    @DisplayName("测试用户登录 - 失败")
    void testLogin_Failure() throws Exception {
        // Arrange
        when(userService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("用户名或密码错误"));
        
        String requestBody = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andDo(print());
        
        verify(userService, times(1)).login(any(LoginRequest.class));
    }
    
    @Test
    @DisplayName("测试获取当前用户信息")
    void testGetCurrentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("mock_user_id"))
            .andExpect(jsonPath("$.data.username").value("mock_user"))
            .andDo(print());
    }
    
    @Test
    @DisplayName("测试用户登出")
    void testLogout() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andDo(print());
    }
}
