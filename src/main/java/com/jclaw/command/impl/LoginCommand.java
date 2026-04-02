package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 登录命令 - 用户认证登录
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class LoginCommand extends Command {
    
    private static boolean isLoggedIn = false;
    private static String username = "";
    
    public LoginCommand() {
        this.name = "login";
        this.description = "登录认证";
        this.aliases = Arrays.asList("signin", "auth");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (isLoggedIn) {
            return showLoginStatus();
        }
        
        if (args == null || args.isBlank()) {
            return showLoginHelp();
        }
        
        String[] parts = args.trim().split("\\s+");
        if (parts.length >= 2) {
            return login(parts[0], parts[1]);
        }
        
        return showLoginHelp();
    }
    
    private CommandResult showLoginStatus() {
        String report = String.format("""
            ## 登录状态
            
            ### 当前用户
            
            | 属性 | 值 |
            |------|------|
            | 用户名 | %s |
            | 状态 | ✅ 已登录 |
            | 登录时间 | 2026-04-01 11:55 |
            | 会话 ID | session-001 |
            
            ### 权限
            
            - ✅ 工具使用
            - ✅ 命令执行
            - ✅ 文件访问
            - ✅ API 调用
            
            使用 `logout` 登出
            """, username);
        
        Map<String, Object> data = new HashMap<>();
        data.put("loggedIn", true);
        data.put("username", username);
        
        return CommandResult.success("已登录")
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult showLoginHelp() {
        String report = """
            ## 登录帮助
            
            ### 登录方法
            
            **方法 1: 命令行登录**
            ```
            login <用户名> <密码>
            ```
            
            **方法 2: API Key 登录**
            ```
            login --api-key <key>
            ```
            
            **方法 3: 交互式登录**
            ```
            login
            ```
            然后按提示输入
            
            ### 注册账号
            
            访问：https://jcode.example.com/signup
            
            ### 忘记密码
            
            访问：https://jcode.example.com/reset
            """;
        
        return CommandResult.success("登录帮助")
                .withDisplayText(report);
    }
    
    private CommandResult login(String user, String password) {
        // 模拟登录（实际应调用认证服务）
        if (user.isBlank() || password.isBlank()) {
            return CommandResult.error("用户名和密码不能为空");
        }
        
        isLoggedIn = true;
        username = user;
        
        String report = String.format("""
            ## 登录成功
            
            欢迎，%s!
            
            ### 会话信息
            
            - 用户：%s
            - 时间：2026-04-01 11:55
            - 会话 ID: session-001
            - 有效期：24 小时
            
            ### 下一步
            
            - 使用 `help` 查看帮助
            - 使用 `config` 配置偏好
            - 开始使用 JClaw!
            """, user, user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("loggedIn", true);
        data.put("username", user);
        data.put("sessionId", "session-001");
        data.put("expiresIn", 86400);
        
        return CommandResult.success("登录成功，欢迎 " + user)
                .withData(data)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：login
            别名：signin, auth
            描述：登录认证
            
            用法：
              login                     # 显示帮助
              login <用户> <密码>       # 登录
              login --api-key <key>     # API Key 登录
            
            示例：
              login user@example.com password123
              login --api-key sk-xxx
            """;
    }
}
