package com.jclaw.tools;

import com.jclaw.core.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 睡眠/延时工具
 */
@Component
public class SleepTool extends Tool {
    
    public SleepTool() {
        this.name = "sleep";
        this.description = "延时执行，等待指定时间";
        this.category = ToolCategory.SYSTEM;
        this.requiresConfirmation = false;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        Number seconds = (Number) params.get("seconds");
        Number milliseconds = (Number) params.get("milliseconds");
        
        long sleepMs = 0;
        if (seconds != null) sleepMs += seconds.longValue() * 1000;
        if (milliseconds != null) sleepMs += milliseconds.longValue();
        
        if (sleepMs <= 0) {
            return ToolResult.error("需要指定 seconds 或 milliseconds");
        }
        
        if (sleepMs > 300000) {
            return ToolResult.error("最大延时时间为 300 秒");
        }
        
        try {
            System.out.println("[SleepTool] 延时 " + sleepMs + "ms");
            Thread.sleep(sleepMs);
            return ToolResult.success("延时完成", "已等待 " + sleepMs + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("延时被中断");
        }
    }
    
    @Override
    public boolean validate(Map<String, Object> params) {
        return params.containsKey("seconds") || params.containsKey("milliseconds");
    }
    
    @Override
    public String getHelp() {
        return """
               %s - %s
               参数:
                 seconds      - 延时秒数 (可选)
                 milliseconds - 延时毫秒数 (可选)
               示例:
                 sleep seconds=5
                 sleep milliseconds=500
               """.formatted(name, description);
    }
}
