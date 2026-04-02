package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 支持：list, install, uninstall, reload 等
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class PluginCommand extends Command {
    
    // 模拟插件
    private static final Map<String, Map<String, Object>> PLUGINS = new HashMap<>();
    static {
        Map<String, Object> plugin1 = new HashMap<>();
        plugin1.put("name", "git-integration");
        plugin1.put("version", "1.0.0");
        plugin1.put("status", "enabled");
        plugin1.put("commands", 5);
        PLUGINS.put("git-integration", plugin1);
        
        Map<String, Object> plugin2 = new HashMap<>();
        plugin2.put("name", "docker-tools");
        plugin2.put("version", "2.1.0");
        plugin2.put("status", "enabled");
        plugin2.put("commands", 8);
        PLUGINS.put("docker-tools", plugin2);
    }
    
    public PluginCommand() {
        this.name = "plugin";
        this.description = "插件管理（列表、安装、卸载、重载）";
        this.aliases = Arrays.asList("plugins", "plug", "p");
        this.category = CommandCategory.PLUGIN;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("action", new CommandParameter("action", 
            "操作类型 (list, install, uninstall, enable, disable, reload)", true)
            .choices("list", "install", "uninstall", "enable", "disable", "reload"));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
            return listPlugins();
        }
        
        String action = parts[0];
        String pluginName = parts.length > 1 ? parts[1] : null;
        
        return switch (action) {
            case "list" -> listPlugins();
            case "install" -> installPlugin(pluginName);
            case "uninstall" -> uninstallPlugin(pluginName);
            case "enable" -> enablePlugin(pluginName);
            case "disable" -> disablePlugin(pluginName);
            case "reload" -> reloadPlugins();
            default -> CommandResult.error("未知操作：" + action);
        };
    }
    
    private CommandResult listPlugins() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 插件列表\n\n");
        sb.append("| 名称 | 版本 | 状态 | 命令数 |\n");
        sb.append("|------|------|------|--------|\n");
        
        for (Map<String, Object> plugin : PLUGINS.values()) {
            sb.append(String.format("| %s | %s | %s | %d |\n",
                    plugin.get("name"),
                    plugin.get("version"),
                    plugin.get("status"),
                    plugin.get("commands")));
        }
        
        return CommandResult.success("插件列表")
                .withData("plugins", new ArrayList<>(PLUGINS.values()))
                .withDisplayText(sb.toString());
    }
    
    private CommandResult installPlugin(String name) {
        if (name == null) {
            return CommandResult.error("请指定插件名称");
        }
        
        if (PLUGINS.containsKey(name)) {
            return CommandResult.error("插件已存在：" + name);
        }
        
        Map<String, Object> plugin = new HashMap<>();
        plugin.put("name", name);
        plugin.put("version", "1.0.0");
        plugin.put("status", "enabled");
        plugin.put("commands", 0);
        
        PLUGINS.put(name, plugin);
        
        return CommandResult.success("插件已安装：" + name)
                .withData("plugin", plugin);
    }
    
    private CommandResult uninstallPlugin(String name) {
        if (name == null) {
            return CommandResult.error("请指定插件名称");
        }
        
        if (PLUGINS.remove(name) == null) {
            return CommandResult.error("插件不存在：" + name);
        }
        
        return CommandResult.success("插件已卸载：" + name);
    }
    
    private CommandResult enablePlugin(String name) {
        if (name == null) {
            return CommandResult.error("请指定插件名称");
        }
        
        Map<String, Object> plugin = PLUGINS.get(name);
        if (plugin == null) {
            return CommandResult.error("插件不存在：" + name);
        }
        
        plugin.put("status", "enabled");
        
        return CommandResult.success("插件已启用：" + name);
    }
    
    private CommandResult disablePlugin(String name) {
        if (name == null) {
            return CommandResult.error("请指定插件名称");
        }
        
        Map<String, Object> plugin = PLUGINS.get(name);
        if (plugin == null) {
            return CommandResult.error("插件不存在：" + name);
        }
        
        plugin.put("status", "disabled");
        
        return CommandResult.success("插件已禁用：" + name);
    }
    
    private CommandResult reloadPlugins() {
        int count = PLUGINS.size();
        
        // 模拟重载
        for (Map<String, Object> plugin : PLUGINS.values()) {
            plugin.put("status", "enabled");
        }
        
        return CommandResult.success("已重载 " + count + " 个插件");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：plugin
            别名：plugins, plug, p
            描述：插件管理
            
            用法：
              plugin list                     # 列出所有插件
              plugin install <name>           # 安装插件
              plugin uninstall <name>         # 卸载插件
              plugin enable <name>            # 启用插件
              plugin disable <name>           # 禁用插件
              plugin reload                   # 重载所有插件
            
            示例：
              plugin list
              plugin install myplugin
              plugin enable myplugin
              plugin disable myplugin
              plugin reload
            """;
    }
}
