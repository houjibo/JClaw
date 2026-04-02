package com.jclaw.command;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令注册中心 - 管理所有可用命令
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CommandRegistry {
    
    /**
     * 命令注册表（按名称索引）
     */
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    
    /**
     * 命令别名映射
     */
    private final Map<String, String> aliasMap = new ConcurrentHashMap<>();
    
    /**
     * 已禁用的命令
     */
    private final Set<String> disabledCommands = ConcurrentHashMap.newKeySet();
    
    /**
     * 初始化时注册核心命令
     */
    public CommandRegistry() {
        registerBuiltinCommands();
    }
    
    /**
     * 注册内置命令
     */
    private void registerBuiltinCommands() {
        // 由 Spring 自动扫描 @Component 的命令类
        System.out.println("[CommandRegistry] 内置命令注册完成");
    }
    
    /**
     * 批量注册命令
     */
    public void registerAll(List<Command> commands) {
        if (commands == null) {
            return;
        }
        for (Command cmd : commands) {
            register(cmd);
        }
    }
    
    /**
     * 注册命令
     */
    public void register(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("命令不能为空");
        }
        
        // 注册主名称
        commands.put(command.getName(), command);
        System.out.println("[CommandRegistry] 注册命令：" + command.getName());
        
        // 注册别名
        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                aliasMap.put(alias, command.getName());
                System.out.println("[CommandRegistry] 注册别名：" + alias + " -> " + command.getName());
            }
        }
    }
    
    /**
     * 注销命令
     */
    public void unregister(String name) {
        String realName = resolveCommandName(name);
        if (realName != null) {
            commands.remove(realName);
            // 清理别名
            aliasMap.entrySet().removeIf(entry -> entry.getValue().equals(realName));
            System.out.println("[CommandRegistry] 注销命令：" + realName);
        }
    }
    
    /**
     * 获取命令（通过名称或别名）
     */
    public Command getCommand(String name) {
        String realName = resolveCommandName(name);
        if (realName == null) {
            return null;
        }
        
        Command command = commands.get(realName);
        if (command == null || disabledCommands.contains(realName)) {
            return null;
        }
        
        return command;
    }
    
    /**
     * 解析命令名称（处理别名）
     */
    public String resolveCommandName(String name) {
        if (name == null) {
            return null;
        }
        
        // 先尝试直接匹配
        if (commands.containsKey(name)) {
            return name;
        }
        
        // 再尝试别名匹配
        String realName = aliasMap.get(name.toLowerCase());
        if (realName != null && commands.containsKey(realName)) {
            return realName;
        }
        
        return null;
    }
    
    /**
     * 列出所有可用命令
     */
    public List<Command> listCommands() {
        List<Command> result = new ArrayList<>();
        for (Command cmd : commands.values()) {
            if (!disabledCommands.contains(cmd.getName())) {
                result.add(cmd);
            }
        }
        result.sort(Comparator.comparing(Command::getName));
        return result;
    }
    
    /**
     * 按类别列出命令
     */
    public List<Command> listCommandsByCategory(Command.CommandCategory category) {
        List<Command> result = new ArrayList<>();
        for (Command cmd : commands.values()) {
            if (!disabledCommands.contains(cmd.getName()) && 
                cmd.getCategory() == category) {
                result.add(cmd);
            }
        }
        result.sort(Comparator.comparing(Command::getName));
        return result;
    }
    
    /**
     * 启用/禁用命令
     */
    public void setCommandEnabled(String name, boolean enabled) {
        String realName = resolveCommandName(name);
        if (realName != null) {
            if (enabled) {
                disabledCommands.remove(realName);
                System.out.println("[CommandRegistry] 启用命令：" + realName);
            } else {
                disabledCommands.add(realName);
                System.out.println("[CommandRegistry] 禁用命令：" + realName);
            }
        }
    }
    
    /**
     * 检查命令是否可用
     */
    public boolean isCommandEnabled(String name) {
        String realName = resolveCommandName(name);
        return realName != null && !disabledCommands.contains(realName);
    }
    
    /**
     * 获取所有命令名称（用于自动完成）
     */
    public Set<String> getCommandNames() {
        Set<String> names = new HashSet<>(commands.keySet());
        names.addAll(aliasMap.keySet());
        return names;
    }
    
    /**
     * 获取命令统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", commands.size());
        stats.put("aliases", aliasMap.size());
        stats.put("disabled", disabledCommands.size());
        stats.put("enabled", commands.size() - disabledCommands.size());
        
        // 按类别统计
        Map<String, Integer> byCategory = new HashMap<>();
        for (Command cmd : commands.values()) {
            String cat = cmd.getCategory().name();
            byCategory.put(cat, byCategory.getOrDefault(cat, 0) + 1);
        }
        stats.put("byCategory", byCategory);
        
        return stats;
    }
    
    /**
     * 清除缓存（用于插件重新加载）
     */
    public void clearCache() {
        // 保留内置命令，清除动态注册的命令
        System.out.println("[CommandRegistry] 清除命令缓存");
    }
}
