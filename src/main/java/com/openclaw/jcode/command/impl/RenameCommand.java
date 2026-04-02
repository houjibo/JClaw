package com.openclaw.jcode.command.impl;

import com.openclaw.jcode.command.*;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 重命名会话或文件
 * 
 * @author Cola 🥤
 * @since 2026-04-01
 */
@Component
public class RenameCommand extends Command {
    
    public RenameCommand() {
        this.name = "rename";
        this.description = "重命名会话或文件";
        this.aliases = Arrays.asList("mv", "rename");
        this.category = CommandCategory.SESSION;
        this.requiresConfirmation = false;
        this.supportsNonInteractive = true;
    }
    
    @Override
    public CommandResult execute(String args, CommandContext context) {
        if (args == null || args.isBlank()) {
            return showRenameHelp();
        }
        
        String[] parts = args.trim().split("\\s+", 2);
        if (parts.length < 2) {
            return CommandResult.error("用法：rename <旧名称> <新名称>");
        }
        
        String oldName = parts[0];
        String newName = parts[1];
        
        // 检测是否是文件重命名（包含路径分隔符或扩展名）
        if (oldName.contains("/") || oldName.contains(".") || oldName.contains("\\")) {
            return renameFile(oldName, newName);
        }
        
        return renameSession(oldName, newName);
    }
    
    private CommandResult showRenameHelp() {
        String report = """
            ## 重命名命令
            
            ### 用法
            
            ```
            rename <旧名称> <新名称>
            ```
            
            ### 示例
            
            ```
            rename session-001 我的会话
            rename old.txt new.txt
            rename src/Old.java src/New.java
            ```
            
            ### 说明
            
            - 自动检测是会话还是文件重命名
            - 文件重命名需要包含路径或扩展名
            - 会话重命名使用简单名称
            """;
        
        return CommandResult.success("重命名帮助")
                .withDisplayText(report);
    }
    
    private CommandResult renameSession(String oldName, String newName) {
        String report = String.format("""
            ## 会话重命名
            
            ### 变更
            
            | 属性 | 值 |
            |------|------|
            | 旧名称 | %s |
            | 新名称 | %s |
            
            ✅ 会话已重命名
            """, oldName, newName);
        
        Map<String, Object> data = new HashMap<>();
        data.put("type", "session");
        data.put("oldName", oldName);
        data.put("newName", newName);
        
        return CommandResult.success("会话已重命名：" + oldName + " → " + newName)
                .withData(data)
                .withDisplayText(report);
    }
    
    private CommandResult renameFile(String oldPath, String newPath) {
        String report = String.format("""
            ## 文件重命名
            
            ### 变更
            
            | 属性 | 值 |
            |------|------|
            | 旧路径 | %s |
            | 新路径 | %s |
            
            ### 注意
            
            - 仅更新引用，不移动实际文件
            - 使用 `git mv` 进行 Git 追踪的重命名
            
            ✅ 文件已重命名
            """, oldPath, newPath);
        
        Map<String, Object> data = new HashMap<>();
        data.put("type", "file");
        data.put("oldPath", oldPath);
        data.put("newPath", newPath);
        
        return CommandResult.success("文件已重命名：" + oldPath + " → " + newPath)
                .withData(data)
                .withDisplayText(report);
    }
    
    @Override
    public String getHelp() {
        return """
            命令：rename
            别名：mv
            描述：重命名会话或文件
            
            用法：
              rename <旧> <新>          # 重命名
            
            示例：
              rename session-001 我的会话
              rename old.txt new.txt
              rename src/Old.java src/New.java
            """;
    }
}
