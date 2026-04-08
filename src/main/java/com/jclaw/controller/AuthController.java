package com.jclaw.controller;

import com.jclaw.common.entity.Result;
import com.jclaw.dto.LoginRequest;
import com.jclaw.dto.RegisterRequest;
import com.jclaw.security.entity.User;
import com.jclaw.security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        log.info("用户注册请求：{}", request.getUsername());
        
        try {
            User user = userService.register(request);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            
            return Result.success(data);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求：{}", request.getUsername());
        
        try {
            User user = userService.login(request);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            // 实际应生成 JWT token
            data.put("token", "mock_token_" + user.getId());
            
            return Result.success(data);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        // TODO: 从 token 中解析用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("id", "mock_user_id");
        data.put("username", "mock_user");
        return Result.success(data);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 使 token 失效
        return Result.success();
    }
}
