package com.openclaw.jcode.tools;

import com.openclaw.jcode.core.*;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 */
@Component
public class BashTool extends Tool {
    
    public BashTool() {
        this.name = "bash";
        this.description = "执行 Bash 命令";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = true;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        String command = (String) params.get("command");
        Number timeoutSec = (Number) params.getOrDefault("timeout", 60);
        String workdir = (String) params.get("workdir");
        
        if (command == null || command.isBlank()) {
            return ToolResult.error("command 参数不能为空");
        }
        
        if (!context.isAllowExec()) {
            return ToolResult.error("命令执行未被允许");
        }
        
        // 安全检查：阻止危险命令
        if (!isSafeCommand(command)) {
            return ToolResult.error("命令包含不安全操作：" + command);
        }
        
        try {
            Path workingDir = workdir != null 
                    ? context.getWorkingDirectory().resolve(workdir)
                    : context.getWorkingDirectory();
            
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.directory(workingDir.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            if (!process.waitFor(timeoutSec.intValue(), TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return ToolResult.error("命令执行超时 (" + timeoutSec + "秒)");
            }
            
            int exitCode = process.exitValue();
            System.out.println("[BashTool] 命令执行完成：" + command + " (exit=" + exitCode + ")");
            
            String result = output.toString().trim();
            if (exitCode == 0) {
                return ToolResult.success("命令执行成功", result);
            } else {
                return ToolResult.error("命令执行失败 (exit=" + exitCode + "): " + result);
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("[BashTool] 命令执行失败：" + command + " - " + e.getMessage());
            Thread.currentThread().interrupt();
            return ToolResult.error("命令执行失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查命令是否安全
     */
    private boolean isSafeCommand(String command) {
        String lower = command.toLowerCase();
        
        // 阻止危险命令
        String[] dangerous = {"rm -rf /", "mkfs", "dd if=", ":(){:|:&};:"};
        for (String dangerousCmd : dangerous) {
            if (lower.contains(dangerousCmd)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("command");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 command  - Bash 命令 (必填)
                 timeout  - 超时时间 (秒),默认 60 (可选)
                 workdir  - 工作目录，相对路径 (可选)
               示例:
                 bash command="ls -la"
                 bash command="git status" workdir="my-project"
               """.formatted(name, description);
    }
}
