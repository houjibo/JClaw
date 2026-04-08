package com.jclaw.security.mapper;

import com.jclaw.BaseMapperTest;
import com.jclaw.security.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapper 集成测试
 * 测试用户数据访问层的 CRUD 操作
 */
@DisplayName("UserMapper 集成测试")
class UserMapperTest extends BaseMapperTest {

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        // 清理 user 表
        userMapper.delete(null);
    }

    @Test
    @DisplayName("测试插入用户")
    void testInsert() {
        // Arrange
        User user = User.builder()
            .id("user_001")
            .username("testuser")
            .email("test@example.com")
            .password("bcrypt_password123")
            .phone("13800138000")
            .status("active")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        // Act
        int result = userMapper.insert(user);

        // Assert
        assertEquals(1, result);
        assertNotNull(user.getId());
    }

    @Test
    @DisplayName("测试根据 ID 查询用户")
    void testSelectById() {
        // Arrange
        User user = createUser("user_001", "testuser");
        userMapper.insert(user);

        // Act
        User result = userMapper.selectById("user_001");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getEmail().contains("testuser"));
    }

    @Test
    @DisplayName("测试根据用户名查询用户")
    void testSelectByUsername() {
        // Arrange
        User user = createUser("user_001", "testuser");
        userMapper.insert(user);

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("username", "testuser");

        // Act
        User result = userMapper.selectOne(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("测试查询不存在的用户")
    void testSelectNonExistentUser() {
        // Act
        User result = userMapper.selectById("non_existent");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试更新用户")
    void testUpdate() {
        // Arrange
        User user = createUser("user_001", "testuser");
        userMapper.insert(user);

        // Act
        user.setEmail("updated@example.com");
        user.setStatus("inactive");
        int result = userMapper.updateById(user);

        // Assert
        assertEquals(1, result);
        
        User updated = userMapper.selectById("user_001");
        assertNotNull(updated);
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("inactive", updated.getStatus());
    }

    @Test
    @DisplayName("测试删除用户")
    void testDelete() {
        // Arrange
        User user = createUser("user_001", "testuser");
        userMapper.insert(user);

        // Act
        int result = userMapper.deleteById("user_001");

        // Assert
        assertEquals(1, result);
        
        User deleted = userMapper.selectById("user_001");
        assertNull(deleted);
    }

    @Test
    @DisplayName("测试查询用户列表")
    void testSelectList() {
        // Arrange
        userMapper.insert(createUser("user_001", "user1"));
        userMapper.insert(createUser("user_002", "user2"));
        userMapper.insert(createUser("user_003", "user3"));

        // Act
        List<User> users = userMapper.selectList(null);

        // Assert
        assertEquals(3, users.size());
    }

    @Test
    @DisplayName("测试条件查询用户列表")
    void testSelectListWithCondition() {
        // Arrange
        userMapper.insert(createUser("user_001", "active_user1", "active"));
        userMapper.insert(createUser("user_002", "active_user2", "active"));
        userMapper.insert(createUser("user_003", "inactive_user", "inactive"));

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("status", "active");

        // Act
        List<User> users = userMapper.selectList(wrapper);

        // Assert
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(u -> "active".equals(u.getStatus())));
    }

    @Test
    @DisplayName("测试条件查询 + 限制结果数")
    void testSelectWithLimit() {
        // Arrange
        for (int i = 1; i <= 25; i++) {
            userMapper.insert(createUser("user_" + String.format("%03d", i), "user" + i));
        }

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.last("LIMIT 10");

        // Act
        List<User> users = userMapper.selectList(wrapper);

        // Assert
        assertEquals(10, users.size());
    }

    @Test
    @DisplayName("测试唯一约束 - 重复用户名")
    void testUniqueConstraintOnUsername() {
        // Arrange
        User user1 = createUser("user_001", "testuser");
        User user2 = createUser("user_002", "testuser");  // 相同用户名

        userMapper.insert(user1);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userMapper.insert(user2);
        });
    }

    /**
     * 辅助方法：创建测试用户
     */
    private User createUser(String id, String username) {
        return createUser(id, username, "active");
    }

    private User createUser(String id, String username, String status) {
        return User.builder()
            .id(id)
            .username(username)
            .email(username + "@example.com")
            .password("bcrypt_password123")
            .status(status)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }
}
