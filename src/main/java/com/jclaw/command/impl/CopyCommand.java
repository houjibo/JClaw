package com.jclaw.command.impl;

import com.jclaw.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 复制内容到剪贴板
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class CopyCommand extends Command {
    
    private static String clipboardContent = "";
    
    public CopyCommand() {
        this.name = "copy";
        this.description = "复制内容到剪贴板";
        this.aliases = Arrays.asList("cp", "clipboard");
        this.category = CommandCategory.SYSTEM;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showClipboard();
        }
        
        if ("clear".equals(args.trim())) {
            return clearClipboard();
        }
        
        return copyToClipboard(args);
    }
    
    private CommandResult showClipboard() {
        String report = """
            ## 剪贴板
            
            ### 当前内容
            
            ```
            (剪贴板为空或内容过长)
            ```
            
            ### 用法
            
            ```
            copy <文本>        # 复制文本
            copy clear         # 清空剪贴板
            copy               # 查看剪贴板
            ```
            
            ### 示例
            
            ```
            copy Hello World
            copy clear
            ```
            """;
        
        Map<String, Object> data = new HashMap<>();
        data.put("content", clipboardContent);
        data.put("length", clipboardContent.length());
        
        return CommandResult.success("剪贴板内容")
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult copyToClipboard(String content) {
        clipboardContent = content;
        
        String preview = content.length() > 50 
                ? content.substring(0, 50) + "..." 
                : content;
        
        String report = String.format("""
            ## 复制成功
            
            ### 内容预览
            
            ```
            %s
            ```
            
            ### 统计
            
            - 字符数：%d
            - 单词数：%d
            - 行数：%d
            
            内容已复制到剪贴板
            """,
                preview,
                content.length(),
                content.split("\\s+").length,
                content.split("\n").length
        );
        
        Map<String, Object> data = new HashMap<>();
        data.put("copied", true);
        data.put("length", content.length());
        
        return CommandResult.success("已复制到剪贴板")
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult clearClipboard() {
        clipboardContent = "";
        return CommandResult.success("剪贴板已清空")
                .withDisplayText("✅ 剪贴板已清空");
    }
    
    @Override
    public String getHelp() {
        return """
            命令：copy
            别名：cp, clipboard
            描述：复制内容到剪贴板
            
            用法：
              copy <文本>           # 复制文本
              copy clear            # 清空剪贴板
              copy                  # 查看剪贴板
            
            示例：
              copy Hello World
              copy clear
            """;
    }
}
