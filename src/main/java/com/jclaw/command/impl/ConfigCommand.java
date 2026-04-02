package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 支持查看、修改配置
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class ConfigCommand extends Command {
    
    // 模拟配置存储（实际应连接配置服务）
    private static final Map<String, Object> CONFIG = new HashMap<>();
    static {
        CONFIG.put("model", "qwen3.5-plus");
        CONFIG.put("temperature", 0.7);
        CONFIG.put("maxTokens", 4096);
        CONFIG.put("autoApprove", false);
        CONFIG.put("workingDirectory", System.getProperty("user.dir"));
    }
    
    public ConfigCommand() {
        this.name = "config";
        this.description = "查看和修改配置";
        this.aliases = Arrays.asList("cfg", "settings", "set");
        this.category = CommandCategory.CONFIG;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("key", new CommandParameter("key", "配置键", false));
        this.parameters.put("value", new CommandParameter("value", "配置值", false));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        // 解析参数
        String[] parts = args != null ? args.trim().split("\\s+", 2) : new String[0];
        
        if (parts.length == 0 || args == null || args.isBlank()) {
            // 无参数：列出所有配置
            return listConfig();
        }
        
        String key = parts[0];
        String value = parts.length > 1 ? parts[1] : null;
        
        if (value != null) {
            // 设置配置
            return setConfig(key, value, context);
        } else if (key.equals("-h") || key.equals("--help")) {
            // 帮助
            return CommandResult.success(getHelp());
        } else {
            // 获取配置
            return getConfig(key);
        }
    }
    
    /**
     * 列出所有配置
     */
    private CommandResult listConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 当前配置\n\n");
        
        for (Map.Entry<String, Object> entry : CONFIG.entrySet()) {
            sb.append("- **").append(entry.getKey()).append("**: ")
              .append(entry.getValue()).append("\n");
        }
        
        return CommandResult.success("配置列表")
                .withData("config", new HashMap<>(CONFIG))
                .withDisplayText(sb.toString());
    }
    
    /**
     * 获取配置值
     */
    private CommandResult getConfig(String key) {
        if (!CONFIG.containsKey(key)) {
            return CommandResult.error("配置项不存在：" + key);
        }
        
        Object value = CONFIG.get(key);
        return CommandResult.success("配置值：" + value)
                .withData("key", key)
                .withData("value", value);
    }
    
    /**
     * 设置配置值
     */
    private CommandResult setConfig(String key, String value, CommandContext context) {
        // 移除引号
        value = value.replaceAll("^\"|\"$", "").replaceAll("^'|'$", "");
        
        // 类型转换
        Object typedValue;
        if (value.equalsIgnoreCase("true")) {
            typedValue = true;
        } else if (value.equalsIgnoreCase("false")) {
            typedValue = false;
        } else {
            try {
                typedValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                typedValue = value;
            }
        }
        
        CONFIG.put(key, typedValue);
        context.output("配置已更新：" + key + " = " + typedValue);
        
        return CommandResult.success("配置已更新：" + key + " = " + typedValue)
                .withData("key", key)
                .withData("value", typedValue);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：config
            别名：cfg, settings, set
            描述：查看和修改配置
            
            用法：
              config              # 列出所有配置
              config <key>        # 查看配置值
              config <key> <value> # 设置配置值
            
            示例：
              config              # 显示所有配置
              config model        # 查看当前模型
              config model qwen3.5-plus  # 设置模型
              config temperature 0.8     # 设置温度
              config autoApprove true    # 启用自动批准
            """;
    }
}
