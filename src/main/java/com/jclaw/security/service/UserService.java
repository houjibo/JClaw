package com.jclaw.security.service;

import com.jclaw.dto.LoginRequest;
import com.jclaw.dto.RegisterRequest;
import com.jclaw.security.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    User login(LoginRequest request);
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    User findByEmail(String email);
}
