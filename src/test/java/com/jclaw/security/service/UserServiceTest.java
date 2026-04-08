package com.jclaw.security.service;

import com.jclaw.dto.LoginRequest;
import com.jclaw.dto.RegisterRequest;
import com.jclaw.security.entity.User;
import com.jclaw.security.mapper.UserMapper;
import com.jclaw.security.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 测试 BCrypt 密码加密功能
 */
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("SecurePassword123");
        request.setPhone("13800138000");
        
        when(userMapper.selectOne(any())).thenReturn(null); // 用户不存在
        when(userMapper.insert(any(User.class))).thenReturn(1);
        
        // 执行测试
        User result = userService.register(request);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertTrue(result.getPassword().startsWith("$2a$")); // BCrypt 前缀
        assertEquals("active", result.getStatus());
        assertNotNull(result.getCreatedAt());
        
        verify(userMapper, times(2)).selectOne(any()); // 检查用户名和邮箱
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testRegister_UsernameExists() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");
        
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        
        when(userMapper.selectOne(any())).thenReturn(existingUser);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
        
        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        
        // 用户名不存在，但邮箱已存在
        when(userMapper.selectOne(any()))
            .thenReturn(null) // 用户名检查
            .thenReturn(new User()); // 邮箱检查
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
        
        assertEquals("邮箱已被注册", exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("SecurePassword123");
        
        User user = new User();
        user.setId("user-id");
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("SecurePassword123"));
        user.setStatus("active");
        
        when(userMapper.selectOne(any())).thenReturn(user);
        
        // 执行测试
        User result = userService.login(request);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("user-id", result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testLogin_UserNotFound() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password");
        
        when(userMapper.selectOne(any())).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPassword");
        
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("CorrectPassword"));
        user.setStatus("active");
        
        when(userMapper.selectOne(any())).thenReturn(user);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void testLogin_UserDisabled() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus("disabled");
        
        when(userMapper.selectOne(any())).thenReturn(user);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        
        assertEquals("用户账号已被禁用", exception.getMessage());
    }

    @Test
    void testFindByUsername() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUsername("finduser");
        
        when(userMapper.selectOne(any())).thenReturn(mockUser);
        
        // 执行测试
        User result = userService.findByUsername("finduser");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("finduser", result.getUsername());
    }

    @Test
    void testFindByEmail() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setEmail("find@example.com");
        
        when(userMapper.selectOne(any())).thenReturn(mockUser);
        
        // 执行测试
        User result = userService.findByEmail("find@example.com");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("find@example.com", result.getEmail());
    }

    @Test
    void testPasswordEncryption_Strength() {
        // 测试 BCrypt 加密强度
        String rawPassword = "TestPassword123";
        String encoded = passwordEncoder.encode(rawPassword);
        
        // 验证 BCrypt 格式
        assertTrue(encoded.startsWith("$2a$"));
        assertEquals(60, encoded.length()); // BCrypt 哈希长度固定
        
        // 验证密码匹配
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
        assertFalse(passwordEncoder.matches("WrongPassword", encoded));
    }

    @Test
    void testPasswordEncryption_DifferentHashes() {
        // 测试同一密码生成不同哈希（BCrypt 特性）
        String password = "SamePassword";
        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);
        
        // BCrypt 每次生成不同哈希（因为盐不同）
        assertNotEquals(hash1, hash2);
        
        // 但都能验证通过
        assertTrue(passwordEncoder.matches(password, hash1));
        assertTrue(passwordEncoder.matches(password, hash2));
    }
}
