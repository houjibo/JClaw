package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 登出命令 - 用户认证登出
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class LogoutCommand extends Command {
    
    public LogoutCommand() {
        this.name = "logout";
        this.description = "登出认证";
        this.aliases = Arrays.asList("signout", "exit");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        // 模拟登出
        String report = """
            ## 登出成功
            
            ### 会话总结
            
            | 指标 | 值 |
            |------|------|
            | 会话时长 | 0 分钟 |
            | 消息数 | 0 |
            | 工具调用 | 0 次 |
            | 命令执行 | 0 次 |
            
            ### 下次登录
            
            - 使用 `login` 重新登录
            - 或使用 API Key 自动登录
            
            感谢使用 JClaw! 👋
            """;
        
        Map<String, Object> data = new HashMap<>();
        data.put("loggedOut", true);
        data.put("timestamp", new Date().toString());
        
        return CommandResult.success("登出成功")
                .withData(data)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：logout
            别名：signout
            描述：登出认证
            
            用法：
              logout                    # 登出
            
            示例：
              logout
              signout
            """;
    }
}
