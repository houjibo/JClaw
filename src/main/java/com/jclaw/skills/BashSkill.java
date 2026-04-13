package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Bash 命令执行技能
 */
@Slf4j
@Service
public class BashSkill implements Skill {
    
    private static final List<String> BLOCKED_COMMANDS = List.of(
        "rm -rf /", "rm -rf /*", "mkfs", "dd if=/dev/zero",
        ":(){:|:&};:", "chmod -R 777 /", "chown -R"
    );
    
    @Override
    public String getName() {
        return "bash";
    }
    
    @Override
    public String getDescription() {
        return "执行 Bash 命令";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String command = (String) params.get("command");
            Integer timeout = (Integer) params.getOrDefault("timeout", 30); // 默认 30 秒超时
            
            if (command == null || command.isEmpty()) {
                return SkillResult.error("缺少参数：command");
            }
            
            // 安全检查：阻止危险命令
            for (String blocked : BLOCKED_COMMANDS) {
                if (command.contains(blocked)) {
                    return SkillResult.error("禁止执行危险命令");
                }
            }
            
            log.info("执行命令：{}", command);
            
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // 等待完成（带超时）
            if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return SkillResult.error("命令执行超时（" + timeout + "秒）");
            }
            
            int exitCode = process.exitValue();
            
            log.info("命令执行完成，退出码：{}", exitCode);
            
            if (exitCode == 0) {
                return SkillResult.success(output.toString(), Map.of(
                    "exitCode", exitCode,
                    "command", command
                ));
            } else {
                return SkillResult.builder()
                    .success(false)
                    .content(output.toString())
                    .error("命令执行失败，退出码：" + exitCode)
                    .data(Map.of("exitCode", exitCode))
                    .build();
            }
            
        } catch (IOException e) {
            log.error("执行命令失败", e);
            return SkillResult.error("执行失败：" + e.getMessage());
        } catch (InterruptedException e) {
            log.error("命令执行被中断", e);
            Thread.currentThread().interrupt();
            return SkillResult.error("命令执行被中断");
        } catch (Exception e) {
            log.error("执行技能失败", e);
            return SkillResult.error("执行失败：" + e.getMessage());
        }
    }
}
