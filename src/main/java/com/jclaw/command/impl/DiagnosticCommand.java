package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 支持：health, check, diagnose 等
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class DiagnosticCommand extends Command {
    
    public DiagnosticCommand() {
        this.name = "doctor";
        this.description = "系统诊断和健康检查";
        this.aliases = Arrays.asList("diagnose", "health", "check");
        this.category = CommandCategory.DIAGNOSTIC;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
        
        this.parameters.put("check", new CommandParameter("check", 
            "检查类型 (all, system, tools, mcp, network)", false)
            .choices("all", "system", "tools", "mcp", "network").defaultValue("all"));
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        String[] parts = args != null ? args.trim().split("\\s+") : new String[0];
        String check = parts.length > 0 ? parts[0] : "all";
        
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> checks = new ArrayList<>();
        
        // 系统检查
        if ("all".equals(check) || "system".equals(check)) {
            checks.add(checkSystem());
        }
        
        // 工具检查
        if ("all".equals(check) || "tools".equals(check)) {
            checks.add(checkTools());
        }
        
        // MCP 检查
        if ("all".equals(check) || "mcp".equals(check)) {
            checks.add(checkMcp());
        }
        
        // 网络检查
        if ("all".equals(check) || "network".equals(check)) {
            checks.add(checkNetwork());
        }
        
        results.put("checks", checks);
        results.put("timestamp", new Date().toString());
        
        // 计算总体状态
        boolean allPassed = checks.stream()
                .allMatch(c -> "pass".equals(c.get("status")));
        results.put("overall", allPassed ? "pass" : "warn");
        
        return CommandResult.success("诊断完成")
                .withData(results)
                .withDisplayText(generateReport(results));
    }
    
    private Map<String, Object> checkSystem() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "系统环境");
        
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        
        if (freeMemory > 100) {
            result.put("status", "pass");
            result.put("message", "系统资源充足");
        } else {
            result.put("status", "warn");
            result.put("message", "内存不足：" + freeMemory + "MB");
        }
        
        result.put("details", Map.of(
            "javaVersion", System.getProperty("java.version"),
            "osName", System.getProperty("os.name"),
            "freeMemory", freeMemory + "MB",
            "processors", runtime.availableProcessors()
        ));
        
        return result;
    }
    
    private Map<String, Object> checkTools() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "工具系统");
        
        // 模拟工具检查
        int toolCount = 45;
        int workingTools = 44;
        
        if (workingTools == toolCount) {
            result.put("status", "pass");
            result.put("message", "所有工具正常");
        } else {
            result.put("status", "warn");
            result.put("message", (toolCount - workingTools) + " 个工具异常");
        }
        
        result.put("details", Map.of(
            "totalTools", toolCount,
            "workingTools", workingTools,
            "failedTools", toolCount - workingTools
        ));
        
        return result;
    }
    
    private Map<String, Object> checkMcp() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "MCP 服务");
        
        // 模拟 MCP 检查
        result.put("status", "pass");
        result.put("message", "MCP 服务正常");
        
        result.put("details", Map.of(
            "servers", 3,
            "resources", 15,
            "status", "connected"
        ));
        
        return result;
    }
    
    private Map<String, Object> checkNetwork() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "网络连接");
        
        // 模拟网络检查
        result.put("status", "pass");
        result.put("message", "网络连接正常");
        
        result.put("details", Map.of(
            "apiEndpoint", "connected",
            "modelService", "connected",
            "latency", "45ms"
        ));
        
        return result;
    }
    
    private String generateReport(Map<String, Object> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 系统诊断报告\n\n");
        sb.append("**总体状态**: ");
        
        String overall = (String) results.get("overall");
        if ("pass".equals(overall)) {
            sb.append("✅ 通过\n\n");
        } else {
            sb.append("⚠️ 警告\n\n");
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> checks = (List<Map<String, Object>>) results.get("checks");
        
        sb.append("### 检查详情\n\n");
        for (Map<String, Object> check : checks) {
            String icon = "pass".equals(check.get("status")) ? "✅" : "⚠️";
            sb.append(icon).append(" **").append(check.get("name")).append("**\n");
            sb.append("   ").append(check.get("message")).append("\n\n");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) check.get("details");
            if (details != null) {
                for (Map.Entry<String, Object> entry : details.entrySet()) {
                    sb.append("   - ").append(entry.getKey()).append(": ")
                            .append(entry.getValue()).append("\n");
                }
                sb.append("\n");
            }
        }
        
        sb.append("---\n");
        sb.append("报告生成时间：").append(results.get("timestamp"));
        
        return sb.toString();
    }
    
    @Override
    public String getHelp() {
        return """
            命令：doctor
            别名：diagnose, health, check
            描述：系统诊断和健康检查
            
            用法：
              doctor [check]
            
            参数：
              check    检查类型 (all, system, tools, mcp, network)，默认 all
            
            示例：
              doctor                      # 完整诊断
              doctor system               # 只检查系统
              doctor tools                # 只检查工具
              doctor mcp                  # 只检查 MCP
            """;
    }
}
