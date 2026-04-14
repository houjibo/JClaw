package com.jclaw.tools;

import com.jclaw.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Sleep 工具 - 延时执行
 * 
 * 功能：
 * - 延时指定时间
 * - 支持毫秒/秒/分钟单位
 * - 可用于测试和流程控制
 * 
 * @author JClaw
 * @since 2026-04-14
 */
@Slf4j
@Component
public class SleepTool extends Tool {
    
    public SleepTool() {
        this.name = "sleep";
        this.description = "延时执行，支持毫秒/秒/分钟单位";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        Integer duration = null;
        Object durationObj = params.get("duration");
        if (durationObj instanceof Integer) {
            duration = (Integer) durationObj;
        } else if (durationObj instanceof Number) {
            duration = ((Number) durationObj).intValue();
        }
        
        if (duration == null || duration <= 0) {
            return ToolResult.error("duration 参数必须是正整数");
        }
        
        String unit = (String) params.get("unit");
        if (unit == null || unit.isBlank()) {
            unit = "ms"; // 默认毫秒
        }
        
        long sleepMs = switch (unit.toLowerCase()) {
            case "ms", "millisecond", "milliseconds" -> duration;
            case "s", "sec", "second", "seconds" -> duration * 1000L;
            case "m", "min", "minute", "minutes" -> duration * 60000L;
            default -> {
                log.warn("不支持的时间单位：{}，使用默认毫秒", unit);
                yield duration;
            }
        };
        
        if (sleepMs > 300000) { // 最大 5 分钟
            return ToolResult.error("延时时间不能超过 5 分钟 (300000ms)");
        }
        
        try {
            log.info("延时 {} {}", duration, unit);
            Thread.sleep(sleepMs);
            return ToolResult.success(String.format("延时完成：%d %s", duration, unit));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("延时被中断：" + e.getMessage());
        }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("duration");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 duration - 延时时长 (必填，正整数)
                 unit - 时间单位：ms(默认), s, m (可选)
               示例:
                 sleep duration=1000
                 sleep duration=5 unit=s
                 sleep duration=1 unit=m
               """.formatted(name, description);
    }
}
