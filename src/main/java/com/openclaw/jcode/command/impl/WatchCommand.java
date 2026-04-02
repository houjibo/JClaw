package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Watch 命令 - 文件变动监听
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class WatchCommand extends Command {
    
    private ExecutorService executor;
    private volatile boolean watching = false;
    
    public WatchCommand() {
        this.name = "watch";
        this.description = "文件变动监听";
        this.aliases = Arrays.asList("monitor");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = (args == null || args.isBlank()) ? new String[0] : args.trim().split("\\s+");
        
        if (parts.length == 0) {
            return showHelp();
        }
        
        String action = parts[0];
        
        return switch (action) {
            case "start", "begin" -> startWatch(parts.length > 1 ? parts[1] : ".", parts.length > 2 ? parts[2] : null);
            case "stop" -> stopWatch();
            case "status" -> watchStatus();
            case "info" -> showHelp();
            default -> startWatch(parts[0], parts.length > 1 ? parts[1] : null);
        };
    }
    
    private CommandResult startWatch(String path, String command) {
        if (watching) {
            return CommandResult.error("监听已在运行中");
        }
        
        Path watchPath = Paths.get(path);
        
        if (!Files.exists(watchPath)) {
            return CommandResult.error("路径不存在：" + path);
        }
        
        watching = true;
        
        executor.submit(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                watchPath.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
                
                StringBuilder sb = new StringBuilder();
                sb.append("## 文件监听已启动\n\n");
                sb.append(String.format("**路径**: %s\n", path));
                sb.append(String.format("**命令**: %s\n\n", command != null ? command : "无"));
                sb.append("监听中...\n\n");
                
                while (watching) {
                    WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                    if (key == null) continue;
                    
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path changed = (Path) pathEvent.context();
                        
                        sb.append(String.format("[%s] %s %s\n",
                                new Date(),
                                event.kind().name(),
                                changed));
                        
                        // 如果指定了命令，执行它
                        if (command != null) {
                            try {
                                ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
                                pb.start();
                            } catch (IOException e) {
                                // 忽略
                            }
                        }
                    }
                    key.reset();
                }
                
                watchService.close();
                
            } catch (Exception e) {
                // 忽略
            }
        });
        
        String report = String.format("""
            ## 文件监听已启动
            
            **路径**: %s
            **命令**: %s
            
            ### 监听事件
            
            - CREATE - 文件创建
            - DELETE - 文件删除
            - MODIFY - 文件修改
            
            ### 停止监听
            
            ```
            watch stop
            ```
            
            ### 查看状态
            
            ```
            watch status
            ```
            
            监听运行中...
            """, path, command != null ? command : "无");
        
        return CommandResult.success("监听已启动")
                .withDisplayText(report);
    }
    
    private CommandResult stopWatch() {
        if (!watching) {
            return CommandResult.error("监听未运行");
        }
        
        watching = false;
        
        return CommandResult.success("监听已停止")
                .withDisplayText("✅ 文件监听已停止");
    }
    
    private CommandResult watchStatus() {
        String status = watching ? "🟢 运行中" : "⚪ 已停止";
        
        String report = String.format("""
            ## 监听状态
            
            **状态**: %s
            
            ### 配置
            
            | 属性 | 值 |
            |------|------|
            | 运行状态 | %s |
            | 线程池 | %s |
            
            ### 操作
            
            - `watch start <路径>` - 启动监听
            - `watch stop` - 停止监听
            """, status, watching ? "运行中" : "已停止", watching ? "活跃" : "空闲");
        
        return CommandResult.success("监听状态")
                .withDisplayText(report);
    }
    
    private CommandResult showHelp() {
        String report = """
            ## Watch 命令帮助
            
            ### 用法
            
            ```
            watch <路径> [命令]       # 启动监听
            watch start <路径> [命令] # 启动监听
            watch stop                # 停止监听
            watch status              # 查看状态
            ```
            
            ### 参数
            
            - 路径：监听的目录（默认：当前目录）
            - 命令：文件变动时执行的命令（可选）
            
            ### 监听事件
            
            | 事件 | 说明 |
            |------|------|
            | CREATE | 文件创建 |
            | DELETE | 文件删除 |
            | MODIFY | 文件修改 |
            
            ### 使用场景
            
            1. **自动编译**
               ```
               watch src "mvn compile"
               ```
            
            2. **自动部署**
               ```
               watch dist "scp -r dist/* server:/var/www"
               ```
            
            3. **自动测试**
               ```
               watch test "npm test"
               ```
            
            ### 示例
            
            ```
            watch .                    # 监听当前目录
            watch src "make build"     # 监听并执行命令
            watch status               # 查看状态
            watch stop                 # 停止监听
            ```
            """;
        
        return CommandResult.success("Watch 帮助")
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：watch
            别名：monitor
            描述：文件变动监听
            
            用法：
              watch <路径> [命令]       # 启动监听
              watch stop                # 停止监听
              watch status              # 查看状态
            
            示例：
              watch .
              watch src "make build"
              watch status
            """;
    }
}
