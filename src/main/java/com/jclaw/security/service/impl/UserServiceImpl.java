package com.jclaw.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jclaw.dto.LoginRequest;
import com.jclaw.dto.RegisterRequest;
import com.jclaw.security.entity.User;
import com.jclaw.security.mapper.UserMapper;
import com.jclaw.security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#request.username")
    public User register(RegisterRequest request) {
        log.info("用户注册：{}", request.getUsername());
        
        // 检查用户名是否已存在
        User existingUser = findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            existingUser = findByEmail(request.getEmail());
            if (existingUser != null) {
                throw new RuntimeException("邮箱已被注册");
            }
        }
        
        // 创建用户
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .status("active")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        
        userMapper.insert(user);
        log.info("用户注册成功：{}", user.getId());
        
        return user;
    }

    @Override
    public User login(LoginRequest request) {
        log.info("用户登录：{}", request.getUsername());
        
        User user = findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("用户账号已被禁用");
        }
        
        log.info("用户登录成功：{}", user.getId());
        return user;
    }

    @Override
    @Cacheable(value = "users", key = "#username", unless = "#result == null")
    public User findByUsername(String username) {
        log.debug("查询用户：{} (从数据库)", username);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User findByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return userMapper.selectOne(wrapper);
    }
}
