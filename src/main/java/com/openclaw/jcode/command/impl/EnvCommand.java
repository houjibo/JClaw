package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 环境变量管理命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class EnvCommand extends Command {
    
    public EnvCommand() {
        this.name = "env";
        this.description = "环境变量管理";
        this.aliases = Arrays.asList("environment");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return listEnv();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "list", "ls" -> listEnv();
            case "get" -> getEnv(parts.length > 1 ? parts[1] : null);
            case "set" -> setEnv(parts.length > 1 ? parts[1] : null, parts.length > 2 ? String.join("=", Arrays.copyOfRange(parts, 2, parts.length)) : null);
            default -> listEnv();
        };
    }
    
    private CommandResult listEnv() {
        Map<String, String> env = System.getenv();
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 环境变量列表\n\n");
        sb.append(String.format("共 %d 个环境变量\n\n", env.size()));
        sb.append("| 变量名 | 值 |\n");
        sb.append("|--------|------|\n");
        
        // 只显示前 50 个，避免输出过长
        int count = 0;
        for (Map.Entry<String, String> entry : env.entrySet()) {
            if (count >= 50) {
                sb.append(String.format("| ... | (还有 %d 个未显示) |\n", env.size() - 50));
                break;
            }
            
            String value = entry.getValue();
            if (value.length() > 50) {
                value = value.substring(0, 50) + "...";
            }
            
            sb.append(String.format("| %s | %s |\n", entry.getKey(), value));
            count++;
        }
        
        return CommandResult.success("环境变量列表")
                .withDisplayText(sb.toString());
    }
    
    private CommandResult getEnv(String name) {
        if (name == null) {
            return CommandResult.error("请指定环境变量名称");
        }
        
        String value = System.getenv(name);
        
        if (value == null) {
            return CommandResult.error("环境变量不存在：" + name);
        }
        
        String report = String.format("""
            ## 环境变量：%s
            
            **值**: %s
            
            **长度**: %d 字符
            
            **来源**: 系统环境变量
            """, name, value, value.length());
        
        return CommandResult.success("环境变量：" + name)
                .withDisplayText(report);
    }
    
    private CommandResult setEnv(String name, String value) {
        if (name == null || value == null) {
            return CommandResult.error("用法：env set <变量名> <值>");
        }
        
        // Java 中无法真正修改系统环境变量，只能修改进程环境变量
        // 这里只提供指导信息
        String report = String.format("""
            ## 设置环境变量
            
            **变量**: %s
            **值**: %s
            
            ### 注意
            
            Java 程序无法直接修改系统环境变量。
            
            ### 临时设置（当前会话）
            
            ```bash
            # Linux/Mac
            export %s="%s"
            
            # Windows
            set %s=%s
            ```
            
            ### 永久设置
            
            **Linux/Mac** (~/.bashrc 或 ~/.zshrc):
            ```
            export %s="%s"
            ```
            
            **Windows** (系统属性 → 环境变量):
            1. 打开系统属性
            2. 点击"环境变量"
            3. 添加新的用户/系统变量
            4. 名称：%s
            5. 值：%s
            
            ### 在 Java 中访问
            
            ```java
            String value = System.getenv("%s");
            ```
            """, name, value, name, value, name, value, name, value, name, value, name);
        
        return CommandResult.success("环境变量设置指导：" + name)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：env
            别名：environment
            描述：环境变量管理
            
            用法：
              env                     # 列出环境变量
              env list                # 列出环境变量
              env get <变量名>        # 查看变量
              env set <名> <值>       # 设置指导
            
            示例：
              env
              env get JAVA_HOME
              env set MY_VAR value
            """;
    }
}
