package com.jclaw.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件编辑技能
 */
@Slf4j
@Service
public class FileEditSkill implements Skill {
    
    // 存储文件备份
    private final Map<String, String> fileBackups = new ConcurrentHashMap<>();
    
    @Override
    public String getName() {
        return "file_edit";
    }
    
    @Override
    public String getDescription() {
        return "编辑文件内容";
    }
    
    @Override
    public SkillResult execute(Map<String, Object> params) {
        try {
            String pathStr = (String) params.get("path");
            String operation = (String) params.get("operation");
            
            if (pathStr == null || pathStr.isEmpty()) {
                return SkillResult.error("缺少参数：path");
            }
            if (operation == null || operation.isEmpty()) {
                return SkillResult.error("缺少参数：operation (append/replace/delete)");
            }
            
            File file = new File(pathStr);
            
            // 安全检查
            String absPath = file.getAbsolutePath();
            if (absPath.startsWith("/etc/") || absPath.startsWith("/root/") || 
                absPath.startsWith("/proc/") || absPath.startsWith("/sys/")) {
                return SkillResult.error("禁止编辑系统文件");
            }
            
            if (!file.exists()) {
                return SkillResult.error("文件不存在：" + pathStr);
            }
            
            String content = java.nio.file.Files.readString(file.toPath());
            
            // 创建备份
            fileBackups.put(pathStr, content);
            
            switch (operation.toLowerCase()) {
                case "append":
                    String appendText = (String) params.get("text");
                    if (appendText == null) {
                        return SkillResult.error("缺少参数：text");
                    }
                    content += appendText;
                    break;
                    
                case "replace":
                    String searchText = (String) params.get("search");
                    String replaceText = (String) params.get("replace");
                    if (searchText == null || replaceText == null) {
                        return SkillResult.error("缺少参数：search 和 replace");
                    }
                    content = content.replace(searchText, replaceText);
                    break;
                    
                case "delete":
                    String deleteText = (String) params.get("text");
                    if (deleteText == null) {
                        return SkillResult.error("缺少参数：text");
                    }
                    content = content.replace(deleteText, "");
                    break;
                    
                default:
                    return SkillResult.error("未知操作：" + operation);
            }
            
            // 写回文件
            java.nio.file.Files.writeString(file.toPath(), content);
            
            log.info("编辑文件：{} 操作：{}", pathStr, operation);
            
            return SkillResult.success("文件编辑成功", Map.of(
                "path", pathStr,
                "operation", operation,
                "backupCreated", true
            ));
            
        } catch (Exception e) {
            log.error("文件编辑失败", e);
            return SkillResult.error("编辑失败：" + e.getMessage());
        }
    }
    
    /**
     * 恢复备份
     */
    public SkillResult restoreBackup(String pathStr) {
        String backup = fileBackups.get(pathStr);
        if (backup == null) {
            return SkillResult.error("没有备份：" + pathStr);
        }
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get(pathStr), backup);
            fileBackups.remove(pathStr);
            log.info("恢复文件备份：{}", pathStr);
            return SkillResult.success("文件已恢复");
        } catch (Exception e) {
            log.error("恢复备份失败", e);
            return SkillResult.error("恢复失败：" + e.getMessage());
        }
    }
}
