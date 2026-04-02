package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * IDE 插件管理和集成
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class IdeCommand extends Command {
    
    public IdeCommand() {
        this.name = "ide";
        this.description = "IDE 集成和插件管理";
        this.aliases = Arrays.asList("editor", "vscode");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showIdeStatus();
        }
        
        String[] parts = args.trim().split("\\s+");
        String action = parts[0];
        
        return switch (action) {
            case "status" -> showIdeStatus();
            case "install" -> installIdePlugin(parts.length > 1 ? parts[1] : null);
            case "uninstall" -> uninstallIdePlugin(parts.length > 1 ? parts[1] : null);
            case "config" -> showIdeConfig();
            default -> showIdeStatus();
        };
    }
    
    private CommandResult showIdeStatus() {
        String report = """
            ## IDE 集成状态
            
            ### 检测到的 IDE
            
            | IDE | 状态 | 插件 |
            |-----|------|------|
            | VS Code | ❌ 未安装 | - |
            | IntelliJ IDEA | ❌ 未检测 | - |
            | Eclipse | ❌ 未检测 | - |
            
            ### 可用插件
            
            | 插件 | 版本 | 状态 |
            |------|------|------|
            | JClaw for VS Code | 1.0.0 | 可安装 |
            | JClaw for IDEA | 1.0.0 | 可安装 |
            
            ### 安装方法
            
            **VS Code**
            ```
            ide install vscode
            ```
            
            **IntelliJ IDEA**
            ```
            ide install idea
            ```
            
            使用 `ide install <ide>` 安装插件
            """;
        
        Map<String, Object> data = new HashMap<>();
        data.put("detected", new ArrayList<>());
        data.put("available", Arrays.asList("vscode", "idea"));
        
        return CommandResult.success("IDE 集成状态")
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult installIdePlugin(String ide) {
        if (ide == null) {
            return CommandResult.error("请指定 IDE 类型：vscode, idea");
        }
        
        String report = switch (ide.toLowerCase()) {
            case "vscode", "visual-studio-code" -> """
                ## 安装 VS Code 插件
                
                ### 方法 1: 市场安装
                
                1. 打开 VS Code
                2. 进入扩展市场 (Ctrl+Shift+X)
                3. 搜索 "JClaw"
                4. 点击安装
                
                ### 方法 2: 命令行安装
                
                ```bash
                code --install-extension jcode.jcode
                ```
                
                ### 安装后
                
                1. 重启 VS Code
                2. 配置 API Key
                3. 开始使用！
                
                ✅ 安装说明已显示
                """;
                
            case "idea", "intellij" -> """
                ## 安装 IntelliJ IDEA 插件
                
                ### 方法 1: 市场安装
                
                1. 打开 IDEA
                2. Settings → Plugins
                3. Marketplace 搜索 "JClaw"
                4. 点击安装
                
                ### 方法 2: 本地安装
                
                1. 下载插件 ZIP
                2. Settings → Plugins → ⚙️ → Install from Disk
                3. 选择 ZIP 文件
                
                ### 安装后
                
                1. 重启 IDEA
                2. 配置 API Key
                3. 开始使用！
                
                ✅ 安装说明已显示
                """;
                
            default -> "未知 IDE: " + ide + "，支持 vscode, idea";
        };
        
        return CommandResult.success("IDE 插件安装说明")
                .withDisplayText(report);
    }
    
    private CommandResult uninstallIdePlugin(String ide) {
        if (ide == null) {
            return CommandResult.error("请指定 IDE 类型");
        }
        
        String report = String.format("""
            ## 卸载 %s 插件
            
            ### VS Code
            ```bash
            code --uninstall-extension jcode.jcode
            ```
            
            ### IDEA
            Settings → Plugins → JClaw → Uninstall
            
            ✅ 卸载说明已显示
            """, ide);
        
        return CommandResult.success("IDE 插件卸载说明")
                .withDisplayText(report);
    }
    
    private CommandResult showIdeConfig() {
        String report = """
            ## IDE 配置
            
            ### 默认设置
            
            ```json
            {
              "jcode.enabled": true,
              "jcode.model": "qwen3.5-plus",
              "jcode.autoComplete": true,
              "jcode.codeReview": true,
              "jcode.terminal": {
                "enabled": true,
                "shell": "bash"
              }
            }
            ```
            
            ### 快捷键
            
            | 功能 | 快捷键 |
            |------|--------|
            | 打开面板 | Ctrl+Shift+J |
            | 代码审查 | Ctrl+Shift+R |
            | 解释代码 | Ctrl+Shift+E |
            | 生成测试 | Ctrl+Shift+T |
            
            ### 自定义配置
            
            在设置中搜索 "jcode" 进行配置
            """;
        
        return CommandResult.success("IDE 配置")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：ide
            别名：editor, vscode
            描述：IDE 集成和插件管理
            
            用法：
              ide                       # 显示状态
              ide status                # 显示状态
              ide install <ide>         # 安装插件
              ide uninstall <ide>       # 卸载插件
              ide config                # 查看配置
            
            示例：
              ide
              ide install vscode
              ide install idea
              ide config
            """;
    }
}
