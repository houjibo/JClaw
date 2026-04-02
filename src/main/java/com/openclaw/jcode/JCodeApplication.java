package com.openclaw.jcode;

import com.openclaw.jcode.command.*;
import com.openclaw.jcode.core.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JClaw 编码智能体 - 主应用入口
 * 
 * - MCP 协议支持
 * - WebSocket 实时通信
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@SpringBootApplication
public class JClawApplication {

    public static void main(String[] args) {
        SpringApplication.run(JClawApplication.class, args);
    }

    /**
     * 应用启动后自动注册所有工具和命令
     */
    @Bean
    public CommandLineRunner init(ToolRegistry toolRegistry, List<Tool> tools,
                                   CommandRegistry commandRegistry, List<Command> commands) {
        return args -> {
            System.out.println("========================================");
            System.out.println("  JClaw 编码智能体启动");
            System.out.println("========================================");
            
            // 注册工具
            toolRegistry.registerAll(tools);
            System.out.println("✓ 已注册 " + toolRegistry.size() + " 个工具");
            
            // 注册命令
            commandRegistry.registerAll(commands);
            System.out.println("✓ 已注册 " + commandRegistry.getCommandNames().size() + " 个命令");
            
            System.out.println("========================================");
            printStats(toolRegistry, commandRegistry);
            System.out.println("========================================");
        };
    }
    
    /**
     * 打印统计信息
     */
    private void printStats(ToolRegistry toolRegistry, CommandRegistry commandRegistry) {
        System.out.println("\n📊 功能统计:");
        System.out.println("  工具：" + toolRegistry.size() + " 个");
        System.out.println("  命令：" + commandRegistry.getCommandNames().size() + " 个");
        
        System.out.println("\n🔧 工具类别:");
        for (ToolCategory cat : ToolCategory.values()) {
            long count = toolRegistry.listTools().stream()
                    .filter(t -> t.getCategory() == cat).count();
            if (count > 0) {
                System.out.println("  " + cat + ": " + count + " 个");
            }
        }
        
        System.out.println("\n📋 命令类别:");
        for (Command.CommandCategory cat : Command.CommandCategory.values()) {
            int count = commandRegistry.listCommandsByCategory(cat).size();
            if (count > 0) {
                System.out.println("  " + cat + ": " + count + " 个");
            }
        }
    }
}
