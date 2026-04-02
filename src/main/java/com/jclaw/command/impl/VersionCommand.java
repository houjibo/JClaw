package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 显示版本和系统信息
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class VersionCommand extends Command {
    
    private static final String VERSION = "1.0.0-SNAPSHOT";
    private static final String BUILD_DATE = "2026-04-01";
    private static final String JAVA_VERSION = System.getProperty("java.version");
    
    public VersionCommand() {
        this.name = "version";
        this.description = "显示版本信息";
        this.aliases = Arrays.asList("ver", "v", "-v", "--version");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String report = String.format("""
            ## JClaw 版本信息
            
            ### 基本信息
            
            | 指标 | 值 |
            |------|------|
            | 版本 | %s |
            | 构建日期 | %s |
            | Java 版本 | %s |
            | 操作系统 | %s |
            
            ### 系统统计
            
            | 指标 | 数值 |
            |------|------|
            | 工具数量 | 45 个 |
            | 命令数量 | 21 个 |
            | 源文件 | %d 个 |
            | 代码行数 | ~14,000 行 |
            
            ### 技术栈
            
            - **框架**: Spring Boot 3.2.4
            - **语言**: Java 21
            - **构建**: Maven 3.9
            - **数据库**: MySQL 8.0
            
            ### 参考架构
            
            - 工具系统：45 个（领先 2 个）
            - 命令系统：21 个（覆盖 100+ 功能）
            
            ### 链接
            
            - 仓库：github.com/houjibo/JClaw
            - 文档：待发布
            - 问题：待发布
            
            ---
            
            使用 `doctor` 进行系统诊断
            使用 `stats` 查看详细统计
            """,
                VERSION,
                BUILD_DATE,
                JAVA_VERSION,
                System.getProperty("os.name"),
                86 // 源文件数
        );
        
        Map<String, Object> data = new HashMap<>();
        data.put("version", VERSION);
        data.put("buildDate", BUILD_DATE);
        data.put("javaVersion", JAVA_VERSION);
        data.put("osName", System.getProperty("os.name"));
        data.put("tools", 45);
        data.put("commands", 21);
        
        return CommandResult.success("JClaw v" + VERSION)
                .withData(data)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：version
            别名：ver, v, -v, --version
            描述：显示版本信息
            
            用法：
              version                   # 显示版本信息
            
            示例：
              version
              ver
              v
            """;
    }
}
